package io.bluephoenix.weathertiles.core.data.model.db;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class Cities extends RealmObject
{
    @PrimaryKey
    private long cityId;

    @Index
    @Required
    private String city;

    @Required
    private String region;

    @Required
    private String country;

    @Index
    @Required
    private String iso;

    //Sort the city depending how large/popular it is.
    private byte sortWeight = 0;

    public long getCityId()
    {
        return cityId;
    }

    public void setCityId(long cityId)
    {
        this.cityId = cityId;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getIso()
    {
        return iso;
    }

    public void setIso(String iso)
    {
        this.iso = iso;
    }

    public byte getSortWeight()
    {
        return sortWeight;
    }

    public void setSortWeight(byte sortWeight)
    {
        this.sortWeight = sortWeight;
    }
}
