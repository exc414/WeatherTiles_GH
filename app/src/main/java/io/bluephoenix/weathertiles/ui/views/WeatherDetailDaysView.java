package io.bluephoenix.weathertiles.ui.views;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.ui.adapters.ITabSelection;
import io.bluephoenix.weathertiles.util.AnimListener;
import io.bluephoenix.weathertiles.util.Util;

/**
 * @author Carlos A. Perez
 */
public class WeatherDetailDaysView extends BaseView
{
    private ITabSelection.DetailsAdapter tabSelectionListenerAdapter;
    private ITabSelection.DetailsForecastAdapter tabSelectionListenerForecastAdapter;

    private int width;
    private int height;

    private float dayCenterWidth;
    private float colWidth;

    private final int btnStandardIconSize = Util.getPixelFromDP(22); //DP
    private final int btnStandardTempSize = Util.getPixelFromDP(14);
    private final int btnStandardDaySize = Util.getPixelFromDP(11);
    private final int btnStandardColor = Util.getColorFromResource(R.color.white);

    private String[] tabContent;

    private Paint[] weatherIconForBtnPaint;
    private Paint[] tempForBtnPaint;
    private Paint[] dayForBtnPaint;
    private Paint bgBtnTabPaint;

    private float textSpacing = Util.getPixelFromDP(20);
    private float textBottomSpacing = Util.getPixelFromDP(12);

    private float heightPosForWeatherIcon;
    private float heightPosForTemp;
    private float heightPosForDay;

    //Detonates the last time the button was clicked/tapped.
    private long lastClickTime = 0;

    //Tab Button Bounds use for the onTouchEvent method
    private float btnTopBound = 0;
    private float btnLeftBound = 0;

    private float bgTopBound = 0;
    private float bgBottomBound = 0;
    private float bgLeftBound = 0;
    private float bgRightBound = 0;
    private float midPointOfNextTab = 0;

    //Default start at the first position
    private int selectedItem = 0;
    private int savedPosition;

    private int standardAlphaValue = 158;
    private int selectedAlphaValue = 240;

    //Number of days that will be shown in the tile detail view.
    int forecastLength = 5;

    public WeatherDetailDaysView(Context context) { super(context); }

