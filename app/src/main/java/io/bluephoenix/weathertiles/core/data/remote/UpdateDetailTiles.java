package io.bluephoenix.weathertiles.core.data.remote;

import java.util.List;

import io.bluephoenix.weathertiles.core.data.model.bus.Events;
import io.bluephoenix.weathertiles.core.data.model.db.Tile;
import io.bluephoenix.weathertiles.core.data.remote.response.ApiResponseFiveDay;
import io.bluephoenix.weathertiles.core.data.repository.IRepository;
import io.bluephoenix.weathertiles.util.ForecastParser;
import java8.util.function.Supplier;

/**
 * @author Carlos A. Perez
 */
public class UpdateDetailTiles implements Supplier<Boolean>
{
    private IRepository.Weather weatherRepository;
    private IRepository.Preferences preferences;

    public UpdateDetailTiles(IRepository.Weather weatherRepository,
                             IRepository.Preferences preferences)
    {
        this.weatherRepository = weatherRepository;
        this.preferences = preferences;
    }

    @Override
    public Boolean get()
    {
        return updateDetailTiles();
    }

    /**
     * Download fresh detail tile data from the API. If successful
     * delete the previously saved data and store the new one. If it
     * fails most likely internet issues then set a share prefs var
     * detonating that on next start up the data should be downloaded.
     */
    private Boolean updateDetailTiles()
    {
        try
        {
            long cityId;
            Events.APIResponse<ApiResponseFiveDay> apiResponse;
            List<Tile> tileList = weatherRepository.getAllTiles();

            for(int i = 0; i < tileList.size(); i++)
            {
                cityId = tileList.get(i).getCityId();
                apiResponse = new GetWeatherInfo<>(
                        RetrofitCall.fiveDayWeather(cityId)).get();

                //No internet. Set needs update on share preferences.
                //On next application start up try downloading the tile details data again.
                if(apiResponse.getHasFailed() == true)
                {
                    preferences.setDataRefreshNeeded(true);
                    return false;
                }
                else
                {
                    weatherRepository.deleteSingleDetailTile(tileList.get(i));
                    weatherRepository.createDetailTiles(
                            ForecastParser.parseResponse(apiResponse.getResponse()));
                }
            }
        }
        catch(NullPointerException ex)
        {
            ex.printStackTrace();
            preferences.setDataRefreshNeeded(true);
            return false;
        }

        preferences.setDataRefreshNeeded(false);
        return true;
    }
}
