package io.bluephoenix.weathertiles.util;

import java.util.List;

import io.bluephoenix.weathertiles.core.data.model.bus.Events;
import io.bluephoenix.weathertiles.core.data.model.db.Tile;
import io.bluephoenix.weathertiles.core.data.remote.GetWeatherInfo;
import io.bluephoenix.weathertiles.core.data.remote.RetrofitCall;
import io.bluephoenix.weathertiles.core.data.remote.response.ApiResponseFiveDay;
import io.bluephoenix.weathertiles.core.data.repository.IRepository;

/**
 * @author Carlos A. Perez
 */
public class RefreshData
{
    /**
     * Refresh the details tiles data. This is done once a day using an alarm and service.
     * If however, it is not possible to do at that time (no internet, phone is off). Then
     * this method sets a preference to let the application know that on next start it needs
     * to refresh the data.
     *
     * @param weatherRepository database CRUD methods.
     * @param preferences       access and modifies the applications shared preferences.
     * @return a boolean whether the tiles were update successfully or not.
     */
    public static boolean refresh(IRepository.Weather weatherRepository,
                                  IRepository.Preferences preferences)
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
                        RetrofitCall.fiveDayWeather(cityId)).get(); //blocking we want.

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