    public WeatherDetailDaysView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        setTypefaces(context);
        setPaintProperties();
    }

    private void setPaintProperties()
    {
        width = (int) Util.getScreenWidthInPixels();

        weatherIconForBtnPaint = new Paint[5];
        tempForBtnPaint = new Paint[5];
        dayForBtnPaint = new Paint[5];

        for(int i = 0; i < forecastLength; i++)
        {
            weatherIconForBtnPaint[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            weatherIconForBtnPaint[i].setTextSize(btnStandardIconSize);
            weatherIconForBtnPaint[i].setColor(btnStandardColor);
            weatherIconForBtnPaint[i].setTextAlign(Paint.Align.CENTER);
            weatherIconForBtnPaint[i].setTypeface(weatherIconTypeface);
            weatherIconForBtnPaint[i].setAlpha(standardAlphaValue);

            tempForBtnPaint[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            tempForBtnPaint[i].setTextSize(btnStandardTempSize);
            tempForBtnPaint[i].setColor(btnStandardColor);
            tempForBtnPaint[i].setTextAlign(Paint.Align.CENTER);
            tempForBtnPaint[i].setTypeface(montserratRegular);
            tempForBtnPaint[i].setAlpha(standardAlphaValue);

            dayForBtnPaint[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            dayForBtnPaint[i].setTextSize(btnStandardDaySize);
            dayForBtnPaint[i].setColor(btnStandardColor);
            dayForBtnPaint[i].setTextAlign(Paint.Align.CENTER);
            dayForBtnPaint[i].setTypeface(montserratRegular);
            dayForBtnPaint[i].setAlpha(standardAlphaValue);

            if(selectedItem == i)
            {
                weatherIconForBtnPaint[i].setAlpha(selectedAlphaValue);
                tempForBtnPaint[i].setAlpha(selectedAlphaValue);
                dayForBtnPaint[i].setAlpha(selectedAlphaValue);
            }
        }

        bgBtnTabPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        heightPosForWeatherIcon =  weatherIconForBtnPaint[0].getFontSpacing();
        heightPosForTemp = heightPosForWeatherIcon
                + (weatherIconForBtnPaint[0].getFontSpacing()
                - tempForBtnPaint[0].getFontSpacing() / 2);

        heightPosForDay = heightPosForTemp + textSpacing;
        height = (int) (heightPosForDay + textBottomSpacing);

        colWidth = width / forecastLength;
        dayCenterWidth = colWidth / 2f;

        btnTopBound = heightPosForWeatherIcon - (textBottomSpacing * 2);
        btnLeftBound = colWidth;

        bgTopBound = height - Util.getPixelFromDP(2);
        bgBottomBound = height;
        bgLeftBound = selectedItem * colWidth;
        bgRightBound = (selectedItem + 1) * colWidth;
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
    protected void onDraw(Canvas canvas)
    {
        drawTabButtons(canvas);
    }

    private void drawTabButtons(Canvas canvas)
    {
        for(int i = 0, j = 1; i < tabContent.length; i += 3, j++)
        {
            if((selectedItem + 1) == j)
            {
                //Left, Top, Right, Bottom
                canvas.drawRect(bgLeftBound, bgTopBound, bgRightBound, bgBottomBound,
                        bgBtnTabPaint);
            }

            canvas.drawText(tabContent[i], (j * colWidth) - dayCenterWidth,
                    heightPosForWeatherIcon, weatherIconForBtnPaint[j - 1]);
            canvas.drawText(tabContent[i + 1], (j * colWidth) - dayCenterWidth,
                    heightPosForTemp, tempForBtnPaint[j - 1]) ;
            canvas.drawText(tabContent[i + 2], (j * colWidth) - dayCenterWidth,
                    heightPosForDay, dayForBtnPaint[j - 1]);
        }
    }

    /**
     * Animate the tab change.
     * @param selectedItem int which detonates which day tab has been selected.
     * @param previouslySelectedItem int which detonates the previously selected tab.
     */
    public void underlineAnimation(int selectedItem, int previouslySelectedItem)
    {
        ValueAnimator fadeOutAnim = ValueAnimator.ofInt(255, 0);
        fadeOutAnim.setDuration(250);

        ValueAnimator fadeInAnim = ValueAnimator.ofInt(0, 255);
        fadeInAnim.setDuration(200);

        //Both shrinkAnim and expandAnim only need half the column as they shrink to
        //the middle of the col or expand from the middle of the column.
        ValueAnimator shrinkAnim = ValueAnimator.ofInt(0, (int) (colWidth / 2));
        fadeInAnim.setDuration(150);

        ValueAnimator expandAnim = ValueAnimator.ofInt(0, (int) (colWidth / 2));
        fadeInAnim.setDuration(150);

        //standardAlphaValue - selectedAlphaValue represents the alpha value on the
        //color white. Which gives different types of white (gray). Unselected and selected.
        ValueAnimator textFadeOut = ValueAnimator.ofInt(selectedAlphaValue, standardAlphaValue);
        textFadeOut.setDuration(200);

        ValueAnimator textFadeIn = ValueAnimator.ofInt(standardAlphaValue, selectedAlphaValue);
        textFadeOut.setDuration(200);

        textFadeOut.addUpdateListener(animation ->
        {
            int value = (int) animation.getAnimatedValue();
            weatherIconForBtnPaint[previouslySelectedItem].setAlpha(value);
            tempForBtnPaint[previouslySelectedItem].setAlpha(value);
            dayForBtnPaint[previouslySelectedItem].setAlpha(value);
            postInvalidate();
        });

        fadeOutAnim.addUpdateListener(animation ->
        {
            int value = (int) animation.getAnimatedValue();
            bgBtnTabPaint.setAlpha(value);
            postInvalidate();
        });

        fadeOutAnim.addListener(new AnimListener()
        {
            @Override
            protected void animationStarted(Animator animation) { }

            @Override
            protected void animationEnded(Animator animation)
            {
                //To expand the underline start from the middle outwards.
                //Therefore get the correct tab where it will be shown. Get the
                //left coordinate thus half the col width needs to be added to get
                //to the middle of it.
                midPointOfNextTab = (selectedItem * colWidth) + (colWidth / 2);
                textFadeIn.start();
                fadeInAnim.start();
                expandAnim.start();
            }
        });

        shrinkAnim.addUpdateListener(animation ->
        {
            int value = (int) animation.getAnimatedValue();
            bgLeftBound = (previouslySelectedItem * colWidth) + value;
            bgRightBound = ((previouslySelectedItem + 1) * colWidth) - value;
            postInvalidate();
        });

        textFadeIn.addUpdateListener(animation ->
        {
            int value = (int) animation.getAnimatedValue();
            weatherIconForBtnPaint[selectedItem].setAlpha(value);
            tempForBtnPaint[selectedItem].setAlpha(value);
            dayForBtnPaint[selectedItem].setAlpha(value);
            postInvalidate();
        });

        fadeInAnim.addUpdateListener(animation ->
        {
            int value = (int) animation.getAnimatedValue();
            bgBtnTabPaint.setAlpha(value);
            postInvalidate();
        });

        expandAnim.addUpdateListener(animation ->
        {
            int value = (int) animation.getAnimatedValue();
            bgLeftBound = midPointOfNextTab - value;
            bgRightBound = midPointOfNextTab + value;
            postInvalidate();
        });

        textFadeOut.start();
        fadeOutAnim.start();
        shrinkAnim.start();
        //invalidate();
    }

    /**
     * Depending on when the user clicks on the screen grab the ACTION_DOWN event
     * within the specified bounds.
     *
     * @param motionEvent An object which contains information about a user interaction.
     * @return A boolean detonating where this event was consumed or not.
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        //All heights are the same therefore its our first check.
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN
                && (height >= motionEvent.getY() &&
                btnTopBound < motionEvent.getY()))
        {
            //Once is known the user is tapping one of the buttons then check for the
            //last click time. Cannot do this in the first IF since they could have
            //tapped outside of the buttons bounds.
            //500 = the time it takes all the animations to finish on button press.
            if((SystemClock.elapsedRealtime() - lastClickTime > 500))
            {
                lastClickTime = SystemClock.elapsedRealtime();
                //Final check which is which button out of the five was clicked.
                //Start left to right.
                if(0 <= motionEvent.getX() &&
                        btnLeftBound > motionEvent.getX() && selectedItem != 0)
                {
                    updateSelectedTabPosition(0, selectedItem, savedPosition);
                }
                else if(btnLeftBound <= motionEvent.getX() &&
                        btnLeftBound * 2 > motionEvent.getX() && selectedItem != 1)
                {
                    updateSelectedTabPosition(1, selectedItem, savedPosition);
                }
                else if(btnLeftBound * 2 <= motionEvent.getX() &&
                        btnLeftBound * 3 > motionEvent.getX() && selectedItem != 2)
                {
                    updateSelectedTabPosition(2, selectedItem, savedPosition);
                }
                else if(btnLeftBound * 3 <= motionEvent.getX() &&
                        btnLeftBound * 4 > motionEvent.getX() && selectedItem != 3)
                {
                    updateSelectedTabPosition(3, selectedItem, savedPosition);
                }
                else if(btnLeftBound * 4 <= motionEvent.getX() &&
                        btnLeftBound * 5 > motionEvent.getX() && selectedItem != 4)
                {
                    updateSelectedTabPosition(4, selectedItem, savedPosition);
                }

                performClick();
            }
        }

        return true;
    }

    private void updateSelectedTabPosition(int selectedItem, int previouslySelectedItem,
                                           int savedPosition)
    {
        this.selectedItem = selectedItem;
        underlineAnimation(selectedItem, previouslySelectedItem);
        tabSelectionListenerAdapter.setSelectedPosition(selectedItem, savedPosition);
        tabSelectionListenerForecastAdapter.setSelectedPosition(selectedItem);
    }

    /**
     * If a view that overrides onTouchEvent or uses an OnTouchListener does not also
     * implement performClick and calls it when clicks are detected, the view may
     * not handle accessibility actions properly. Logic handling the click actions
     * should ideally be placed in View#performClick as some accessibility services
     * invoke performClick when a click action should occur.
     *
     * @return a boolean detonating whether a click was performed.
     */
    @Override
    public boolean performClick()
    {
        super.performClick();
        return true;
    }

    public void setSavePosition(int savedPosition)
    {
        this.savedPosition = savedPosition;
    }

    /**
     * Set which tab is selected. This needs to be here because the recyclerView recycles
     * views (who would have guessed) which means if the user selects tab 2. Once that view
     * is recycled it will show tab 2 selected for a new view even though the user did not
     * select that tab in the new view.
     *
     * @param selectedItem an int with the position of the selected tab.
     */
    public void setSelectedTab(int selectedItem)
    {
        this.selectedItem = selectedItem;
        reCalculateSelectedTab();
        invalidate();
    }

    /**
     * Re-Calculate selected tab.
     */
    private void reCalculateSelectedTab()
    {
        for(int i = 0; i < forecastLength; i++)
        {
            weatherIconForBtnPaint[i].setColor(btnStandardColor);
            weatherIconForBtnPaint[i].setAlpha(standardAlphaValue);

            tempForBtnPaint[i].setColor(btnStandardColor);
            tempForBtnPaint[i].setAlpha(standardAlphaValue);

            dayForBtnPaint[i].setColor(btnStandardColor);
            dayForBtnPaint[i].setAlpha(standardAlphaValue);

            if(selectedItem == i)
            {
                weatherIconForBtnPaint[i].setAlpha(selectedAlphaValue);
                tempForBtnPaint[i].setAlpha(selectedAlphaValue);
                dayForBtnPaint[i].setAlpha(selectedAlphaValue);
            }
        }

        bgTopBound = height - Util.getPixelFromDP(2);
        bgBottomBound = height;
        bgLeftBound = selectedItem * colWidth;
        bgRightBound = (selectedItem + 1) * colWidth;
    }

    /**
     * Tab buttons content to display.
     * @param tabContent an array of string with the text content to display.
     */
    public void setTabContent(String[] tabContent)
    {
        this.tabContent = tabContent;
        mapWeatherIdToIcons();
    }

    /**
     * Replace the weather id inside of the tabContent array with the actual weather icons.
     */
    private void mapWeatherIdToIcons()
    {
        for(int i = 0; i < tabContent.length; i += 3)
        {
            tabContent[i] = (getContext().getString(getResources()
                    .getIdentifier("wi_owm_" + tabContent[i], "string",
                            getContext().getPackageName())));
        }
    }

    public void underLineColor(boolean isDayTime)
    {
        if(isDayTime)
        { bgBtnTabPaint.setColor(Util.getColorFromResource(R.color.sunYellow)); }
        else
        { bgBtnTabPaint.setColor(Util.getColorFromResource(R.color.moonBlue)); }
    }

    public void setTabSelectionListener(ITabSelection.DetailsAdapter tabSelectionListener)
    {
        this.tabSelectionListenerAdapter = tabSelectionListener;
    }

    public void setTabSelectionListener(ITabSelection.DetailsForecastAdapter
                                                tabSelectionListener)
    {
        this.tabSelectionListenerForecastAdapter = tabSelectionListener;
    }
}