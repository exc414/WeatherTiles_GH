package io.bluephoenix.weathertiles.core.presenters;

import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.app.App;
import io.bluephoenix.weathertiles.core.common.DialogDef;
import io.bluephoenix.weathertiles.core.common.EventDef;
import io.bluephoenix.weathertiles.core.common.SortDef.SortType;
import io.bluephoenix.weathertiles.core.common.TempScaleDef.TempScale;
import io.bluephoenix.weathertiles.core.data.model.bus.Events;
import io.bluephoenix.weathertiles.core.data.model.db.Tile;
import io.bluephoenix.weathertiles.core.data.remote.GetWeatherInfo;
import io.bluephoenix.weathertiles.core.data.remote.RetrofitCall;
import io.bluephoenix.weathertiles.core.data.repository.IRepository;
import io.bluephoenix.weathertiles.core.data.repository.PreferencesRepository;
import io.bluephoenix.weathertiles.core.data.repository.WeatherRepository;
import io.bluephoenix.weathertiles.core.jobs.DailySyncJob;
import io.bluephoenix.weathertiles.util.Constant;
import io.bluephoenix.weathertiles.util.ForecastParser;
import io.bluephoenix.weathertiles.util.RefreshData;
import io.bluephoenix.weathertiles.util.Util;
import java8.util.concurrent.CompletableFuture;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class WeatherPresenter extends BasePresenter<IWeatherContract.IPublishToView>
        implements IWeatherContract.IPresenter
{

    private final boolean onStartUpCall = true;
    private final boolean onRepeatCall = false;

    private final long updateInterval = TimeUnit.HOURS.toSeconds(3);
    private final long updateSunriseSunsetInterval = TimeUnit.MINUTES.toMillis(5);

    private final Handler updateWeather = new Handler();
    private final Handler updateSunriseSunset = new Handler();
    private final Handler initTiles = new Handler();
    private final Handler noInternet = new Handler();

    private IRepository.Weather weatherRepository;
    private IRepository.Preferences preferences;
    private List<Tile> savedTileList;

    public WeatherPresenter(SharedPreferences sharedPreferences)
    {
        weatherRepository = new WeatherRepository();
        preferences = new PreferencesRepository(sharedPreferences);
        new DailySyncJob();
    }

    /**
     * Check if the details tiles data needs a refresh.
     * Load tile(s) from the database on startup.
     */
    @Override
    public void initTiles()
    {
        if(savedTileList == null)
        {
            CompletableFuture.runAsync(() ->
            {
                if(preferences.getDataRefreshNeeded() && preferences.getHasRunOnce())
                {
                    //Show dialog to let the user know that data is downloading in the
                    //background and it might take a while. Usually this is done
                    //in a background service but for whatever reason this was not
                    //done successfully. If it fails then show the network problem dialog.
                    initTiles.post(() -> publishToView.notifyUserAlert(DialogDef.DOWNLOADING));
                    boolean result = RefreshData.refresh(weatherRepository, preferences);

                    if(!result)
                    {
                        initTiles.post(() ->
                        {
                            publishToView.notifyUserAlert(DialogDef.DOWNLOADING_FINISHED);
                            publishToView.notifyUserAlert(DialogDef.NO_INTERNET);
                        });
                    }
                    else
                    {
                        //If it did not failed, it has finished therefore dismiss the dialog.
                        initTiles.post(() ->
                                publishToView.notifyUserAlert(DialogDef.DOWNLOADING_FINISHED));
                    }
                }

                long updateTimestampVar = updateTimestamp();
                Log.i(Constant.TAG, "Update timestamp is : " + updateTimestampVar);
                weatherRepository.updateTiles(updateTimestampVar, onStartUpCall);
                List<Tile> tiles = weatherRepository.getAllTiles();

                initTiles.post(() ->
                {
                    publishToView.initTiles(tiles);
                    pollForCurrentWeatherUpdate();
                    pollForSunriseSunsetUpdate();
                });
            });
        }
        else { publishToView.initTiles(savedTileList); }
    }

    /**
     * Obtain tile from network and adds the tile to the database.
     * Sets a placeholder/dummy tile in the WeatherAdapter.
     * @param cityId a long with the city identifier to pass to the API.
     */
    @Override
    public void createTile(long cityId)
    {
        //Download data -> Accept Result -> Check result in case of failure -> Parse Result ->
        //Save to tile table -> Execute another CF to get detail weather information ->
        //Accept Result -> Parse result -> Save to details table
        CompletableFuture.supplyAsync(new GetWeatherInfo<>(
                RetrofitCall.currentWeather(cityId))).thenAccept(apiResponseCurrent ->
        {
            if(apiResponseCurrent.getHasFailed())
            { noInternet.post(() -> publishToView.notifyUserAlert(DialogDef.NO_INTERNET)); }
            else
            {
                weatherRepository.createTile(ForecastParser.parseResponse(
                        apiResponseCurrent.getResponse(), cityId));

                CompletableFuture.supplyAsync(new GetWeatherInfo<>(
                    RetrofitCall.fiveDayWeather(cityId))).thenAccept(apiResponseFiveDay ->
                    weatherRepository.createTileDetails(ForecastParser.parseResponse
                            (apiResponseFiveDay.getResponse(), cityId)));
            }
        });
    }

    /**
     * On configuration changes save the currently loaded tiles.
     * @param tileList a list of tiles objects.
     */
    @Override
    public void saveTiles(List<Tile> tileList)
    {
        this.savedTileList = tileList;
    }

    /**
     * Set the position of tiles in the database which correspond to the
     * Weather Adapter position.
     * @param citiesId a long array of cities id.
     */
    @Override
    public void saveTilePosition(long[] citiesId)
    {
        weatherRepository.saveTilePosition(citiesId);
    }

    /**
     * Delete one tile.
     * @param tile an object with weather information.
     */
    @Override
    public void deleteTile(Tile tile) { weatherRepository.deleteTile(tile); }

    /**
     * Check for an update on whether is daytime or nighttime.
     * The interval is 5 minutes which is half of the margin of error of the sunrise/sunset
     * calculation. (Sane default)
     */
    private void pollForSunriseSunsetUpdate()
    {
        updateSunriseSunset.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                weatherRepository.updateTilesSunriseSunset();
                updateSunriseSunset.postDelayed(this, updateSunriseSunsetInterval);
            }
        }, 2000);
    }

    /**
     * Update tiles if need it and set an update interval.
     * Update interval is 3 hours as set by the Open Weather Map API.
     */
    private void pollForCurrentWeatherUpdate()
    {
        long updateTimestamp = updateTimestamp();
        long startTime = (updateTimestamp + updateInterval) -
                Util.getTimeNowInSeconds(Calendar.getInstance(), "UTC");

        //Set future update while the application is running. Even though the
        //start time will be (most likely) far beyond the time the application is
        //used for, this covers edge cases if a user leaves the app open for 3+ hours.
        int jitter = 15000;
        updateWeather.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                weatherRepository.updateTiles(updateTimestamp(), onRepeatCall);
                updateWeather.postDelayed(this, TimeUnit.HOURS.toMillis(3));
            }
            //Convert starTime to milliseconds from seconds.
        }, (startTime * 1000) + jitter);
    }

    /**
     * Calculates the correct update hour plus the start of the day all in seconds.
     * @return a long timestamp that will be matched against the tile detail db.
     */
    private long updateTimestamp()
    {
        double nowInDecimals = Util.getTimeNowInDecimals(Calendar.getInstance(), "UTC");

        //There are 8 updates over 24 hours. Interval of 3 hours.
        //00:00 - 03:00 - 06:00 - 09:00 - 12:00 - 15:00 - 18:00 - 21:00
        for(int i = 0; i < 24; i += 3)
        {
            if(nowInDecimals >= i && nowInDecimals < (i + 3))
            {
                //Correct lower bound update hour. If nowInDecimals is 5 the
                //correct update hour returned would be 3. 12 would be 12. 11 would be 9.
                //All converted into seconds. Add this to the start of the day in seconds.
                return Util.getStartOfDayInSeconds() + TimeUnit.HOURS.toSeconds(i);
            }
        }
        return 0;
    }

    /**
     * Successfully added or updated a tile in the database.
     * @param event Event.TransactionResult object containing which event
     *                type the transaction was completed for.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTransactionResult(Events.Weather event)
    {
        switch(event.getEventType())
        {
            case EventDef.CREATED:
                 publishToView.addTile(event.getTile());
                 break;

            case EventDef.CREATE_ERROR:
                 publishToView.tileEvent(App.getInstance().getString(
                        R.string.event_message_error_creating));
                 break;

            case EventDef.UPDATED:
                 publishToView.updateTiles(event.getTiles());
                 break;

            case EventDef.UPDATE_ERROR:
                 publishToView.tileEvent(App.getInstance().getString(
                        R.string.event_message_error_updating));
                 break;

            case EventDef.GENERAL_ERROR:
                 publishToView.tileEvent(App.getInstance().getString(
                        R.string.event_message_error_general));
                 break;

            default: break;
        }
    }

    @Override
    public void setDefaultSort(@SortType int sortType)
    { preferences.setDefaultSort(sortType); }

    @Override
    public int getDefaultSort()
    {
        return preferences.getDefaultSort();
    }

    @Override
    public void setDefaultTempScale(@TempScale int tempScale)
    { preferences.setDefaultTempScale(tempScale); }

    @Override
    public int getDefaultTempScale()
    {
        return preferences.getDefaultTempScale();
    }

    @Override
    public void setHasRunOnce()
    {
        preferences.setHasRunOnce();
    }

    @Override
    public boolean getHasRunOnce() { return preferences.getHasRunOnce(); }

    @Override
    public int getMaxNumberOfTiles() { return preferences.getMaxNumberOfTiles(); }

    @Override
    public int getNewTileBehaviourFlags()
    {
        return (preferences.getEnableBlinkAnimation()
                | preferences.getEnableAutoScrolling());
    }
}