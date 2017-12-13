package io.bluephoenix.weathertiles.core.data.remote.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class ApiResponseCurrent
{
    @SerializedName("id")
    @Expose
    private int cityId;

    @SerializedName("coord")
    @Expose
    private Coordinates coord;

    @SerializedName("weather")
    @Expose
    private List<Weather> weather = null;

    @SerializedName("main")
    @Expose
    private Main main;

    @SerializedName("wind")
    @Expose
    private Wind wind;

    @SerializedName("dt")
    @Expose
    private int timestamp;

    @SerializedName("sys")
    @Expose
    private CountryCode countryCode;

    @SerializedName("name")
    @Expose
    private String cityName;

    public int getCityId()
    {
        return cityId;
    }

    public void setCityId(int cityId)
    {
        this.cityId = cityId;
    }

    public Coordinates getCoord()
    {
        return coord;
    }

    public void setCoord(Coordinates coord)
    {
        this.coord = coord;
    }

    public List<Weather> getWeather()
    {
        return weather;
    }

    public void setWeather(List<Weather> weather)
    {
        this.weather = weather;
    }

    public Main getMain()
    {
        return main;
    }

    public void setMain(Main main)
    {
        this.main = main;
    }

    public Wind getWind()
    {
        return wind;
    }

    public void setWind(Wind wind)
    {
        this.wind = wind;
    }

    public int getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(int timestamp)
    {
        this.timestamp = timestamp;
    }

    public CountryCode getCountryCode()
    {
        return countryCode;
    }

    public void setCountryCode(CountryCode countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getCityName()
    {
        return cityName;
    }

    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }

    public class Weather
    {
        @SerializedName("id")
        @Expose
        private int weatherId;

        @SerializedName("main")
        @Expose
        private String mainWeather;

        @SerializedName("description")
        @Expose
        private String description;

        public int getWeatherId() { return weatherId; }

        public void setWeatherId(int weatherId) { this.weatherId = weatherId; }

        public String getDescription()
        {
            return description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public String getMainWeather()
        {
            return mainWeather;
        }

        public void setMainWeather(String mainWeather)
        {
            this.mainWeather = mainWeather;
        }
    }

    public class Main
    {
        @SerializedName("temp")
        @Expose
        private double temp;

        @SerializedName("humidity")
        @Expose
        private int humidity;

        public double getTemp()
        {
            return temp;
        }

        public void setTemp(double temp)
        {
            this.temp = temp;
        }

        public int getHumidity()
        {
            return humidity;
        }

        public void setHumidity(int humidity)
        {
            this.humidity = humidity;
        }
    }

    public class Wind
    {
        @SerializedName("speed")
        @Expose
        private double speed;

        public double getSpeed()
        {
            return speed;
        }

        public void setSpeed(double speed)
        {
            this.speed = speed;
        }
    }

    public class Coordinates
    {
        @SerializedName("lat")
        @Expose
        private double lat;

        @SerializedName("lon")
        @Expose
        private double lon;

        public double getLat()
        {
            return lat;
        }

        public void setLat(Double lat)
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
    }

    public class CountryCode
    {
        @SerializedName("country")
        @Expose
        private String countryCode;

        public String getCountryCode()
        {
            return countryCode;
        }

        public void setCountryCode(String countryCode)
        {
            this.countryCode = countryCode;
        }
    }
}
