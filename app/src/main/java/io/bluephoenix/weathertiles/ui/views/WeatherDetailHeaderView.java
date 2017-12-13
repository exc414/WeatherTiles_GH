package io.bluephoenix.weathertiles.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.Locale;

import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.util.Constant;
import io.bluephoenix.weathertiles.util.Util;

/**
 * @author Carlos A. Perez
 */
public class WeatherDetailHeaderView extends BaseView
{
    private int width;
    private int height;

    private final int weatherIconSize = Util.getPixelFromDP(44); //DP
    private final int tempSize = Util.getPixelFromDP(30);
    private final int citySize = Util.getPixelFromDP(14);
    private final int provinceCountrySize = Util.getPixelFromDP(12);
    private final int minMaxTempSize = Util.getPixelFromDP(12);
    private final int rainWindHumValueSize = Util.getPixelFromDP(30);
    private final int rainWindHumHeaderSize = Util.getPixelFromDP(12);

    private final int defaultColor = Util.getColorFromResource(R.color.colorAccentMain);
    private final int maxTempColor = Util.getColorFromResource(R.color.sunYellow);
    private final int minTempColor = Util.getColorFromResource(R.color.moonBlue);

    private Paint weatherTextPaint;
    private Paint tempPaint;
    private Paint maxTempPaint;
    private Paint minTempPaint;
    private Paint weatherDescPaint;
    private Paint cityPaint;
    private Paint provinceCountryPaint;
    private Paint rainWindHumValuePaint;
    private Paint rainWindHumHeaderPaint;

    private String tempContent = "";
    private String maxTempContent = "";
    private String minTempContent = "";

    private String weatherDescContent = "";
    private String cityContent = "";
    private String provinceCountryContent = "";

    private String rainContent = "";
    private String windContent = "";
    private String humidityContent = "";

    private String rainHeaderContent = "Rain %";
    private String windHeaderContent = "Wind M/S";
    private String humidityHeaderContent = "Humidity %";

    private float iconX = 0;
    private float iconY = 0;
    private float tempX = 0;
    private float tempY = 0;
    private float maxTempX = 0;
    private float maxTempY = 0;
    private float minTempX = 0;
    private float minTempY = 0;

    private float weatherDescX = 0;
    private float cityX = 0;
    private float provinceCountryX = 0;
    private float weatherDescY = 0;
    private float cityY = 0;
    private float provinceCountryY = 0;

    private float rainHeaderX = 0;
    private float windHeaderX = 0;
    private float humidityHeaderX = 0;

    private float windValueX = 0;
    private float humidityValueX = 0;
    private float rainValueX = 0;


    public WeatherDetailHeaderView(Context context) { super(context); }

