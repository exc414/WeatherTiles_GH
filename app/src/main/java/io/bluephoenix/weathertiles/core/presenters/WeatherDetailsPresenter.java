package io.bluephoenix.weathertiles.core.presenters;

import android.content.SharedPreferences;
import android.os.Handler;

import io.bluephoenix.weathertiles.core.common.TempScaleDef.TempScale;
import io.bluephoenix.weathertiles.core.data.model.db.WeatherDetail;
import io.bluephoenix.weathertiles.core.data.repository.IRepository;
import io.bluephoenix.weathertiles.core.data.repository.PreferencesRepository;
import io.bluephoenix.weathertiles.core.data.repository.WeatherRepository;
import java8.util.concurrent.CompletableFuture;

/**
 * @author Carlos A. Perez
 */
public class WeatherDetailsPresenter extends
        BasePresenter<IWeatherDetailsContract.IPublishToView>
        implements IWeatherDetailsContract.IPresenter
{
    //offset - 3 = 1, 5 = 2, 7 = 3. offsetCheck - 1 = offset
    private int offset = 1;
    private IRepository.Weather weatherRepository;
    private IRepository.Preferences preferencesRepository;
    private final Handler main = new Handler();
    private @TempScale
    int tempScale;

    public WeatherDetailsPresenter(@TempScale int tempScale,
                                   SharedPreferences sharedPreferences)
    {
        this.tempScale = tempScale;
        weatherRepository = new WeatherRepository();
        preferencesRepository = new PreferencesRepository(sharedPreferences);
    }

    /**
     * Gets the first detail to be shown first. Once that completes then it gets the
     * surrounding details. In this case there are 2 extra details loaded that the
     * user cannot see. When possible its always even on the left and right.
     *
     * Take using 5 details. In this case if the user taps/clicks on the 0 position
     * tile no tiles to the left will be loaded, only 2 to the right. If the click
     * comes at position 1. Then 1 detail tile will be loaded to the left and 2 to the
     * right. The same in reverse goes for the last tile and the second to last tile.
     * Instead tiles to the left will be loaded but not on the right.
     *
     * @param startPos   an int which gives the position of which detail will be shown first.
     *                   From this position we traverse the detail table.
     * @param totalTiles an int which gives the total number of tiles. Useful in determining
     *                   the end of the list which is important when adding tile to the
     *                   right of the first item.
     */
    @Override
    public void initDetails(int startPos, int totalTiles)
    {
        //Get the maxNumberOfTiles to set the size of the selectedPosition array.
        //The array keeps track of which button tabs the user presses per WeatherDetail.
        CompletableFuture getMaxNumberOfTiles = CompletableFuture.supplyAsync(() ->
            preferencesRepository.getMaxNumberOfTiles())
            .thenAccept(maxNumberOfTiles ->
            {
                main.post(() -> publishToView.setMaxNumberOfTiles(maxNumberOfTiles));
            });

        getMaxNumberOfTiles.thenRunAsync(() ->
        {
            //Get first details, once that is confirmed to have returned, then go and get
            //the ones to the left and right.
            CompletableFuture getDetails = CompletableFuture.supplyAsync(() ->
                weatherRepository.getWeatherDetail(startPos, tempScale))
                .thenAccept(weatherDetail ->
                {
                    main.post(() -> publishToView.initDetailFirst(weatherDetail));
                });

            getDetails.thenRunAsync(() ->
            {
                //If there is less details to load than the offset use that starting position
                //as the offset or else an out of bounds exception will be thrown from the
                //repository class.
                int adjustedOffsetLeft = (startPos < offset) ? startPos : offset;
                for(int i = 1; i <= adjustedOffsetLeft; i++)
                {
                    int index = i;
                    if(i >= 0)
                    {
                        WeatherDetail wd = weatherRepository.getWeatherDetail(
                                startPos - i, tempScale);
                        main.post(() -> publishToView.initDetailsToLeftOfFirst(wd, index));
                    }
                }

                //If the different between the total tiles and start position is less than the
                //offset then use as the diff as offset or just like before an out of bounds
                //exception will thrown.
                int diff = totalTiles - startPos;
                int adjustedOffsetRight = (diff < offset) ? diff : offset;
                for(int i = 1; i <= adjustedOffsetRight; i++)
                {
                    if(i <= totalTiles)
                    {
                        WeatherDetail wd = weatherRepository.getWeatherDetail(
                                startPos + i, tempScale);
                        main.post(() -> publishToView.initDetailsToRightOfFirst(wd));
                    }
                }
            });
        });
    }

    @Override
    public void addDetailLeft(int detailPos)
    {
        CompletableFuture.runAsync(() ->
        {
            WeatherDetail wd = weatherRepository.getWeatherDetail(detailPos, tempScale);
            main.post(() -> publishToView.addDetailLeft(wd));
        });
    }

    @Override
    public void addDetailRight(int detailPos)
    {
        CompletableFuture.runAsync(() ->
        {
            WeatherDetail wd = weatherRepository.getWeatherDetail(detailPos, tempScale);
            main.post(() -> publishToView.addDetailRight(wd));
        });
    }
}
