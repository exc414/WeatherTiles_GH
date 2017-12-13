package io.bluephoenix.weathertiles.core.data.repository;

import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.bluephoenix.weathertiles.core.common.EventDef;
import io.bluephoenix.weathertiles.core.common.EventDef.EventType;
import io.bluephoenix.weathertiles.core.common.TempScaleDef.TempScale;
import io.bluephoenix.weathertiles.core.data.model.bus.Events;
import io.bluephoenix.weathertiles.core.data.model.db.Cities;
import io.bluephoenix.weathertiles.core.data.model.db.SunriseSunset;
import io.bluephoenix.weathertiles.core.data.model.db.Tile;
import io.bluephoenix.weathertiles.core.data.model.db.TileDetail;
import io.bluephoenix.weathertiles.core.data.model.db.TimeZoneIds;
import io.bluephoenix.weathertiles.core.data.model.db.WeatherDetail;
import io.bluephoenix.weathertiles.util.Constant;
import io.bluephoenix.weathertiles.util.DayTime;
import io.bluephoenix.weathertiles.util.Util;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class WeatherRepository implements IRepository.Weather
{
    private final String CITY_ID_FIELD = "cityId";
    private final String SAVED_POSITION_FIELD = "savedPosition";
    private final String TIMESTAMP = "timestamp";
    private final String HAS_BEEN_USED = "hasBeenUsed";

    /**
     * Get all the tiles object that are currently in the database.
     */
    @Override
    public List<Tile> getAllTiles()
    {
        Realm realmInstance = Realm.getDefaultInstance();
        List<Tile> tiles;
        try
        {
            RealmQuery<Tile> tileQuery = realmInstance.where(Tile.class);
            tiles = realmInstance.copyFromRealm(
                    tileQuery.findAllSorted(SAVED_POSITION_FIELD, Sort.ASCENDING));
        }
        finally { if(realmInstance != null) { realmInstance.close(); } }
        return tiles;
    }

    /**
     * Add a tile to the realm database.
     * @param tile contains all weather information.
     */
    @Override
    public void createTile(Tile tile)
    {
        Realm realmInstance = Realm.getDefaultInstance();
        try
        {
            realmInstance.executeTransaction(realm ->
            {
                long cityId = tile.getCityId();
                SunriseSunset sunriseSunset = new SunriseSunset();
                String timezone = realm.where(TimeZoneIds.class)
                        .equalTo(CITY_ID_FIELD, cityId).findFirst().getTimezone();
                int preInsertTotalTiles = realm.where(Tile.class).findAll().size();
                tile.setSavedPosition(preInsertTotalTiles);
                //Always zero when creating. 0 = First Position.
                tile.setSelectedPosition(0);

                double lat = tile.getLat();
                double lon = tile.getLon();

                //Calculate new sunrise and sunset values
                boolean isDayTime = DayTime.isDayTime(lat, lon, timezone);
                tile.setIsDayTime(isDayTime);

                sunriseSunset.setCityId(cityId);
                sunriseSunset.setLat(lat);
                sunriseSunset.setLon(lon);
                sunriseSunset.setSunrise(DayTime.getSunrise());
                sunriseSunset.setSunset(DayTime.getSunset());

                realm.insert(sunriseSunset);
                realm.insert(tile);

                //Check that the tile was inserted successfully
                int postInsertTotalTiles = realm.where(Tile.class).findAll().size();
                if(postInsertTotalTiles > preInsertTotalTiles)
                { onTransactionResult(tile, EventDef.CREATED); }
                else { onTransactionResult(tile, EventDef.CREATE_ERROR); }
            });
        }
        catch(NullPointerException ex)
        {
            onTransactionResult(tile, EventDef.CREATE_ERROR);
        }
        finally { if(realmInstance != null) { realmInstance.close(); } }
    }

    /**
     * Add detail tiles.
     * @param tileDetailList a list containing tile details objects.
     */
    @Override
    public void createTileDetails(List<TileDetail> tileDetailList)
    {
        Realm realmInstance = Realm.getDefaultInstance();
        try
        {
            realmInstance.executeTransaction(realm ->
            {
                for(int i = 0; i < tileDetailList.size(); i++)
                {
                    Number id = realm.where(TileDetail.class).max("id");
                    int nextID;
                    if(id == null) { nextID = 1; }
                    else { nextID = id.intValue() + 1; }
                    tileDetailList.get(i).setId(nextID);

                    double rain = (tileDetailList.get(i).getRainFall3h() * 10);
                    rain = (rain > 0) ? rain + 1 : rain;
                    tileDetailList.get(i).setRainFall3h(rain);

                    realm.insert(tileDetailList.get(i));
                }
            });
        }
        catch(NullPointerException ex)
        {
            //Log error here.
        }
        finally { if(realmInstance != null) { realmInstance.close(); } }
    }

    /**
     * Update the tiles from the tiles details database in three hours intervals.
     * @param calcTimestamp a long to match against the timestamp in the tile detail db.
     */
    @Override
    public void updateTiles(long calcTimestamp, boolean onCallType)
    {
        List<Tile> tileList = new ArrayList<>();
        Realm realmInstance = Realm.getDefaultInstance();

        try
        {
            realmInstance.executeTransaction(realm ->
            {
                RealmQuery<Tile> tileQuery = realm.where(Tile.class);
                List<Tile> tiles = realm.copyFromRealm(tileQuery.findAll());

                for(int i = 0; i < tiles.size(); i++)
                {
                    TileDetail tileDetail = realm.where(TileDetail.class)
                            .equalTo(CITY_ID_FIELD, tiles.get(i).getCityId())
                            .notEqualTo(HAS_BEEN_USED, true)
                            .equalTo(TIMESTAMP, calcTimestamp).findFirst();

                    //Make sure a result was returned
                    if(tileDetail != null)
                    {
                        long cityId = tileDetail.getCityId();

                        SunriseSunset sunriseSunset = realm.where(SunriseSunset.class)
                                .equalTo(CITY_ID_FIELD, cityId).findFirst();

                        String timezone = realm.where(TimeZoneIds.class)
                                .equalTo(CITY_ID_FIELD, cityId).findFirst().getTimezone();

                        int weatherId = tileDetail.getWeatherId();
                        boolean isDayTime = DayTime.isDayTime(sunriseSunset, timezone);
                        tiles.get(i).setWeatherId(weatherId);
                        tiles.get(i).setIsDayTime(isDayTime);
                        tiles.get(i).setTemp(tileDetail.getTemperature());
                        tiles.get(i).setDescription(tileDetail.getDescription());
                        tiles.get(i).setHumidity(tileDetail.getHumidity());
                        tiles.get(i).setWind(tileDetail.getWind());

                        Log.i(Constant.TAG, "updateTiles - WeatherRepository - Tile Detail");

                        realm.insertOrUpdate(tiles.get(i));
                        tileDetail.setHasBeenUsed(true);
                        realm.insertOrUpdate(tileDetail);

                        if(!onCallType) { tileList.add(tiles.get(i)); }
                    }
                }

                //Send tiles to update adapter
                if(!onCallType && tileList.size() != 0)
                {
                    onTransactionResult(tileList, EventDef.UPDATED);
                }
            });
        }
        catch(Exception ex)
        {
            Log.i(Constant.TAG, "UpdateTiles could not complete, please check output.");
            ex.printStackTrace();
        }
        finally { if(realmInstance != null) { realmInstance.close(); } }
    }

    /**
     * Updates the tiles sunrise/sunset icon.
     */
    @Override
    public void updateTilesSunriseSunset()
    {
        List<Tile> tileList = new ArrayList<>();
        Realm realmInstance = Realm.getDefaultInstance();
        try
        {
            realmInstance.executeTransaction(realm ->
            {
                boolean isDayTime;
                RealmQuery<Tile> tileQuery = realm.where(Tile.class);
                List<Tile> tiles = realm.copyFromRealm(tileQuery.findAll());

                for(int i = 0; i < tiles.size(); i++)
                {
                    //Use ALREADY calculated sunrise/sunset values
                    isDayTime = isDayTime(realm, tiles.get(i).getCityId());

                    if(isDayTime && !tiles.get(i).getIsDayTime())
                    {
                        tiles.get(i).setIsDayTime(true);
                        tileList.add(tiles.get(i));
                        realm.insertOrUpdate(tiles.get(i));

                    }
                    else if(!isDayTime && tiles.get(i).getIsDayTime())
                    {
                        tiles.get(i).setIsDayTime(false);
                        tileList.add(tiles.get(i));
                        realm.insertOrUpdate(tiles.get(i));
                    }
                }

                if(tileList.size() != 0)
                {
                    onTransactionResult(tileList, EventDef.UPDATED);
                }
            });
        }
        catch(Exception ex)
        {
            Log.i(Constant.TAG, "UpdateTilesSunriseSunset could not complete,"
                    + " please check output.");
            ex.printStackTrace();
        }
        finally { if(realmInstance != null) { realmInstance.close(); } }
    }

    /**
     * Delete an existing tile or tiles.
     * @param tile an object with weather information.
     */
    @Override
    public void deleteTile(final Tile tile)
    {
        Realm realmInstance = Realm.getDefaultInstance();
        long cityId = tile.getCityId();
        try
        {
            realmInstance.executeTransactionAsync(realm ->
            {
                //Delete from tile table.
                RealmResults<Tile> tiles = realm.where(Tile.class)
                        .equalTo(CITY_ID_FIELD, cityId).findAll();
                tiles.deleteAllFromRealm();

                RealmResults<SunriseSunset> sunriseSunsets = realm.where(SunriseSunset.class)
                        .equalTo(CITY_ID_FIELD, cityId).findAll();
                sunriseSunsets.deleteAllFromRealm();

                //Delete detail DON'T use deleteTileDetail().
                //Delete inside of this transaction.
                RealmResults<TileDetail> tileDetails = realm.where(TileDetail.class)
                        .equalTo(CITY_ID_FIELD, cityId).findAll();
                tileDetails.deleteAllFromRealm();
            });
        }
        catch(Exception ex)
        {
            Log.i(Constant.TAG, "Could not delete the tile. ID : " + tile.getCityId());
            ex.printStackTrace();
        }
        finally { if(realmInstance != null) { realmInstance.close(); } }
    }

    /**
     * Delete a single detail tile. Don't do async or there could be weird bugs
     * when using in conjunctions with RefreshData.refresh().
     * @param tile an object with weather information.
     */
    @Override
    public void deleteTileDetail(final Tile tile)
    {
        Realm realmInstance = Realm.getDefaultInstance();
        try
        {
            realmInstance.executeTransaction(realm ->
            {
                RealmResults<TileDetail> tileDetails = realm.where(TileDetail.class)
                        .equalTo(CITY_ID_FIELD, tile.getCityId()).findAll();
                tileDetails.deleteAllFromRealm();
            });
        }
        catch(Exception ex)
        {
            Log.i(Constant.TAG, "Could not delete the tile detail. ID : " + tile.getCityId());
            ex.printStackTrace();
        }
        finally { if(realmInstance != null) { realmInstance.close(); } }
    }

    /**
     * Gets the data to populate the Weather Details View.
     * @param position  an int which detonates which row the data is fetched from.
     * @param tempScale an int which indicates which temp scale needs to be used. C or F.
     * @return one WeatherDetail object.
     */
    @Override
    public WeatherDetail getWeatherDetail(int position, @TempScale int tempScale)
    {
        Realm realmInstance = Realm.getDefaultInstance();
        Calendar calDefault = Calendar.getInstance();
        calDefault.setTimeZone(TimeZone.getDefault());

        WeatherDetail weatherDetail;
        WeatherDetail.DetailRows detailRows;

        SparseArray<List<WeatherDetail.DetailRows>> detailRowSparseArray = new SparseArray<>(6);
        List<WeatherDetail.DetailRows> detailRowsList = new ArrayList<>();

        int maxTempHeader = -999;
        int minTempHeader = 999;
        String[] tabContent = new String[15];

        try
        {
            Tile tile = realmInstance.where(Tile.class)
                    .equalTo(SAVED_POSITION_FIELD, position).findFirst();

            long cityId = tile.getCityId();

            Cities city = realmInstance.where(Cities.class)
                    .equalTo(CITY_ID_FIELD, cityId).findFirst();

            weatherDetail = new WeatherDetail();
            weatherDetail.setCityId(cityId);
            weatherDetail.setSavedPosition(tile.getSavedPosition());
            weatherDetail.setCityName(tile.getCityName());
            weatherDetail.setProvinceName(city.getRegion());
            weatherDetail.setCountryName(city.getCountry());

            int weatherId = tile.getWeatherId();
            weatherDetail.setWeatherId(weatherId);

            weatherDetail.setTemp(tile.getTempWithScale(tempScale));
            weatherDetail.setDescription(tile.getDescription());
            weatherDetail.setDayTime(tile.getIsDayTime());

            weatherDetail.setWindSpeed(tile.getWind());
            weatherDetail.setHumidity(tile.getHumidity());

            weatherDetail.setRainChance((int) realmInstance.where(TileDetail.class)
                    .equalTo(CITY_ID_FIELD, cityId).findFirst().getRainFall3h());

            long filterDay = Util.getStartOfDayInSecondsBasedOnDefaultTimeZone();
            int holdStartingDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

            //Only need five days
            for(int i = 0, l = 0; l < tabContent.length; i++, l += 3)
            {
                int rowTemp = -999;

                //Use to get most repeated weather ID in the list.
                int count = 1;
                int tempWeatherIdCount;
                int tempWeatherIdValue;
                //if the list is empty the button tab will show 800 (Sunny).
                int dayTabMostRepeatedWeatherId = 800;

                //Negative one (-1) is used in the TO field of the between query so
                //it does not include the first value of the next day.
                RealmResults<TileDetail> tileDetailList = realmInstance.where(TileDetail.class)
                        .equalTo(CITY_ID_FIELD, cityId)
                        .between(TIMESTAMP, filterDay + TimeUnit.DAYS.toSeconds(i),
                                (filterDay + TimeUnit.DAYS.toSeconds(i + 1)) - 1)
                        .findAll();

                int listSize = tileDetailList.size();

                /*
                 * List size could be zero for two reasons. One because the user added the
                 * tile past the last updated hour (in this case 10:00PM EST). Two because
                 * something did not update right. Fill in those values with the current
                 * tiles value. For the first (current) day this works fine. Other days
                 * is the best that can be done.
                 */
                if(listSize == 0)
                {
                    dayTabMostRepeatedWeatherId = tile.getWeatherId();
                    rowTemp = tile.getTemp();

                    //Create row record from current tile.
                    detailRows = weatherDetail.new DetailRows();
                    detailRows.setHumidity(tile.getHumidity());
                    detailRows.setTemp(tile.getTempWithScale(tempScale));
                    detailRows.setWind(tile.getWind());
                    detailRows.setWeatherId(dayTabMostRepeatedWeatherId);
                    detailRows.setIsDayTime(isDayTimeRow(realmInstance, cityId, 22));
                    detailRows.setRain(weatherDetail.getRainChance());
                    detailRows.setTimeShown("10PM");
                    detailRowsList.add(detailRows);
                }

                for(int j = 0; j < listSize; j++)
                {
                    calDefault.setTimeInMillis(tileDetailList.get(j).getTimestamp() * 1000);
                    int hourOfDay12 = calDefault.get(Calendar.HOUR);
                    int hourOfDay24 = calDefault.get(Calendar.HOUR_OF_DAY);
                    String AMPM = (calDefault.get(Calendar.AM_PM) == Calendar.AM) ? "AM" : "PM";
                    String timeShown = String.format(Locale.getDefault(), "%02d", hourOfDay12)
                            + AMPM;

                    detailRows = weatherDetail.new DetailRows();
                    detailRows.setHumidity(tileDetailList.get(j).getHumidity());
                    int temp = tileDetailList.get(j).getTempWithScale(tempScale);
                    detailRows.setTemp(temp);
                    detailRows.setRain((int) tileDetailList.get(j).getRainFall3h());
                    detailRows.setWind(tileDetailList.get(j).getWind());
                    detailRows.setWeatherId(tileDetailList.get(j).getWeatherId());
                    detailRows.setTimeShown(timeShown);
                    detailRows.setIsDayTime(isDayTimeRow(realmInstance, cityId, hourOfDay24));
                    detailRowsList.add(detailRows);

                    //If the first list has more than six values then set the max/min temp
                    //for the header display.
                    if(i == 0 && listSize >= 4)
                    {
                        if(temp > maxTempHeader) { maxTempHeader = temp; }
                        if(temp < minTempHeader) { minTempHeader = temp; }
                    }

                    //Get the max temp to apply to the rows.
                    if(temp > rowTemp) { rowTemp = temp; }

                    //Set first id as the default to start comparing.
                    dayTabMostRepeatedWeatherId = tileDetailList.get(i).getWeatherId();
                    tempWeatherIdValue = tileDetailList.get(j).getWeatherId();
                    tempWeatherIdCount = 0;
                    //Get the weatherId that repeats the most in the list
                    for(int k = 0; k < listSize; k++)
                    {
                        if(tempWeatherIdValue == tileDetailList.get(k).getWeatherId())
                        { tempWeatherIdCount ++; }
                    }

                    if(tempWeatherIdCount > count)
                    {
                        dayTabMostRepeatedWeatherId = tempWeatherIdValue;
                        count = tempWeatherIdCount;
                    }
                }

                //Set tabs values for each day.
                tabContent[l] = String.valueOf(dayTabMostRepeatedWeatherId);
                tabContent[l + 1] = String.valueOf(rowTemp) + Constant.degreeSymbol;
                tabContent[l + 2] = Util.getDayOfWeekAsStr(holdStartingDay + i);

                //Break the list into a single day.
                detailRowSparseArray.put(i, detailRowsList);
                detailRowsList = new ArrayList<>();
            }

            weatherDetail.setTabContent(tabContent);
            weatherDetail.setTempMax(maxTempHeader);
            weatherDetail.setTempMin(minTempHeader);
            weatherDetail.setDetailRows(detailRowSparseArray);
        }
        catch(Exception ex)
        {
            Log.i(Constant.TAG, "Could not retrieve Weather Detail data. Position : "
                    + position);
            ex.printStackTrace();
            return null;
        }
        finally { if(realmInstance != null) { realmInstance.close(); } }

        return weatherDetail;
    }

    /**
     * Change the saved position of the tile object.
     * @param citiesId long array of cities id.
     */
    @Override
    public void saveTilePosition(long[] citiesId)
    {
        Realm realmInstance = Realm.getDefaultInstance();
        try
        {
            realmInstance = Realm.getDefaultInstance();
            realmInstance.executeTransactionAsync(realm ->
            {
                for(int i = 0; i < citiesId.length; i++)
                {
                    Tile tile = realm.where(Tile.class)
                            .equalTo(CITY_ID_FIELD, citiesId[i])
                            .findFirst();
                    try
                    {
                        tile.setSavedPosition(i);
                        realm.insertOrUpdate(tile);
                    }
                    catch(NullPointerException ex) { ex.printStackTrace(); }
                }
            });
        }
        finally { if(realmInstance != null) { realmInstance.close(); } }
    }

    /**
     * Set a new tab position.
     * @param position an int to which the selected position will be set to.
     * @param cityId   an int with which to search for the correct tile.
     */
    @Override
    public void saveSelectedTabPosition(int position, long cityId)
    {
        Realm realmInstance = Realm.getDefaultInstance();
        try
        {
            realmInstance = Realm.getDefaultInstance();
            realmInstance.executeTransactionAsync(realm ->
            {

            });
        }
        catch(Exception ex)
        {
            Log.i(Constant.TAG, "Unable to set the selected tab position."
                    + "City Id : " + cityId + " - Position : " + position);
        }
        finally { if(realmInstance != null) { realmInstance.close(); } }
    }

    /**
     * Get whether is day time or night time. Uses sunset and sunrise values
     * which are already calculate. This is not a good method for createTile
     * since those values need to be calculated at the time the tile is created.
     *
     * @param realm  a realm instance.
     * @param cityId a long representing a city.
     * @return a boolean detonating whether is day or night.
     */
    private boolean isDayTime(Realm realm, long cityId)
    {
        SunriseSunset sunriseSunset = realm.where(SunriseSunset.class)
                .equalTo(CITY_ID_FIELD, cityId).findFirst();

        String timezone = realm.where(TimeZoneIds.class)
                .equalTo(CITY_ID_FIELD, cityId).findFirst().getTimezone();

        //Uses already calculate sunrise/sunset values.
        return DayTime.isDayTime(sunriseSunset, timezone);
    }

    /**
     * Get whether is day time or night time. Uses sunset and sunrise values
     * which are already calculate.
     *
     * @param realm a realm instance.
     * @param cityId a long representing a city.
     * @param hour an int with the row hour (base on 24 hours).
     * @return a boolean detonating whether is day or night.
     */
    private boolean isDayTimeRow(Realm realm, long cityId, int hour)
    {
        SunriseSunset sunriseSunset = realm.where(SunriseSunset.class)
                .equalTo(CITY_ID_FIELD, cityId).findFirst();

        return DayTime.isDayTime(sunriseSunset, hour);
    }

    /**
     *
     * @param tile      an object with weather information.
     * @param eventType an int which indicates what type of event is being passed.
     *                  Create, Update or Delete.
     */
    private void onTransactionResult(@Nullable Tile tile, @EventType int eventType)
    {
        Events.Weather event = new Events().new Weather();
        event.setEventType(eventType);
        event.setTile(tile);
        EventBus.getDefault().post(event);
    }

    /**
     *
     * @param tileList  a list of tile objects with weather information.
     * @param eventType an int which indicates what type of event is being passed.
     *                  Create, Update or Delete.
     */
    private void onTransactionResult(@Nullable List<Tile> tileList, @EventType int eventType)
    {
        Events.Weather event = new Events().new Weather();
        event.setEventType(eventType);
        event.setTiles(tileList);
        EventBus.getDefault().post(event);
    }
}