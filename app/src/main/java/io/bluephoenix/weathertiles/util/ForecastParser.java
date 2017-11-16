package io.bluephoenix.weathertiles.util;

import java.util.ArrayList;
import java.util.List;

import io.bluephoenix.weathertiles.core.data.model.db.Tile;
import io.bluephoenix.weathertiles.core.data.model.db.TileDetail;
import io.bluephoenix.weathertiles.core.data.remote.response.ApiResponseCurrent;
import io.bluephoenix.weathertiles.core.data.remote.response.ApiResponseFiveDay;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class ForecastParser
{
    public static Tile parseResponse(ApiResponseCurrent response, long cityId)
    {
        //Make tile object to be saved in the Tile table. This is used in the WeatherActivity.
        Double lat = Double.valueOf(response.getData().get(0).getLat());
        Double lon = Double.valueOf(response.getData().get(0).getLon());

        Tile tile = new Tile();
        tile.setCityId(cityId);
        tile.setWeatherId(response.getData().get(0).getWeatherCurrent().getWeatherId());
        tile.setTempCelsius((int) Math.round(response.getData().get(0).getTemp()));
        tile.setLat(lat);
        tile.setLon(lon);
        tile.setDescription(response.getData().get(0).getWeatherCurrent().getDescription());
        tile.setTimeZone(response.getData().get(0).getTimezone());

        tile.setDayTime(
                SunriseSunset.isDayTime(tile.getLat(), tile.getLon(), tile.getTimeZone()));
        tile.setSunrise(SunriseSunset.getSunrise());
        tile.setSunset(SunriseSunset.getSunset());

        tile.setCity(response.getData().get(0).getCityName());
        tile.setCountryIso(response.getData().get(0).getCountryCode());

        return tile;
    }

    /**
     * Parse the response returned by the API into a list of tile detail objects.
     * @param response a payload of parsed json.
     * @return a list of tile details objects.
     */
    public static List<TileDetail> parseResponse(ApiResponseFiveDay response)
    {
        int size = response.getList().size();
        List<TileDetail> tileDetailList = new ArrayList<>(size);

        try
        {
            int temp;
            TileDetail tileDetail;

            for(int i = 0; i < size; i++)
            {
                tileDetail = new TileDetail();
                tileDetail.setCityId(response.getCity().getCityId());

                tileDetail.setWeatherId(String.valueOf(response.getList().get(i)
                        .getWeather().get(0).getWeatherId()));

                temp = (int) Math.round(
                        response.getList().get(i).getMain().getTemp());
                tileDetail.setTemperature(temp);

                tileDetail.setHumidity(Math.round(
                        response.getList().get(i).getMain().getHumidity()));

                tileDetail.setWind(Math.round(
                        response.getList().get(i).getWind().getSpeed()));

                //Timestamp is in UTC and seconds NOT milliseconds.
                tileDetail.setTimestamp(response.getList().get(i).getTimestamp());

                try
                {
                    //This could completely null therefore catch and set to 0 if so.
                    tileDetail.setRainFall3h((float)
                            response.getList().get(i).getRain().getRainFall3h());
                }
                catch(NullPointerException ex)
                {
                    tileDetail.setRainFall3h(0);
                }

                tileDetail.setMainWeather(response.getList().get(i)
                        .getWeather().get(0).getMainWeather());

                tileDetail.setDescription(response.getList().get(i)
                        .getWeather().get(0).getDescription());

                tileDetailList.add(tileDetail);
            }
        }
        catch(NullPointerException ex) { ex.printStackTrace(); }

        return tileDetailList;
    }
}
