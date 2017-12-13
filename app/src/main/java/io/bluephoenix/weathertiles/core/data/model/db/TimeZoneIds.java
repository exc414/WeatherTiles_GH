package io.bluephoenix.weathertiles.core.data.model.db;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * @author Carlos A. Perez
 */
public class TimeZoneIds extends RealmObject
{
    @Index
    @PrimaryKey
    private long cityId;

    @Required
    private String timezone;

    public long getCityId()
    {
        return cityId;
    }

    public void setCityId(long cityId)
    {
        this.cityId = cityId;
    }

    public String getTimezone()
    {
        return timezone;
    }

    public void setTimezone(String timezone)
    {
        this.timezone = timezone;
    }
}
