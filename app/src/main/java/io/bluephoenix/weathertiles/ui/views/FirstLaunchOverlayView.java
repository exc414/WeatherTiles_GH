package io.bluephoenix.weathertiles.ui.views;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.util.AnimListener;
import io.bluephoenix.weathertiles.util.Util;

/**
 * @author Carlos A. Perez
 */
public class FirstLaunchOverlayView extends View
{
    //Root Layout where view will be added
    FrameLayout frameLayout;

    //Get dimensions of the screen
    private final float width = Util.getScreenWidthInPixels();
    private final float height = Util.getScreenHeightInPixels();

    private final float centerWidth = width / 2;
    private final float centerHeight = height / 2;

    //Color text and size
    private final boolean isSizeLikeTablet = Util.getBooleanFromResource(R.bool.isTablet);
    private final int colorText = Util.getColorFromResource(R.color.colorAccentMain);
    private final int colorButton = Util.getColorFromResource(R.color.dayNight);
    private final int messageTextSize = Util.getPixelFromDP((isSizeLikeTablet) ? 22 : 18); //DP
    private final int buttonTextSize = Util.getPixelFromDP((isSizeLikeTablet) ? 18 : 16); //DP

    //Radius of the showcase circle and the background expanding circle.
    private float circleRadius = 0;
    private float circleRadiusPercentage = 0;
    private float backgroundCircleRadius;

    //Circle position
    private float circleX = 0;
    private float circleY = 0;

    //Content values
    private String[] messages;
    private View[] views;
    private String[] messageContent;
    private String buttonContent = "NEXT";

    //Button Click Area Bounds
    private int btnPadding = Util.getPixelFromDP(40);
    private int btnTopBound = (int) (height - Util.getPixelFromDP(65));
    private int btnLeftBound = (int) (width - Util.getPixelFromDP(70));

    //Paint
    private Paint circleClearPaint;
    private Paint backgroundPaint;
    private Paint messagePaint;
    private Paint buttonTextPaint;

    //Bounds
    private Rect messageBound = new Rect();
    private Rect buttonBound = new Rect();

    //How many animations will be run. Coincides with the number of views passed.
    private int animCounter = 0;
    private int maxAnimations = 0;

    //Set the final radius for the background circle based on whether the phone is
    //in portrait or landscape mode.
    private final int finalValue = (int) ((width > height) ? width * 2 : height * 2);

    //Background circle starting properties
    private int bgCircleAnimationDelay = 600;
    private int bgCircleAnimationDuration = 800;

    //Animators
    private ValueAnimator circleScaleAnimation;

    //Message setup
    private final int messageMaxWidth = (int) (width * 0.80F); //80 percent of total width

    //private final float startDrawingMessageX = width * 0.10F;
    private float startDrawingMessageX;
    private float startDrawingMessageY = centerHeight;
    private float messageFontSpacing = 0;

    //Detonates the last time the button was clicked/tapped.
    private long lastClickTime = 0;

    /**
     * Simple constructor to use when creating a view from code.
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public FirstLaunchOverlayView(Context context)
    {
        super(context);
        init(context);
    }

    /**
     * Init all paints and font objects.
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    private void init(Context context)
    {
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_HARDWARE, null);

        String PATH_TO_MONTSERRAT_REGULAR_FONT = "fonts/OpenSans-Regular.ttf";
        Typeface openSans = Typeface.createFromAsset(context.getAssets(),
                PATH_TO_MONTSERRAT_REGULAR_FONT);

        circleClearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circleClearPaint.setColor(Color.TRANSPARENT);
        circleClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        circleClearPaint.setAlpha(0);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(0xD8000000);

        messagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        messagePaint.setColor(colorText);
        messagePaint.setTextSize(messageTextSize);
        messagePaint.setTextAlign(Paint.Align.LEFT);
        messagePaint.setTypeface(openSans);
        //This needs to come after setting the color and not before it.
        messagePaint.setAlpha(0);
        messageFontSpacing = messagePaint.getFontSpacing();

        buttonTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonTextPaint.setColor(colorButton);
        buttonTextPaint.setTextSize(buttonTextSize);
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);
        buttonTextPaint.setTypeface(openSans);
        buttonTextPaint.setAlpha(0);
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
        return resolveSizeAndState((int) width, measureSpec, 0);
    }

    private int measureHeight(int measureSpec)
    {
        return resolveSizeAndState((int) height, measureSpec, 0);
    }

    /**
     * Called when the view should render its content.
     * @param canvas to host the draw calls (writing into the bitmap)
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        drawBackground(canvas);
        drawCircle(canvas);
        drawTextButton(canvas);
        drawMessage(canvas);
    }

    /**
     * Draw text which can be only one line or multi line.
     * @param canvas The Canvas class holds the "draw" calls.
     */
    private void drawMessage(Canvas canvas)
    {
        for(int i = 0; i < messageContent.length; i++)
        {
            //As text chunks are drawn keep adding to the height so that they will be drawn
            //below each other in correct order.
            canvas.drawText(messageContent[i], startDrawingMessageX,
                    startDrawingMessageY + (i * messageFontSpacing), messagePaint);
        }
    }

