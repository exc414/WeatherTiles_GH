package io.bluephoenix.weathertiles.core.data.remote.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class ApiResponseCurrent
{
    @SerializedName("data")
    @Expose
    private List<Data> data = null;

    public List<Data> getData()
    {
        return data;
    }

    public void setData(List<Data> data)
    {
        this.data = data;
    }

    public class Data
    {
        @SerializedName("temp")
        @Expose
        private double temp;

        @SerializedName("ts")
        @Expose
        private long timestamp;

        @SerializedName("lat")
        @Expose
        private String lat;

        @SerializedName("lon")
        @Expose
        private String lon;

        @SerializedName("timezone")
        @Expose
        private String timezone;

        @SerializedName("city_name")
        @Expose
        private String cityName;

        @SerializedName("country_code")
        @Expose
        private String countryCode;

        @SerializedName("weather")
        @Expose
        private WeatherCurrent weatherCurrent;

        public double getTemp()
        {
            return temp;
        }

        public void setTemp(double temp)
        {
            this.temp = temp;
        }

        public long getTimestamp()
        {
            return timestamp;
        }

        public void setTimestamp(long timestamp)
        {
            this.timestamp = timestamp;
        }

        public String getLat()
        {
            return lat;
        }

        public void setLat(String lat)
        {
            this.lat = lat;
        }

        public String getLon()
        {
            return lon;
        }

        public void setLon(String lon)
        {
            this.lon = lon;
        }

        public String getTimezone()
        {
            return timezone;
        }

        public void setTimezone(String timezone)
        {
            this.timezone = timezone;
        }

        public String getCityName()
        {
            return cityName;
        }

        public void setCityName(String cityName)
        {
            this.cityName = cityName;
        }

        public String getCountryCode()
        {
            return countryCode;
        }

        public void setCountryCode(String countryCode)
        {
            this.countryCode = countryCode;
        }

        public WeatherCurrent getWeatherCurrent()
        {
            return weatherCurrent;
        }

        public void setWeatherCurrent(WeatherCurrent weatherCurrent)
        {
            this.weatherCurrent = weatherCurrent;
        }
    }

    public class WeatherCurrent
    {
        @SerializedName("code")
        @Expose
        private String weatherId;

        @SerializedName("description")
        @Expose
        private String description;

        public String getWeatherId()
        {
            return weatherId;
        }

        public void setWeatherId(String weatherId)
        {
            this.weatherId = weatherId;
        }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }
    }
}
