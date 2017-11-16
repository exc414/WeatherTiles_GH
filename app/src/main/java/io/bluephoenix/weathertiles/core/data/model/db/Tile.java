package io.bluephoenix.weathertiles.core.data.model.db;

import java.util.HashMap;

import io.bluephoenix.weathertiles.core.common.SortDef;
import io.bluephoenix.weathertiles.core.common.SortDef.SortType;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class Tile extends RealmObject
{
    @PrimaryKey
    private long cityId;

    @Required
    private String weatherId;

    private int tempCelsius;
    private double lat;
    private double lon;
    private int savedPosition = 0;
    private double sunrise;
    private double sunset;

    @Required
    private String description;

    @Required
    private String timeZone;

    //Using Boolean object instead of primitive for the null value.
    private Boolean isDayTime;

    @Required
    private String city;

    @Required
    private String countryIso;

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

    public int getTempCelsius()
    {
        return tempCelsius;
    }

    public void setTempCelsius(int tempCelsius)
    {
        this.tempCelsius = tempCelsius;
    }

    /**
     * The db has only celsius temp. Convert on the fly when fahrenheit is required.
     * @return an integer with the temperature in fahrenheit.
     */
    public int getTempFahrenheit() { return (int) Math.round((tempCelsius * 1.8) + 32); }

    public void setDayTime(boolean isDayTime) { this.isDayTime = isDayTime; }

    public boolean getDayTime() { return isDayTime; }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getTimeZone()
    {
        return timeZone;
    }

    public void setTimeZone(String timeZone)
    {
        this.timeZone = timeZone;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCountryIso()
    {
        return countryIso;
    }

    public void setCountryIso(String countryIso)
    {
        this.countryIso = countryIso;
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

    public int getSavedPosition()
    {
        return savedPosition;
    }

    public void setSavedPosition(int savedPosition)
    {
        this.savedPosition = savedPosition;
    }

    public double getSunrise() { return sunrise; }

    public void setSunrise(double sunrise) { this.sunrise = sunrise; }

    public double getSunset() { return sunset; }

    public void setSunset(double sunset) { this.sunset = sunset; }

    /**
     * Indicates whether some other object is "equal to" this one.
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @see #hashCode()
     * @see HashMap
     */
    @Override
    public boolean equals(Object obj)
    {
        //If the object is compared with itself(in memory reference) then return true
        if(this == obj) return true;
        if(obj == null) return false;

        /*
         * Use getClass instead of instanceOf when the class has not been
         * declared final. This is done because using instanceOf can break the
         * symmetric principal. Meaning if x.equals(y) == true but y.equals(x)
         * could be false. This, however means that if this class gets extended
         * and trivially (i.e. is basically still the same) changed it will
         * return false using getClass which will not happen using instanceOf.
         */
        if(this.getClass() != obj.getClass()) return false;

        Tile that = (Tile) obj;
        return this.cityId == that.cityId;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash =  31 * hash + (int) cityId;
        return hash;
    }

    /**
     * Send back a field value depending on the sort. This is use for
     * the binarySearch algorithm in the PlaceTiles class.
     *
     * @param sortType an int detonating the user selected sort.
     * @return an int with the correct field value.
     */
    public int getComparableField(@SortType int sortType)
    {
        switch(sortType)
        {
            case SortDef.TEMP_ASCENDING:
            case SortDef.TEMP_DESCENDING: return getTempCelsius();
            case SortDef.DAYTIME:
            case SortDef.NIGHTTIME: return (getDayTime()) ? 1 : 0;
            default: return -1;
        }
    }
}