    /**
     * Draw the clear circle that brings attention to the buttons.
     * @param canvas The Canvas class holds the "draw" calls.
     */
    private void drawCircle(Canvas canvas)
    {
        canvas.drawCircle(circleX, circleY, circleRadius, circleClearPaint);
    }

    /**
     * Draw the text button in the bottom right corner of the screen. Using the bound.width()
     * it will be separate from screen about 16-18dp.
     * @param canvas The Canvas class holds the "draw" calls.
     */
    private void drawTextButton(Canvas canvas)
    {
        buttonTextPaint.getTextBounds(buttonContent, 0, buttonContent.length(), buttonBound);
        canvas.drawText(buttonContent, (width - btnPadding), height - btnPadding,
                buttonTextPaint);
    }

    /**
     * Draw background circle. Will eventually expand to cover the whole screen.
     * @param canvas The Canvas class holds the "draw" calls.
     */
    private void drawBackground(Canvas canvas)
    {
        canvas.drawCircle(circleX, 0, backgroundCircleRadius, backgroundPaint);
    }

    /**
     * Circle animation to bring attention to the buttons we want to showcase.
     */
    public void circleAnimation()
    {
        circleScaleAnimation = ValueAnimator.ofFloat(
                circleRadius, circleRadius + circleRadiusPercentage, circleRadius);
        circleScaleAnimation.setDuration(650);
        circleScaleAnimation.setRepeatCount(-1);

        circleScaleAnimation.addUpdateListener(animation ->
        {
            circleRadius = Math.round((float) animation.getAnimatedValue());
            postInvalidate();
        }); circleScaleAnimation.start();
    }

    /**
     * Animate both the button text and normal text. Fade in when the animation starts
     * for a specific view, then fade out when a new view needs to be highlight. Params
     * are reverse when the animation needs to be reverse.
     *
     * @param startValue    An int detonating the starting alpha value of the drawing.
     * @param finalValue    An int detonating the final alpha value of the drawing.
     * @param fadingSpeed   An int detonating how fast the view should fade out.
     */
    public void textAnimation(int startValue, int finalValue, int fadingSpeed)
    {
        ValueAnimator fadeInTextAnimation = ValueAnimator.ofFloat(startValue, finalValue);
        fadeInTextAnimation.setDuration(400);
        fadeInTextAnimation.setStartDelay(bgCircleAnimationDelay +
                (bgCircleAnimationDuration / 2) - 100);

        fadeInTextAnimation.addUpdateListener(animation ->
        {
            int alpha = Math.round((float) animation.getAnimatedValue());
            int buttonAlpha = alpha - fadingSpeed;
            messagePaint.setAlpha(alpha);
            //Once the alpha value is below zero, just keep setting zero.
            //If it goes into the negative the alpha will go to 255 again.
            buttonTextPaint.setAlpha((buttonAlpha < 0) ? 0 : buttonAlpha);
            postInvalidate();
        }); fadeInTextAnimation.start();
    }

