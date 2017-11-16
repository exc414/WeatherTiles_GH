package io.bluephoenix.weathertiles.core.data.repository;

import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.bluephoenix.weathertiles.core.common.EventDef;
import io.bluephoenix.weathertiles.core.common.EventDef.EventType;
import io.bluephoenix.weathertiles.core.data.model.bus.Events;
import io.bluephoenix.weathertiles.core.data.model.db.Tile;
import io.bluephoenix.weathertiles.core.data.model.db.TileDetail;
import io.bluephoenix.weathertiles.util.SunriseSunset;
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
                int preInsertTotalTiles = realm.where(Tile.class).findAll().size();
                tile.setSavedPosition(preInsertTotalTiles);
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
    public void createDetailTiles(List<TileDetail> tileDetailList)
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
                        tiles.get(i).setWeatherId(tileDetail.getWeatherId());
                        tiles.get(i).setTempCelsius(tileDetail.getTemperature());
                        tiles.get(i).setDescription(tileDetail.getDescription());
                        tiles.get(i).setDayTime(SunriseSunset.isDayTime(tiles.get(i)));

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
                    isDayTime = SunriseSunset.isDayTime(tiles.get(i));

                    if(isDayTime && !tiles.get(i).getDayTime())
                    {
                        tiles.get(i).setDayTime(true);
                        tileList.add(tiles.get(i));
                        realm.insertOrUpdate(tiles.get(i));

                    }
                    else if(!isDayTime && tiles.get(i).getDayTime())
                    {
                        tiles.get(i).setDayTime(false);
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
        try
        {
            realmInstance.executeTransactionAsync(realm ->
            {
                //Delete from tile table.
                RealmResults<Tile> result = realm.where(Tile.class)
                        .equalTo(CITY_ID_FIELD, tile.getCityId()).findAll();
                result.deleteAllFromRealm();

                //Delete from TileDetail table.
                deleteSingleDetailTileHelper(tile, realm);
            });
        }
        finally { if(realmInstance != null) { realmInstance.close(); } }
    }

    /**
     * Delete a single detail tile wrapper.
     * @param tile an object with weather information.
     */
    @Override
    public void deleteSingleDetailTile(final Tile tile)
    {
        Realm realmInstance = Realm.getDefaultInstance();
        try
        {
            realmInstance.executeTransaction(realm ->
                    deleteSingleDetailTileHelper(tile, realm));
        }
        finally { if(realmInstance != null) { realmInstance.close(); } }
    }

    /**
     * Delete single detail tile.
     * @param tile  tile an object with weather information.
     * @param realm a realm instance.
     */
    private void deleteSingleDetailTileHelper(final Tile tile, Realm realm)
    {
        RealmResults<TileDetail> tileDetails = realm.where(TileDetail.class)
                .equalTo(CITY_ID_FIELD, tile.getCityId()).findAll();
        tileDetails.deleteAllFromRealm();
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

    private void onTransactionResult(@Nullable Tile tile, @EventType int eventType)
    {
        Events.Weather event = new Events().new Weather();
        event.setEventType(eventType);
        event.setTile(tile);
        EventBus.getDefault().post(event);
    }

    private void onTransactionResult(@Nullable List<Tile> tileList, @EventType int eventType)
    {
        Events.Weather event = new Events().new Weather();
        event.setEventType(eventType);
        event.setTiles(tileList);
        EventBus.getDefault().post(event);
    }
}