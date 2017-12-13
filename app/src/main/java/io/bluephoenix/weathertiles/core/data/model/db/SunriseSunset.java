package io.bluephoenix.weathertiles.core.data.model.db;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * @author Carlos A. Perez
 */
public class SunriseSunset extends RealmObject
{
    @Index
    @PrimaryKey
    private long cityId;

    private double lat;
    private double lon;

    private double sunrise;
    private double sunset;

    public long getCityId()
    {
        return cityId;
    }

    public void setCityId(long cityId)
    {
        this.cityId = cityId;
    }

    public double getLat()
    {
        return lat;
    }

    public void setLat(double lat)
    {
        this.lat = lat;
    }

    public double getLon()
    {
        return lon;
    }

    public void setLon(double lon)
    {
        this.lon = lon;
    }

    public double getSunrise()
    {
        return sunrise;
    }

    public void setSunrise(double sunrise)
    {
        this.sunrise = sunrise;
    }

    public double getSunset()
    {
        return sunset;
    }

    public void setSunset(double sunset)
    {
        this.sunset = sunset;
    }
}