    /**
     * Animate the circle expanding background. Once done increase the animCounter so
     * that when the user's presses the button text (NEXT) it will get the values for the
     * view that needs to be highlighted next.
     *
     * @param startValue An int detonating the starting alpha value of the drawing.
     * @param finalValue An int detonating the final alpha value of the drawing.
     * @param isEnding   A boolean value whether this is a starting animation or
     *                   an ending animating meaning we are going to focus a different v
     *                   iew and the animCounter needs to be increased.
     */
    public void bgCircleAnimation(int startValue, int finalValue, boolean isEnding)
    {
        ValueAnimator bgCircleScaleAnimation = ValueAnimator.ofFloat(startValue, finalValue);
        bgCircleScaleAnimation.setDuration(bgCircleAnimationDuration);
        bgCircleScaleAnimation.setStartDelay(bgCircleAnimationDelay);
        bgCircleScaleAnimation.setRepeatCount(0);
        bgCircleScaleAnimation.addUpdateListener(animation ->
        {
            backgroundCircleRadius = Math.round((float) animation.getAnimatedValue());
            postInvalidate();
        }); bgCircleScaleAnimation.start();

        bgCircleScaleAnimation.addListener(new AnimListener()
        {
            @Override
            protected void animationStarted(Animator animation) { }

            @Override
            protected void animationEnded(Animator animation)
            {
                //Kick off the next animation only if this is the ending
                //animation of the starting animation.
                if(isEnding && animCounter < maxAnimations)
                {
                    //If equal this means that the is the second to last animation.
                    //Therefore we need to change the button text content from NEXT to FINISH
                    if(animCounter + 1 == maxAnimations) { buttonContent = "FINISH"; }

                    //End the circle animation as its the only one that runs forever.
                    circleScaleAnimation.end();
                    //Increase the counter and start animating the new view that
                    // needs highlighting.
                    animCounter++;
                    startAnimations();
                }
                else if(isEnding && animCounter == maxAnimations)
                {
                    //End the circle animation as its the only one that runs forever.
                    circleScaleAnimation.end();
                    //Once equal it means that all animations and button highlights are done.
                    //Destroy the view.
                    destroyView();
                }
            }
        });
    }

    /**
     * Views that need to be showcased with their respective strings.
     * @param views     An array of view objects.
     * @param messages  An array of string objects.
     */
    public void setTargetViews(View[] views, String[] messages)
    {
        this.views = views;
        this.messages = messages;
    }

    /**
     * Extract information from the views. Get the location of the views so that we know where
     * we have to draw the highlighting circle. Get the number of views so we know how many
     * animations we need to do. Set the circleRadius based on the views width and specified
     * float (can be anything just personal preference as how big the circle is)
     * number to create the circle.
     *
     * Set the circleRadiusPercentage which will determine how big the circle will get when
     * expanding.
     */
    public void startAnimations()
    {
        int[] location = new int[2];
        maxAnimations = views.length - 1;
        multiLineMessage(messages[animCounter]);
        views[animCounter].getLocationInWindow(location);
        int viewWidth = views[animCounter].getWidth();
        int viewHeight = views[animCounter].getWidth();

        circleRadius = views[animCounter].getWidth() / 1.6F;
        circleRadiusPercentage = circleRadius * 0.20F; //Higher = Bigger circle.
        circleX = location[0] + (viewWidth / 2);
        circleY = location[1] - (viewHeight * 0.04F);

        //Starting scale value - Ending scale value - Whether this is a starting anim (false)
        //or an ending anim (true)
        bgCircleAnimation(0, finalValue, false);
        //Starting value - Ending value - Speed of alpha animation (higher = faster).
        textAnimation(0, 255, 0);
        circleAnimation();
        invalidate(); //Force re-draw
    }

