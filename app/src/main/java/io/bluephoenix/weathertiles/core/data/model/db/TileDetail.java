package io.bluephoenix.weathertiles.core.data.model.db;

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

    @Required
    private String weatherId;

    private int temperature;
    private int humidity;
    private float wind;
    private long timestamp;
    private float rainFall3h;
    private boolean hasBeenUsed = false;

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

    public String getWeatherId()
    {
        return weatherId;
    }

    public void setWeatherId(String weatherId)
    {
        this.weatherId = weatherId;
    }

    public int getTemperature()
    {
        return temperature;
    }

    public void setTemperature(int temperature)
    {
        this.temperature = temperature;
    }

    public int getHumidity()
    {
        return humidity;
    }

    public void setHumidity(int humidity)
    {
        this.humidity = humidity;
    }

    public float getWind()
    {
        return wind;
    }

    public void setWind(float wind)
    {
        this.wind = wind;
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

    public float getRainFall3h()
    {
        return rainFall3h;
    }

    public void setRainFall3h(float rainFall3h)
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
