package io.bluephoenix.weathertiles.core.data.remote.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class ApiResponseFiveDay
{
    @SerializedName("list")
    @Expose
    private java.util.List<List> list = null;

    @SerializedName("city")
    @Expose
    private City city;

    public java.util.List<List> getList()
    {
        return list;
    }

    public void setList(java.util.List<List> list)
    {
        this.list = list;
    }

    public City getCity()
    {
        return city;
    }

    public void setCity(City city)
    {
        this.city = city;
    }

    public class List
    {
        @SerializedName("dt")
        @Expose
        private long timestamp;

        @SerializedName("main")
        @Expose
        private Main main;

        @SerializedName("weather")
        @Expose
        private java.util.List<Weather> weather = null;
        @SerializedName("clouds")
        @Expose
        private Clouds clouds;

        @SerializedName("wind")
        @Expose
        private Wind wind;

        @SerializedName("rain")
        @Expose
        private Rain rain;

        public long getTimestamp()
        {
            return timestamp;
        }

        public void setTimestamp(long dt)
        {
            this.timestamp = dt;
        }

        public Main getMain()
        {
            return main;
        }

        public void setMain(Main main)
        {
            this.main = main;
        }

        public java.util.List<Weather> getWeather()
        {
            return weather;
        }

        public void setWeather(java.util.List<Weather> weather)
        {
            this.weather = weather;
        }

        public Clouds getClouds()
        {
            return clouds;
        }

        public void setClouds(Clouds clouds)
        {
            this.clouds = clouds;
        }

        public Wind getWind()
        {
            return wind;
        }

        public void setWind(Wind wind)
        {
            this.wind = wind;
        }

        public Rain getRain()
        {
            return rain;
        }

        public void setRain(Rain rain)
        {
            this.rain = rain;
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

            @SerializedName("temp_min")
            @Expose
            private double tempMin;

            @SerializedName("temp_max")
            @Expose
            private double tempMax;

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

            public double getTempMin()
            {
                return tempMin;
            }

            public void setTempMin(double tempMin)
            {
                this.tempMin = tempMin;
            }

            public double getTempMax()
            {
                return tempMax;
            }

            public void setTempMax(double tempMax)
            {
                this.tempMax = tempMax;
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

        public class Clouds
        {
            @SerializedName("all")
            @Expose
            private int cloudiness;

            public int getCloudiness()
            {
                return cloudiness;
            }

            public void setCloudiness(int cloudiness)
            {
                this.cloudiness = cloudiness;
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

        public class Rain
        {
            @SerializedName("3h")
            @Expose
            private double rainFall3h;

            public double getRainFall3h()
            {
                return rainFall3h;
            }

            public void setRainFall3h(double rainFall3h)
            {
                this.rainFall3h = rainFall3h;
            }
        }
    }

    public class City
    {
        @SerializedName("id")
        @Expose
        private int cityId;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("coord")
        @Expose
        private Coordinates coordinates;

        @SerializedName("country")
        @Expose
        private String country;

        public int getCityId()
        {
            return cityId;
        }

        public void setCityId(int cityId)
        {
            this.cityId = cityId;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public Coordinates getCoordinates()
        {
            return coordinates;
        }

        public void setCoordinates(Coordinates coordinates)
        {
            this.coordinates = coordinates;
        }

        public String getCountry()
        {
            return country;
        }

        public void setCountry(String country)
        {
            this.country = country;
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
    }
}
