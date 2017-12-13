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
        Tile tile = new Tile();
        tile.setCityId(cityId);
        tile.setWeatherId(response.getWeather().get(0).getWeatherId());
        tile.setTemp((int) Math.round(response.getMain().getTemp()));
        tile.setHumidity(response.getMain().getHumidity());
        tile.setWind(response.getWind().getSpeed());
        tile.setLat(response.getCoord().getLat());
        tile.setLon(response.getCoord().getLon());
        tile.setDescription(response.getWeather().get(0).getDescription());
        tile.setCityName(response.getCityName());
        tile.setCountryIso(response.getCountryCode().getCountryCode());

        return tile;
    }

    /**
     * Parse the response returned by the API into a list of tile detail objects.
     * @param response a payload of parsed json.
     * @return a list of tile details objects.
     */
    public static List<TileDetail> parseResponse(ApiResponseFiveDay response, long cityId)
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
                tileDetail.setCityId(cityId);

                tileDetail.setWeatherId(response.getList().get(i)
                        .getWeather().get(0).getWeatherId());

                temp = (int) Math.round(response.getList().get(i).getMain().getTemp());
                tileDetail.setTemperature(temp);

                tileDetail.setHumidity(Math.round(response.getList().get(i)
                        .getMain().getHumidity()));

                tileDetail.setWind(response.getList().get(i).getWind().getSpeed());
                tileDetail.setTimestamp(response.getList().get(i).getTimestamp());

                try
                {
                    //This could completely null therefore catch and set to 0 if so.
                    tileDetail.setRainFall3h(
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