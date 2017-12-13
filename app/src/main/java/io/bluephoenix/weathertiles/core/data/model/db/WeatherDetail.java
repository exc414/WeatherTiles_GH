package io.bluephoenix.weathertiles.core.data.model.db;

import android.util.SparseArray;

import java.util.List;

/**
 * @author Carlos A. Perez
 */
public class WeatherDetail
{
    private long cityId;
    //This class does not need temp conversion as before it gets the temp is should
    //be already converted into C or F as per the user's requirements.
    private int temp;
    private int tempMax;
    private int tempMin;
    private int savedPosition;
    private int selectedPosition;
    private int weatherId;
    private String weatherIcon;
    private String cityName;
    private String provinceName;
    private String countryName;
    private String description;
    private int rainChance;
    private double windSpeed;
    private int humidity;
    private boolean isDayTime;
    private SparseArray<List<DetailRows>> detailRows = null;
    private String[] tabContent;

    public long getCityId()
    {
        return cityId;
    }

    public void setCityId(long cityId)
    {
        this.cityId = cityId;
    }

    public int getTemp() { return temp; }

    public void setTemp(int temp)
    {
        this.temp = temp;
    }

    public int getTempMax()
    {
        return tempMax;
    }

    public void setTempMax(int tempMax)
    {
        this.tempMax = tempMax;
    }

    public int getTempMin()
    {
        return tempMin;
    }

    public void setTempMin(int tempMin)
    {
        this.tempMin = tempMin;
    }

    public int getSavedPosition()
    {
        return savedPosition;
    }

    public void setSavedPosition(int savedPosition)
    {
        this.savedPosition = savedPosition;
    }

    public int getWeatherId()
    {
        return weatherId;
    }

    public void setWeatherId(int weatherId)
    {
        this.weatherId = weatherId;
    }

    public String getCityName()
    {
        return cityName;
    }

    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }

    public String getCountryName()
    {
        return countryName;
    }

    public void setCountryName(String countryName)
    {
        this.countryName = countryName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public boolean getDayTime()
    {
        return isDayTime;
    }

    public void setDayTime(boolean dayTime)
    {
        isDayTime = dayTime;
    }

    public int getSelectedPosition()
    {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition)
    {
        this.selectedPosition = selectedPosition;
    }

    public SparseArray<List<DetailRows>> getDetailRows()
    {
        return detailRows;
    }

    public void setDetailRows(SparseArray<List<DetailRows>> detailRows)
    {
        this.detailRows = detailRows;
    }

    public String getProvinceName()
    {
        return provinceName;
    }

    public void setProvinceName(String provinceName)
    {
        this.provinceName = provinceName;
    }

    public int getRainChance()
    {
        return rainChance;
    }

    public void setRainChance(int rainChance)
    {
        this.rainChance = rainChance;
    }

    public double getWindSpeed()
    {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed)
    {
        this.windSpeed = windSpeed;
    }

    public int getHumidity()
    {
        return humidity;
    }

    public void setHumidity(int humidity)
    {
        this.humidity = humidity;
    }

    public String getWeatherIcon()
    {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon)
    {
        this.weatherIcon = weatherIcon;
    }

    public String[] getTabContent()
    {
        return tabContent;
    }

    public void setTabContent(String[] tabContent)
    {
        this.tabContent = tabContent;
    }


    public class DetailRows
    {
        private int weatherId;
        private int temp;
        private String timeShown;
        private int rain;
        private double wind;
        private int humidity;
        private boolean isDayTime;

        public int getWeatherId()
        {
            return weatherId;
        }

        public void setWeatherId(int weatherId)
        {
            this.weatherId = weatherId;
        }

        public int getTemp() { return temp; }

        public void setTemp(int temp) { this.temp = temp; }

        public String getTimeShown() { return timeShown; }

        public int getRain()
        {
            return rain;
        }

        public void setRain(int rain)
        {
            this.rain = rain;
        }

        public void setTimeShown(String timeShown)
        {
            this.timeShown = timeShown;
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

        public boolean getIsDayTime()
        {
            return isDayTime;
        }

        public void setIsDayTime(boolean dayTime)
        {
            isDayTime = dayTime;
        }

    }
}
