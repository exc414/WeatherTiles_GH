package io.bluephoenix.weathertiles.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.util.Constant;
import io.bluephoenix.weathertiles.util.Util;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class WeatherView extends BaseView
{
    //Tile ID
    private long cityId;

    private final int defColor = Util.getColorFromResource(R.color.colorAccent);

    //Depending on the size of the device set the correct number of columns.
    //3 default, 5 and 7 for 600dp and 820dp respectively.
    private int numberOfColumns = 3;

    //Tile width and height based on the number of columns passed
    private float tileWidth;
    private float tileHeight;
    private float centerWidth;

    //Content values
    private String tempContent = "";
    private String cityContent = "";
    private String countryContent = "";

    //Font sizes based on DP NOT SP
    private int weatherIconTextSize = 0;
    private int tempTextSize = 0;
    private int cityTextSize = 0;
    private int countryTextSize = 0;

    //Colors
    private int weatherIconColor = 0;
    private int tempColor = 0;
    private int cityColor = 0;
    private int countryColor = 0;

    //Paint
    private Paint weatherTextPaint;
    private Paint tempTextPaint;
    private Paint cityTextPaint;
    private Paint countryTextPaint;

    //Bounds
    private Rect weatherBounds = new Rect();
    private Rect tempBounds = new Rect();
    private Rect cityBounds = new Rect();
    private Rect countryBounds = new Rect();

    //Spacing
    private int spaceAfterIcon = 0;
    private int spaceAfterTemp = 0;
    private int spaceAfterCity = 0;
    private int spaceAfterCountry = 0;

    //Font spacing
    private float weatherIconFontSpacing = 0;
    private float tempFontSpacing = 0;
    private float cityFontSpacing = 0;
    private float countryFontSpacing = 0;

    //Keeps track the total item height
    private float runningHeight = 0;

    //Margin/Padding offset. Takes into consideration the amount of padding the
    //inside tiles have.
    private int marginOffset;

    /**
     * Simple constructor to use when creating a view from code.
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public WeatherView(Context context)
    {
        super(context);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p>
     * The method onFinishInflate() will be called after all children have been
     * added.
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public WeatherView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Get all the values from the xml file if none are giving set defaults.
     * Set the typeface for the different text elements.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    private void init(Context context, AttributeSet attrs)
    {
        int defTextSize = 16;
        //Get all the values set in the xml file.
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeatherView,
                0, 0);

        weatherIconContent = ta.getString(R.styleable.WeatherView_weatherIconContent);

        weatherIconTextSize = ta.getDimensionPixelSize(
                R.styleable.WeatherView_weatherIconTextSize, defTextSize);
        weatherIconColor = ta.getColor(R.styleable.WeatherView_weatherIconColor, defColor);

        tempContent = ta.getString(R.styleable.WeatherView_tempContent);
        tempTextSize = ta.getDimensionPixelSize(
                R.styleable.WeatherView_tempTextSize, defTextSize);
        tempColor = ta.getColor(R.styleable.WeatherView_tempColor, defColor);

        cityContent = ta.getString(R.styleable.WeatherView_cityContent);
        cityTextSize = ta.getDimensionPixelSize(
                R.styleable.WeatherView_cityTextSize, defTextSize);
        cityColor = ta.getColor(R.styleable.WeatherView_cityColor, defColor);

        countryContent = ta.getString(R.styleable.WeatherView_countryContent);
        countryTextSize = ta.getDimensionPixelSize(
                R.styleable.WeatherView_countryTextSize, defTextSize);
        countryColor = ta.getColor(R.styleable.WeatherView_countryColor, defColor);

        //Spacing
        spaceAfterIcon = ta.getDimensionPixelSize(R.styleable.WeatherView_spaceAfterIcon, 0);
        spaceAfterTemp = ta.getDimensionPixelSize(R.styleable.WeatherView_spaceAfterTemp, 0);
        spaceAfterCity = ta.getDimensionPixelSize(R.styleable.WeatherView_spaceAfterCity, 0);
        spaceAfterCountry = ta.getDimensionPixelSize(
                R.styleable.WeatherView_spaceAfterCountry, 0);

        numberOfColumns = ta.getInteger(R.styleable.WeatherView_numberOfColumns, 3);

        //Total margin offset - adds all the tile margins plus the parent margins
        marginOffset = ta.getDimensionPixelSize(R.styleable.WeatherView_marginOffset, 0);

        //TypedArrays are heavyweight objects that should be recycled immediately
        //after all the attributes you need have been extracted.
        ta.recycle();

        //Order matters paint properties must be set before calculating the height
        setTypefaces(context);
        setPaintProperties();
        setWidth();
        setHeight();
    }

    /**
     * Sets the paint object properties
     */
    private void setPaintProperties()
    {
        weatherTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        weatherTextPaint.setTextSize(weatherIconTextSize);
        weatherTextPaint.setColor(weatherIconColor);
        weatherTextPaint.setTextAlign(Paint.Align.CENTER);
        weatherTextPaint.setTypeface(weatherIconTypeface);

        tempTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tempTextPaint.setTextSize(tempTextSize);
        tempTextPaint.setColor(tempColor);
        tempTextPaint.setTextAlign(Paint.Align.CENTER);
        tempTextPaint.setTypeface(montserratRegular);

        cityTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cityTextPaint.setTextSize(cityTextSize);
        cityTextPaint.setColor(cityColor);
        cityTextPaint.setTextAlign(Paint.Align.CENTER);
        cityTextPaint.setTypeface(montserratRegular);

        countryTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        countryTextPaint.setTextSize(countryTextSize);
        countryTextPaint.setColor(countryColor);
        countryTextPaint.setTextAlign(Paint.Align.CENTER);
        countryTextPaint.setTypeface(montserratSemiBold);

        //Get font spacing
        weatherIconFontSpacing = weatherTextPaint.getFontSpacing();
        tempFontSpacing = tempTextPaint.getFontSpacing();
        cityFontSpacing = cityTextPaint.getFontSpacing();
        countryFontSpacing = countryTextPaint.getFontSpacing();
    }

    private void setWidth()
    {
        //Set total width and center width (for element centering)
        //Total width is determine by the number of columns required. Default/Min is 3.
        tileWidth = Util.getTileWidthPixel(numberOfColumns);
        centerWidth = (tileWidth - marginOffset) / 2f;
    }

    //On measure gets called multiple times therefore I do calculations outside
    //of it then set the height var once.
    private void setHeight()
    {
        //Set the tile height using the spacing from xml and the font spacing
        float allSpace = spaceAfterIcon + spaceAfterTemp + spaceAfterCity + spaceAfterCountry;
        float fontSpacing = weatherIconFontSpacing + tempFontSpacing + cityFontSpacing
                + countryFontSpacing;
        tileHeight = (int) (allSpace + fontSpacing);
    }

    /**
     * Called to determine the size requirements for this view and all of its children.
     *
     * @param widthMeasureSpec  width for the view.
     * @param heightMeasureSpec height for the view.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec)
    {
        return resolveSizeAndState((int) tileWidth, measureSpec, 0);
    }

    private int measureHeight(int measureSpec)
    {
        return resolveSizeAndState((int) tileHeight, measureSpec, 0);
    }

    /**
     * Called when the view should render its content.
     * @param canvas to host the draw calls (writing into the bitmap)
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        drawWeatherIcon(canvas);
        drawTempText(canvas);
        drawCityText(canvas);
        drawCountryText(canvas);

        //Stops re-calc if the view is already store in the cache.
        setDrawingCacheEnabled(true);
    }

    /**
     * canvas.drawText method variables explanation.
     * <p>
     * Text is what to draw.
     * centerWidth is the location on the x axis. (in this case the center of the view)
     * runningHeight is the location on the y axis. We keep adding all the previous views
     * height and spacing as we draw views.
     * TextPaint object is need it to draw.
     * <p>
     * In getTextBounds 0 and text.length are the number of characters and therefore width that
     * is needed to draw the text.
     *
     * @param canvas The Canvas class holds the "draw" calls.
     */
    private void drawWeatherIcon(Canvas canvas)
    {

        runningHeight = weatherIconFontSpacing;
        canvas.drawText(weatherIconContent, centerWidth, runningHeight, weatherTextPaint);
    }

    private void drawTempText(Canvas canvas)
    {

        runningHeight = runningHeight + tempFontSpacing + spaceAfterIcon;
        canvas.drawText(tempContent, centerWidth, runningHeight, tempTextPaint);
    }

    private void drawCityText(Canvas canvas)
    {
        runningHeight = runningHeight + cityFontSpacing + spaceAfterTemp;
        canvas.drawText(cityContent, centerWidth, runningHeight, cityTextPaint);
    }

    private void drawCountryText(Canvas canvas)
    {
        runningHeight = runningHeight + countryFontSpacing + spaceAfterCity;
        canvas.drawText(countryContent, centerWidth, runningHeight, countryTextPaint);
    }

    public long getCityId()
    {
        return cityId;
    }

    public void setCityId(long cityId)
    {
        this.cityId = cityId;
    }

    /**
     * Depending on whether is daytime or nighttime a different weather icon color is used.
     * If the weatherIconContent value is empty don't try to get a resource.
     * The default value of timeOfDayContent is the neutral - wi_owm_.
     *
     * @param weatherId a int which determines the type of weather icon shown.
     * @param isDayTime a boolean whether the icon should be painted
     *                  yellow (day) or blue (night)
     */
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
        weatherTextPaint.getTextBounds(weatherIconContent, 0, weatherIconContent.length(),
                weatherBounds);
    }

    /**
     * Set the temperature value.
     * @param tempContentSet int which display the temp of a city in the tile.
     */
    public void setTempContent(int tempContentSet)
    {
        this.tempContent = String.valueOf(tempContentSet) + Constant.degreeSymbol;
        tempTextPaint.getTextBounds(tempContent, 0, tempContent.length(), tempBounds);
    }

    /**
     * While drawing text, some city's name may be to large for the tile. Cut the
     * string using string.split() and build a string that fits the tile using
     * string.builder().
     *
     * @param cityContentSet A string with the content passed into the view.
     */
    public void setCityContent(String cityContentSet)
    {
        this.cityContent = cityContentSet;

        cityTextPaint.getTextBounds(cityContent, 0, cityContent.length(), cityBounds);
        float tileWidthWithInnerPadding = tileWidth - Util.getTileInnerPadding();

        int counter = 0;

        if(tileWidthWithInnerPadding < cityBounds.width())
        {
            StringBuilder builder;
            String[] words;
            String cutName = "";

            //Check the type of name that it is. Sometimes they have - (hyphens)
            //and simply splicing by spaces wont do the trick.
            if(cityContent.contains("-")) { words = cityContent.split("-"); }
            else
            {
                words = cityContent.split(" ");
            }

            while(tileWidthWithInnerPadding < cityBounds.width())
            {
                //Must rebuilding the STB or it will keep adding strings to the previous
                //STB object.
                builder = new StringBuilder();

                for(int i = 0; i < words.length - counter; i++)
                {
                    builder.append(words[i]);
                    builder.append(" ");
                }

                cutName = builder.toString() + "...";
                cityTextPaint.getTextBounds(cutName, 0, cutName.length(), cityBounds);
                //Update the counter every time a pass completes to remove a word
                //from the city name.
                counter++;
            }

            this.cityContent = cutName;
        }
    }

    public void setCountryContent(String countryContentSet)
    {
        this.countryContent = countryContentSet;
        countryTextPaint.getTextBounds(countryContent, 0,
                countryContent.length(), countryBounds);
    }
}