    public WeatherDetailHeaderView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        width = (int) Util.getScreenWidthInPixels();
        setTypefaces(context);
        setPaintProperties();
    }

    private void setPaintProperties()
    {
        weatherTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        weatherTextPaint.setTextSize(weatherIconSize);
        weatherTextPaint.setColor(defaultColor);
        weatherTextPaint.setTextAlign(Paint.Align.CENTER);
        weatherTextPaint.setTypeface(weatherIconTypeface);

        tempPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tempPaint.setTextSize(tempSize);
        tempPaint.setColor(defaultColor);
        tempPaint.setTextAlign(Paint.Align.CENTER);
        tempPaint.setTypeface(montserratRegular);

        maxTempPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maxTempPaint.setTextSize(minMaxTempSize);
        maxTempPaint.setColor(maxTempColor);
        maxTempPaint.setTextAlign(Paint.Align.CENTER);
        maxTempPaint.setTypeface(montserratSemiBold);

        minTempPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minTempPaint.setTextSize(minMaxTempSize);
        minTempPaint.setColor(minTempColor);
        minTempPaint.setTextAlign(Paint.Align.CENTER);
        minTempPaint.setTypeface(montserratSemiBold);

        weatherDescPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        weatherDescPaint.setTextSize(citySize);
        weatherDescPaint.setColor(defaultColor);
        weatherDescPaint.setTextAlign(Paint.Align.LEFT);
        weatherDescPaint.setTypeface(montserratRegular);

        cityPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cityPaint.setTextSize(citySize);
        cityPaint.setColor(defaultColor);
        cityPaint.setTextAlign(Paint.Align.LEFT);
        cityPaint.setTypeface(montserratSemiBold);

        provinceCountryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        provinceCountryPaint.setTextSize(provinceCountrySize);
        provinceCountryPaint.setColor(defaultColor);
        provinceCountryPaint.setTextAlign(Paint.Align.LEFT);
        provinceCountryPaint.setTypeface(montserratRegular);

        rainWindHumValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rainWindHumValuePaint.setTextSize(rainWindHumValueSize);
        rainWindHumValuePaint.setColor(defaultColor);
        rainWindHumValuePaint.setTextAlign(Paint.Align.CENTER);
        rainWindHumValuePaint.setTypeface(montserratRegular);

        rainWindHumHeaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rainWindHumHeaderPaint.setTextSize(rainWindHumHeaderSize);
        rainWindHumHeaderPaint.setColor(defaultColor);
        rainWindHumHeaderPaint.setTextAlign(Paint.Align.CENTER);
        rainWindHumHeaderPaint.setTypeface(montserratRegular);

        float weatherSpacing = weatherTextPaint.getFontSpacing();
        int marginY = Util.getPixelFromDP(3);

        float colWidth = width / 5;
        float colWidthCenter = colWidth / 2;

        iconX = colWidth - Util.getPixelFromDP(14);
        iconY = weatherSpacing - Util.getPixelFromDP(2);

        weatherDescX = (colWidth * 1.5F) + Util.getPixelFromDP(8);
        cityX = weatherDescX;
        provinceCountryX = weatherDescX;

        cityY = iconY - ((iconY / 2) - (cityPaint.getFontSpacing() + marginY));
        weatherDescY = cityY - (weatherDescPaint.getFontSpacing() + marginY);
        provinceCountryY = cityY + provinceCountryPaint.getFontSpacing() + marginY;

        tempX = iconX;
        tempY = iconY + tempPaint.getFontSpacing() + Util.getPixelFromDP(30);

        maxTempX = colWidthCenter + maxTempPaint.getFontSpacing();
        maxTempY = tempY + maxTempPaint.getFontSpacing() + marginY;

        minTempX = maxTempX + (minTempPaint.getFontSpacing() * 1.5F);
        minTempY = tempY + minTempPaint.getFontSpacing() + marginY;

        rainHeaderX = iconX * 2.25F;
        windHeaderX = iconX * 3.65F;
        humidityHeaderX = iconX * 5.25F;

        rainValueX = rainHeaderX;
        windValueX = windHeaderX;
        humidityValueX = humidityHeaderX;

        height = (int) (maxTempY + Util.getPixelFromDP(28));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec)
    { return resolveSizeAndState(width, measureSpec, 0); }

    private int measureHeight(int measureSpec)
    { return resolveSizeAndState(height, measureSpec, 0); }

    @Override
    protected void onDraw(Canvas canvas) { drawText(canvas); }

    private void drawText(Canvas canvas)
    {
        canvas.drawText(weatherIconContent, iconX, iconY, weatherTextPaint);

        //Temp Block
        canvas.drawText(tempContent, tempX, tempY, tempPaint);
        canvas.drawText(maxTempContent, maxTempX, maxTempY, maxTempPaint);
        canvas.drawText(minTempContent, minTempX, minTempY, minTempPaint);

        //Description, Location
        canvas.drawText(weatherDescContent, weatherDescX, weatherDescY, weatherDescPaint);
        canvas.drawText(cityContent, cityX, cityY, cityPaint);
        canvas.drawText(provinceCountryContent, provinceCountryX, provinceCountryY,
                provinceCountryPaint);

        //Values - Rain, Wind, Humidity
        canvas.drawText(rainContent, rainValueX, tempY, rainWindHumValuePaint);
        canvas.drawText(windContent, windValueX, tempY, rainWindHumValuePaint);
        canvas.drawText(humidityContent, humidityValueX, tempY, rainWindHumValuePaint);

        //Header - Rain, Wind, Humidity
        canvas.drawText(rainHeaderContent, rainHeaderX, maxTempY, rainWindHumHeaderPaint);
        canvas.drawText(windHeaderContent, windHeaderX, maxTempY, rainWindHumHeaderPaint);
        canvas.drawText(humidityHeaderContent, humidityHeaderX, maxTempY,
                rainWindHumHeaderPaint);

    }

    public void setWeatherIconContent(int weatherId, boolean isDayTime)
    {
        String timeOfDayContent;

        if(isDayTime)
        {
            timeOfDayContent = "wi_owm_day_";
            weatherTextPaint.setColor(Util.getColorFromResource(R.color.sunYellow));
        }
        else
        {
            timeOfDayContent = "wi_owm_night_";
            weatherTextPaint.setColor(Util.getColorFromResource(R.color.moonBlue));
        }

        setWeatherIcon(timeOfDayContent, weatherId);
    }

    public void setTemp(int temp)
    {
        this.tempContent = String.valueOf(temp) + Constant.degreeSymbol;
    }

    public void setMaxTemp(int maxTemp)
    {
        //If -999 no value was calculated.
        if(maxTemp != -999)
        { maxTempContent = String.valueOf(maxTemp) + Constant.degreeSymbol; }

    }

    public void setMinTemp(int minTemp)
    {
        //If 999 no value was calculated.
        if(minTemp != 999)
        { minTempContent = String.valueOf(minTemp) + Constant.degreeSymbol; }
    }

    public void setWeatherDesc(String weatherDesc)
    {
        String firstLetterCap = weatherDesc.substring(0, 1).toUpperCase();
        weatherDescContent = firstLetterCap + weatherDesc.substring(1);
    }

    public void setCity(String city)
    {
        cityContent = city;
    }

    public void setProvince(String province, String country)
    {
        provinceCountryContent = province + ", " + country;
    }

    public void setRainChance(int rainChance)
    {
        rainContent = String.valueOf(rainChance);
    }

    public void setWindSpeed(double windSpeed)
    {
        windContent = String.format(Locale.getDefault(), "%.1f", windSpeed);
    }

    public void setHumidity(int humidity)
    {
        humidityContent = String.valueOf(humidity);
    }
}