package io.bluephoenix.weathertiles.ui.views;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.util.AnimListener;
import io.bluephoenix.weathertiles.util.Util;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class SnackView extends View
{
    private final Handler dismissSnackBar = new Handler();

    //View width and height
    private float viewWidth = 0;
    private float viewHeight = 0;

    //Custom Fonts
    private final String PATH_TO_OPENSANS_REGULAR_FONT = "fonts/OpenSans-Regular.ttf";

    //Typefaces
    private Typeface opensansRegular;

    //Font Spacing
    private float messageFontSpacing;

    //Content
    private String messageContent = "";
    private String messageUpdate = "";

    //Size
    private int messageTextSize = Util.getPixelFromDP(14); //dp

    //Indent Left (Margin)
    private int indentLeft = Util.getPixelFromDP(16); //dp

    //Colors
    private int loadingIconColor = Util.getColorFromResource(R.color.colorAccentMain);
    private int messageColor = Util.getColorFromResource(R.color.colorAccentMain);

    //Paint
    private Paint loadingIconPaint;
    private Paint messageTextPaint;
    private Paint messageUpdateTextPaint;

    //Bounds
    private Rect messageBounds = new Rect();
    private Rect messageUpdateBounds = new Rect();

    //Loading icon values
    private final float SCALE = 0.70f; //Thickness of the lines
    private final int numberOfItems = 3;

    //Delays when the lines shrink and expand
    private final long[] timingDelays = new long[] { 120, 240, 360 };
    private final float[] scaleFloats = new float[] { SCALE, SCALE, SCALE };

    //Circle spacing, radius and position
    private final float circleSpacing = 16;
    private float radius;
    private float yCoordinate;

    List<ValueAnimator> valueAnimators = new ArrayList<>();

    /**
     * Simple constructor to use when creating a view from code.
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public SnackView(Context context)
    {
        super(context);
        init(context);
    }

    /**
     * Init XML values.
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    private void init(Context context)
    {
        opensansRegular = Typeface.createFromAsset(context.getAssets(),
                PATH_TO_OPENSANS_REGULAR_FONT);
        setPaintProperties();
    }

    /**
     * Init all paints and font spacing objects.
     */
    private void setPaintProperties()
    {
        loadingIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        loadingIconPaint.setColor(loadingIconColor);

        messageTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        messageTextPaint.setTextSize(messageTextSize);
        messageTextPaint.setColor(messageColor);
        messageTextPaint.setTextAlign(Paint.Align.CENTER);
        messageTextPaint.setTypeface(opensansRegular);

        messageUpdateTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        messageUpdateTextPaint.setTextSize(messageTextSize);
        messageUpdateTextPaint.setColor(messageColor);
        messageUpdateTextPaint.setTextAlign(Paint.Align.CENTER);
        messageUpdateTextPaint.setTypeface(opensansRegular);
        messageUpdateTextPaint.setAlpha(0);

        messageFontSpacing = messageTextPaint.getFontSpacing() / 4;
    }

    private void drawLoadingIconSetup()
    {
        //The bigger this number the smaller the final radius will be.
        //Note if this number is to big you wont be able to see the circles.
        int endingSize = 24;
        //Calc circle radius
        radius = (Math.min(viewWidth, viewHeight) - circleSpacing * 2) / endingSize;

        //Puts the circles at the desire place. Height wise. We cannot do this for
        //this width until be get the bounds of the message string.
        yCoordinate = (viewHeight / 2) + messageFontSpacing;
    }

    /**
     * Called to determine the size requirements for this view and all of its children.
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
        return resolveSizeAndState((int) viewWidth, measureSpec, 0);
    }

    private int measureHeight(int measureSpec)
    {
        return resolveSizeAndState((int) viewHeight, measureSpec, 0);
    }

    /**
     * Called when the view should render its content.
     * @param canvas The Canvas class holds the "draw" calls.
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        drawMessage(canvas);
        drawLoadingIcon(canvas);
        drawMessageDone(canvas);
    }

    /**
     * Draw the starting message on the left side of the view.
     * @param canvas The Canvas class holds the "draw" calls.
     */
    private void drawMessage(Canvas canvas)
    {
        messageTextPaint.getTextBounds(messageContent, 0,
                messageContent.length(), messageBounds);
        canvas.drawText(messageContent, messageBounds.width() / 2 + indentLeft,
                yCoordinate, messageTextPaint);
    }

    /**
     * Draw the ending message just like the drawMessage() on the left side.
     * @param canvas The Canvas class holds the "draw" calls.
     */
    private void drawMessageDone(Canvas canvas)
    {
        messageUpdateTextPaint.getTextBounds(messageUpdate, 0,
                messageUpdate.length(), messageUpdateBounds);
        canvas.drawText(messageUpdate, messageUpdateBounds.width() / 2 + indentLeft,
                yCoordinate, messageUpdateTextPaint);
    }

    /**
     * Draw and place the three rounded rectangles that make the loading icon.
     * @param canvas The Canvas class holds the "draw" calls.
     */
    private void drawLoadingIcon(Canvas canvas)
    {
        for(int i = 0; i < numberOfItems; i++)
        {
            canvas.save();
            float translateX =
                    indentLeft + messageBounds.width() + circleSpacing + (radius * 2) * i
                            + circleSpacing * i;

            canvas.translate(translateX, yCoordinate - (radius / 2));
            canvas.scale(scaleFloats[i], scaleFloats[i]);
            canvas.drawCircle(0, 0, radius, loadingIconPaint);
            canvas.restore();
        }
    }

    /**
     * Crossfade the two text drawn text. drawMessage fade out and drawMessageDone fade it.
     * @param snackbar A snackbar reference so that at the end of the animation it can be
     *                 dismiss.
     */
    public void startFadeAnimation(final Snackbar snackbar)
    {
        ValueAnimator alphaAnim = ValueAnimator.ofFloat(0, 255);
        alphaAnim.setDuration(400);
        alphaAnim.setStartDelay(100);

        alphaAnim.addUpdateListener(animation ->
        {
            //Fade out the message and loading icon and fade in the new update message.
            int value = Math.round((float) animation.getAnimatedValue());
            messageTextPaint.setAlpha(255 - value);
            loadingIconPaint.setAlpha(255 - value);
            messageUpdateTextPaint.setAlpha(value);
            postInvalidate();
        });

        alphaAnim.addListener(new AnimListener()
        {
            @Override
            protected void animationStarted(Animator animation) { }

            @Override
            protected void animationEnded(Animator animation)
            {
                stopLoadingAnimation();
                //Dismiss with delay so that the user can read the ending message
                dismissSnackBar.postDelayed(() -> snackbar.dismiss(), 800);
            }
        });

        alphaAnim.start();
        invalidate();
    }

    /**
     * Start the three rounded rectangle loading animation.
     */
    public void startLoadingAnimation()
    {
        //Make numberOfItems (in this case 3) different animations for each item.
        //Timing delay creates the effect of continuous animation.
        for(int i = 0; i < numberOfItems; i++)
        {
            final int index = i;
            //The 1, 0.3, 1 are what makes the circles bigger, smaller and bigger.
            ValueAnimator scaleAnim = ValueAnimator.ofFloat(1, 0.3F, 1);
            scaleAnim.setDuration(500);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(timingDelays[i]);
            valueAnimators.add(scaleAnim);

            scaleAnim.addUpdateListener(animation ->
            {
                scaleFloats[index] = (float) animation.getAnimatedValue();
                postInvalidate();
            });

            scaleAnim.start();
            invalidate();
        }
    }

    /**
     * Stop all animations based on the number of items.
     */
    public void stopLoadingAnimation()
    {
        for(int i = 0; i < numberOfItems; i++) { valueAnimators.get(i).end(); }
    }

    /**
     * Used as the only message or the starting message.
     * @param messageContent A string detonating the message to display.
     */
    public void setMessage(String messageContent)
    {
        this.messageContent = messageContent;
    }

    /**
     * Used as an update message or ending message.
     * @param messageUpdate A string detonating the message to display.
     */
    public void setMessageDone(String messageUpdate)
    {
        this.messageUpdate = messageUpdate;
    }

    /**
     * Measure the view based on the snackbar own width and height. Once the dimensions
     * are set then set up the yCoordinate and radius for the loading icon.
     * @param width An int with the width of the snackbar
     * @param height An int with the height of the snackbar.
     */
    public void measureFromSnackBarDimensions(int width, int height)
    {
        viewWidth = width;
        viewHeight = height;
        drawLoadingIconSetup();
        invalidate();
    }
}