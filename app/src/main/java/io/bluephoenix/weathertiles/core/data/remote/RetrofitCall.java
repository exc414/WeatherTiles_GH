package io.bluephoenix.weathertiles.core.data.remote;

import io.bluephoenix.weathertiles.core.data.remote.response.ApiResponseCurrent;
import io.bluephoenix.weathertiles.core.data.remote.response.ApiResponseFiveDay;
import io.bluephoenix.weathertiles.util.Constant;
import retrofit2.Call;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class RetrofitCall
{
    public static Call<ApiResponseCurrent> currentWeather(long cityId)
    {
        return RetrofitClient.getCurrentWeatherClient(Constant.BASE_OWM_API_URL)
                .create(IWeatherService.class).getCurrentWeatherByID(cityId);
    }

    public static Call<ApiResponseFiveDay> fiveDayWeather(long cityId)
    {
        return RetrofitClient.getForecastWeatherClient(Constant.BASE_OWM_API_URL)
                .create(IWeatherService.class).getFiveDayForecastById(cityId);
    }
}
