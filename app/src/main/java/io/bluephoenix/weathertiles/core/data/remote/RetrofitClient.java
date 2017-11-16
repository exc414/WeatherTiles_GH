package io.bluephoenix.weathertiles.core.data.remote;

import io.bluephoenix.weathertiles.app.UtilHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Carlos A. Perez Zubizarreta
 */
class RetrofitClient
{
    private static Retrofit weatherClientCurrent = null;
    private static Retrofit weatherClientForecast = null;

    /**
     * Singleton Retrofit client to perform request with.
     * For use with Weatherbit.
     *
     * @param baseUrl String containing url
     * @return a retrofit instance if one is not made already
     */
    static Retrofit getCurrentWeatherClient(String baseUrl)
    {
        if(weatherClientCurrent == null)
        {
            weatherClientCurrent = new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(UtilHttpClient.getHttpClient().build())
                            .build();
        }

        return weatherClientCurrent;
    }

    /**
     * Singleton Retrofit client to perform request with.
     * For use with Open Weather Map.
     *
     * @param baseUrl String containing url
     * @return a retrofit instance if one is not made already
     */
    static Retrofit getForecastWeatherClient(String baseUrl)
    {
        if(weatherClientForecast == null)
        {
            weatherClientForecast = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(UtilHttpClient.getHttpClient().build())
                    .build();
        }

        return weatherClientForecast;
    }
}
