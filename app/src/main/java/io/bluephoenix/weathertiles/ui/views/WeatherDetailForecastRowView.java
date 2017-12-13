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
public class WeatherDetailForecastRowView extends BaseView
{
    private int width;
    private int height;

    private String timeShown = "01AM";
    private String temp = "0";
    private String windSpeed = "0";
    private String humidity = "0";
    private String rainChange = "Rain";

    private Paint weatherIconPaint;
    private Paint valuesPaint;
    private Paint valuesHeadingPaint;

    private final int textIconSize = Util.getPixelFromDP(26); //DP
    private final int textValueSize = Util.getPixelFromDP(13);
    private final int textValueHeadingSize = Util.getPixelFromDP(10);

    private final int defaultColor = Util.getColorFromResource(R.color.colorAccentMain);

    private float iconPosY = 0;
    private float headingPosY = 0;
    private float valuesPosY = 0;

    private float iconPosX = 0;
    private float tempBlockPosX = 0;
    private float rainBlockPosX = 0;
    private float windBlockPosX = 0;
    private float humidityBlockPosX = 0;

    public WeatherDetailForecastRowView(Context context)
    {
        super(context);
    }

    public WeatherDetailForecastRowView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        setTypefaces(context);
        setPaintProperties();
    }

    private void setPaintProperties()
    {
        width = (int) Util.getScreenWidthInPixels();

        weatherIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        weatherIconPaint.setTextSize(textIconSize);
        weatherIconPaint.setColor(defaultColor);
        weatherIconPaint.setTextAlign(Paint.Align.CENTER);
        weatherIconPaint.setTypeface(weatherIconTypeface);

        valuesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuesPaint.setTextSize(textValueSize);
        valuesPaint.setColor(defaultColor);
        valuesPaint.setTextAlign(Paint.Align.LEFT);
        valuesPaint.setTypeface(montserratRegular);

        valuesHeadingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valuesHeadingPaint.setTextSize(textValueHeadingSize);
        valuesHeadingPaint.setColor(defaultColor);
        valuesHeadingPaint.setTextAlign(Paint.Align.LEFT);
        valuesHeadingPaint.setTypeface(montserratRegular);

        height = Util.getPixelFromDP(24) + (int) weatherIconPaint.getFontSpacing();
        iconPosY = (height / 2F) + (weatherIconPaint.getFontSpacing() / 4);

        float valuesHeadingFontSpacing = valuesHeadingPaint.getFontSpacing();
        float posX = (Util.getScreenWidthInPixels() / 5F); //Sections of values;
        //5 = Number of blocks
        iconPosX = (Util.getScreenWidthInPixels() / 5F) / 2; //center
        tempBlockPosX = (posX + iconPosX) - valuesHeadingFontSpacing; //left aligned
        rainBlockPosX = ((posX * 2) + iconPosX) - valuesHeadingFontSpacing; //left aligned
        windBlockPosX = ((posX * 3) + iconPosX) - (valuesHeadingFontSpacing * 2); //centered
        humidityBlockPosX = ((posX * 4) + iconPosX) - (valuesHeadingFontSpacing * 2); //centered

        headingPosY = (height / 2) + valuesHeadingPaint.getFontSpacing();
        valuesPosY = (headingPosY - valuesHeadingPaint.getFontSpacing())
                - Util.getPixelFromDP(2);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec)
    {
        return resolveSizeAndState(width, measureSpec, 0);
    }

    private int measureHeight(int measureSpec)
    {
        return resolveSizeAndState(height, measureSpec, 0);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        drawText(canvas);
    }

    private void drawText(Canvas canvas)
    {
        canvas.drawText(weatherIconContent, iconPosX, iconPosY, weatherIconPaint);

        canvas.drawText(temp, tempBlockPosX, valuesPosY, valuesPaint);
        canvas.drawText(timeShown, tempBlockPosX, headingPosY, valuesHeadingPaint);

        canvas.drawText(rainChange, rainBlockPosX, valuesPosY, valuesPaint);
        canvas.drawText("Rain", rainBlockPosX, headingPosY, valuesHeadingPaint);

        canvas.drawText(windSpeed, windBlockPosX, valuesPosY, valuesPaint);
        canvas.drawText("Wind", windBlockPosX, headingPosY, valuesHeadingPaint);

        canvas.drawText(humidity, humidityBlockPosX, valuesPosY, valuesPaint);
        canvas.drawText("Humidity", humidityBlockPosX, headingPosY, valuesHeadingPaint);
    }

    public void setWeatherIconContent(int weatherId, boolean isDayTime)
    {
        String timeOfDayContent;

        if(isDayTime) { timeOfDayContent = "wi_owm_day_"; }
        else { timeOfDayContent = "wi_owm_night_"; }

        setWeatherIcon(timeOfDayContent, weatherId);
    }

    public void setTemp(int temp)
    {
        this.temp = String.valueOf(temp) + Constant.degreeSymbol;
    }

    public void setTimeShown(String timeShown)
    {
        this.timeShown = timeShown;
    }

    public void setWindSpeed(double windSpeed)
    {
        this.windSpeed = String.format(Locale.getDefault(), "%.1f", windSpeed) + " M/S";
    }

    public void setHumidity(int humidity)
    {
        this.humidity = String.valueOf(humidity) + "%";
    }

    public void setRainChange(int rainChange)
    {
        this.rainChange = String.valueOf(rainChange) + "%";
    }
}