    /**
     * If the text is bigger than the set max width, then break it into pieces.
     * @param message A string with the message to draw onto the screen.
     */
    private void multiLineMessage(String message)
    {
        //Get the total width and number chars of the string message.
        messagePaint.getTextBounds(message, 0, message.length(), messageBound);
        startDrawingMessageX = centerWidth - (messageBound.width() / 2);
        int textWidth = messageBound.width();

        //Only break the message into chunks if it does not fit into our max width.
        //Which is 80 percent of the total width.
        if(textWidth > messageMaxWidth)
        {
            //Amount of chars the string has.
            int messageLength = message.length();

            //Get the number of chunks the message string will need to be split into.
            //+1 is added because to the value because rounding up is necessary.
            //Therefore, if value is 3.3 ~ 4 or 3.8 ~ 4.
            //We add another +1 because we are going to be using the String.substring
            //and therefore messageChunks needs to have an ending value to it as the
            //substring method will take staring position and ending position.
            int numberOfMessageChunks = (textWidth / messageMaxWidth) + 2;
            int[] messageChunks = new int[numberOfMessageChunks];

            //Calculate how many pixels per char (approx). Always round up
            //therefore, if the value is 28.83 ~ 29, 28.03 ~ 29.
            int pixelsPerChar = (textWidth / messageLength) + 1;
            //Get how many chars we can have fit into our allowed width.
            int charsToSubStr = (messageMaxWidth / pixelsPerChar);

            //Set while loop values.
            int counter = 0;
            boolean bool = false;

            //Starting value will always be zero.
            messageChunks[0] = 0;
            //Ending value will be the total number of characters in the messages string.
            messageChunks[numberOfMessageChunks - 1] = message.length();

            //We sub 1 to correct for array starting at zero here.
            messageContent = new String[numberOfMessageChunks - 1];

            //The text should be at the center (plus its own font spacing) so we start with the
            //centerHeight. The more chunks the text needs to be split into the higher the
            //subtrahend will be. Divide the subtrahend by 2 to keep the whole block centered.
            //If not it will further up the screen than desired.
            startDrawingMessageY = centerHeight - (
                    (messageContent.length / 2) * messageFontSpacing);

            //Start at i at one since we know the starting value is always zero.
            //And there is no need to traverse the last item so remove one from the
            //numberOfMessageChunks.
            for(int i = 1; i < numberOfMessageChunks - 1; i++)
            {
                while(!bool)
                {
                    //If the value set by charsToSubStr is a space we can break
                    //there, however if its a letter than we cannot. We cannot
                    //go forward adding characters as this would mean our string
                    //will be larger than our max width instead we must remove a
                    //char from charsToSubStr until we find a space.
                    //Example - charsToSubStr = 39, but space is found at 34. Keep
                    //removing until 34. Set as value for the second messageChunks array,
                    //reset counter and bool. Next multiply 39 (charsToSubStr starting
                    //value) but this time i is 2 so we correctly moved along the
                    //string or else we would always get the same string into our array.
                    //Then once again count backwards until a space is found and break again.
                    //Rinse and repeat.
                    if(message.charAt((charsToSubStr * i) - counter) == ' ')
                    {
                        messageChunks[i] = (charsToSubStr * i) - counter;
                        bool = true;
                    }
                    counter++;
                }

                //Reset
                counter = 0;
                bool = false;
            }

            //Once the position at which the string needs to be broken is known. Then
            //we can use those values to break it using the substring function. We
            //trim any spaces or else it will throw the alignment off.
            for(int i = 0; i < messageContent.length; i++)
            {
                messageContent[i] = message.substring(
                        messageChunks[i], messageChunks[i + 1]).trim();
            }

            messagePaint.getTextBounds(messageContent[0], 0, messageContent[0].length(), messageBound);
            //Make sure the text is centered but aligned to the left when multiple lines are used.
            startDrawingMessageX = centerWidth - (messageBound.width() / 2);
        }
        else
        {
            //Array init is need it here instead of when the array is declared because if a multi
            //line messageContent is written and afterwards a single line is written. Then the
            //2nd 3rd ... n lines in the previous content will stay drawn and only the first
            // line will change. Therefore, we must always remake the array when a single line is
            //about to be drawn. Multi line already does this.
            messageContent = new String[1];
            messageContent[0] = message;
        }
    }

    /**
     * Depending on when the user clicks on the screen grab the ACTION_DOWN event
     * within the specified bounds. The bounds are create around the button text and
     * are larger than the text so that the user has no trouble click it. It is
     * import to use ACTION_DOWN here or else this will be called twice. Also returning
     * true makes it so no view below it can response to events.
     *
     * @param motionEvent An object which contains information about a user interaction.
     * @return A boolean detonating where this event was consumed or not.
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN
                && (height >= motionEvent.getY() && btnTopBound < motionEvent.getY())
                && (width >= motionEvent.getX() && btnLeftBound < motionEvent.getX())
                && (SystemClock.elapsedRealtime() - lastClickTime > 1500))
        {
            //Make sure that the user cannot keep pressing the button if the animation
            //is not finished.
            lastClickTime = SystemClock.elapsedRealtime();
            bgCircleAnimationDuration = 700;

            //Reduce the delay of the animation since the application is already started
            bgCircleAnimationDelay = 80;

            //Starting scale value - Ending scale value - Whether this is a starting anim
            //(false) or an ending anim (true)
            bgCircleAnimation(finalValue, 0, true);

            //Starting alpha value - Ending alpha value - Speed of alpha animation
            //(higher = fades out faster).
            textAnimation(255, 0, 100);
            performClick();
        }

        return true;
    }

    /**
     * If a view that overrides onTouchEvent or uses an OnTouchListener does not also implement
     * performClick and calls it when clicks are detected, the view may not handle accessibility
     * actions properly. Logic handling the click actions should ideally be placed in
     * View#performClick as some accessibility services invoke performClick when
     * a click action should occur.
     * @return a boolean detonating whether a click was perform.
     */
    @Override
    public boolean performClick()
    {
        super.performClick();
        return true;
    }

    /**
     * Layout reference need it when removing the view.
     * @param frameLayout The top most layout of an application.
     */
    public void setLayout(FrameLayout frameLayout)
    {
        this.frameLayout = frameLayout;
    }

    /**
     * Remove the view when its no longer need it.
     */
    private void destroyView()
    {
        frameLayout.removeView(this);
    }
}