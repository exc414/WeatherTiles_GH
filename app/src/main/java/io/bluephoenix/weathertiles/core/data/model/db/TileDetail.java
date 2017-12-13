package io.bluephoenix.weathertiles.core.data.model.db;

import io.bluephoenix.weathertiles.core.common.TempScaleDef;
import io.bluephoenix.weathertiles.core.common.TempScaleDef.TempScale;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class TileDetail extends RealmObject
{
    @PrimaryKey
    private int id;

    @Index
    private long cityId;
    private int weatherId;
    private int tempCelsius;
    private double wind;
    private int humidity;
    private long timestamp;
    private boolean hasBeenUsed = false;
    private double rainFall3h;

    @Required
    private String mainWeather;

    @Required
    private String description;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public long getCityId()
    {
        return cityId;
    }

    public void setCityId(long cityId)
    {
        this.cityId = cityId;
    }

    public int getWeatherId()
    {
        return weatherId;
    }

    public void setWeatherId(int weatherId)
    {
        this.weatherId = weatherId;
    }

    public int getTemperature()
    {
        return tempCelsius;
    }

    public int getTempWithScale(@TempScale int tempScale)
    {
        return (tempScale == TempScaleDef.FAHRENHEIT) ?
                (int) Math.round((tempCelsius * 1.8) + 32) : tempCelsius;
    }

    public void setTemperature(int tempCelsius)
    {
        this.tempCelsius = tempCelsius;
    }

    public double getWind()
    {
        return wind;
    }

    public void setWind(double wind)
    {
        this.wind = wind;
    }

    public int getHumidity()
    {
        return humidity;
    }

    public void setHumidity(int humidity)
    {
        this.humidity = humidity;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public double getRainFall3h()
    {
        return rainFall3h;
    }

    public void setRainFall3h(double rainFall3h)
    {
        this.rainFall3h = rainFall3h;
    }

    public String getMainWeather()
    {
        return mainWeather;
    }

    public void setMainWeather(String mainWeather)
    {
        this.mainWeather = mainWeather;
    }

    public boolean getHasBeenUsed()
    {
        return hasBeenUsed;
    }

    public void setHasBeenUsed(boolean hasBeenUsed)
    {
        this.hasBeenUsed = hasBeenUsed;
    }
}
