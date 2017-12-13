package io.bluephoenix.weathertiles.ui.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import io.bluephoenix.weathertiles.util.Constant;

/**
 * @author Carlos A. Perez
 */
public abstract class BaseView extends View
{
    protected Typeface weatherIconTypeface;
    protected Typeface montserratRegular;
    protected Typeface opensansRegular;
    protected Typeface montserratSemiBold;

    protected String weatherIconContent = "";

    public BaseView(Context context) { super(context); }

    public BaseView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); }

    /**
     * Set the different typefaces for each view.
     * @param context Global information about an application environment.
     */
    protected void setTypefaces(Context context)
    {
        //Set custom fonts
        String PATH_TO_WEATHER_FONT = "fonts/weather.ttf";
        weatherIconTypeface = Typeface.createFromAsset(context.getAssets(),
                PATH_TO_WEATHER_FONT);

        String PATH_TO_MONTSERRAT_REGULAR_FONT = "fonts/Montserrat-Regular.ttf";
        montserratRegular = Typeface.createFromAsset(context.getAssets(),
                PATH_TO_MONTSERRAT_REGULAR_FONT);

        String PATH_TO_OPENSANS_REGULAR_FONT = "fonts/OpenSans-Regular.ttf";
        opensansRegular = Typeface.createFromAsset(context.getAssets(),
                PATH_TO_OPENSANS_REGULAR_FONT);

        String PATH_TO_MONTSERRAT_SEMIBOLD_FONT = "fonts/Montserrat-SemiBold.ttf";
        montserratSemiBold = Typeface.createFromAsset(context.getAssets(),
                PATH_TO_MONTSERRAT_SEMIBOLD_FONT);
    }

    /**
     * Set the weather icon the view(s) will use.
     * @param timeOfDayContent a string weather the icon resource to be retrieve should be
     *                         in daytime or nighttime.
     * @param weatherId an int which detonates what type of icon gets returned.
     */
    protected void setWeatherIcon(String timeOfDayContent, int weatherId)
    {
        String weatherIconContentSet;

        try
        {
            weatherIconContentSet = (getContext().getString(getResources()
                    .getIdentifier(timeOfDayContent + weatherId, "string",
                            getContext().getPackageName())));
        }
        catch(Resources.NotFoundException ex)
        {
            Log.i(Constant.TAG, "Resource from weather id not found :" + weatherId);
            //If not found set the standard icon for the time of day.
            weatherIconContentSet = (getContext().getString(getResources()
                    .getIdentifier(timeOfDayContent + 800, "string",
                            getContext().getPackageName())));
        }

        weatherIconContent = weatherIconContentSet;
    }
}
