package io.bluephoenix.weathertiles.core.data.remote;

import io.bluephoenix.weathertiles.BuildConfig;
import io.bluephoenix.weathertiles.core.data.remote.response.ApiResponseCurrent;
import io.bluephoenix.weathertiles.core.data.remote.response.ApiResponseFiveDay;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public interface IWeatherService
{
    /**
     * Query by providing the exact ID of the city/region.
     * Gives at the moment data. Most accurate and detailed data.
     * This uses Open Weather Map API.
     *
     * @param id an int of the city/region
     * @return an ApiResponseCurrent with which you can make a tile object
     */
    @GET("weather?units=metric&apikey=" + BuildConfig.OPEN_WEATHER_API_KEY)
    Call<ApiResponseCurrent> getCurrentWeatherByID(@Query("id") long id);

    /**
     * Query by providing the exact ID of the city/region
     * This is gives back 5 days worth of data split by 3 hour intervals.
     * This uses Open Weather Map API.
     *
     * @param id a long containing the identification number of the city.
     * @return a Completable Future object with the parsed JSON.
     */
    @GET("forecast?units=metric&apikey=" + BuildConfig.OPEN_WEATHER_API_KEY)
    Call<ApiResponseFiveDay> getFiveDayForecastById(@Query("id") long id);
}
