package io.bluephoenix.weathertiles.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.app.App;
import io.bluephoenix.weathertiles.ui.views.FirstLaunchOverlayView;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class Util
{
    public static int totalRVHeight = 0;
    private static DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();

    /**
     * Gets the device's screen width in pixels.
     * @return a float with the screen width.
     */
    public static float getScreenWidthInPixels()
    {
       return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * Gets the device's screen height in pixels.
     * @return a float with the screen height.
     */
    public static float getScreenHeightInPixels()
    {
        return metrics.heightPixels;
    }

    /**
     * Calculates a dp value into pixels for the current device.
     * @param dp A virtual pixel unit that you should use when defining UI layout.
     * @return a float with the number of pixels.
     */
    public static int getPixelFromDP(int dp)
    {
        return (int) (dp * metrics.density);
    }

    /**
     * Calculate tile width based on margin and screen density.
     * @param columns An int with the number of columns the grid contains.
     * @return a float representing the width of one tile.
     */
    public static float getTileWidthPixel(float columns)
    {
        //This can change based on screen orientation
        //therefore it must be recalculate. Unlike the density above.
        float width = Resources.getSystem().getDisplayMetrics().widthPixels;

        //Magic number 8 - represents the addition of left + right margins given to the
        //entire screen in the top parent layout. Subtract it from the total width
        //of the screen so as to not skew the tile width calculation.
        float margin = 8f * metrics.density; //convert to pixels

        //return (width - margin) / columns;
        return (width / columns) - margin;
    }

    /**
     * @return a float with 8dp of padding in pixels.
     */
    public static float getTileInnerPadding() { return 8f * metrics.density; }

    /**
     * Helper - Get drawable from resource.
     * @param resource A particular resource ID.
     * @return a drawable object associated with a particular resource ID.
     */
    public static Drawable getDrawableFromResource(int resource)
    {
        return ContextCompat.getDrawable(App.getInstance(), resource);
    }

    /**
     * Helper - Get color-int from resource.
     * @param resource A particular resource ID.
     * @return a color associated with a particular resource ID.
     */
    public static int getColorFromResource(int resource)
    {
        return ContextCompat.getColor(App.getInstance(), resource);
    }

    /**
     * Helper - Get ColorStateList from resource.
     * @param resource A particular resource ID.
     * @return a color state list associated with a particular resource ID.
     */
    public static ColorStateList getColorStateListFromResource(int resource)
    {
        return ContextCompat.getColorStateList(App.getInstance(), resource);
    }

    /**
     * Helper - Get Boolean from resource.
     * @param resource A particular resource ID.
     * @return a boolean value.
     */
    public static boolean getBooleanFromResource(int resource)
    {
        return App.getInstance().getResources().getBoolean(resource);
    }

    /**
     * Calculate the width of the Material Dialog as they try to take the whole's phone
     * width otherwise. The 0.78 represents 78% of the total width.
     * @return an int with the width of the dialog.
     */
    public static int dialogWidth()
    {
        return (int) (metrics.widthPixels * 0.78);
    }

    /**
     * Based on the timezone id passed calculate the current time and convert it
     * into decimals.
     * @param calendar   A calendar instance.
     * @param timeZoneId A string with a timezone for the calendar instance.
     * @return a double with the current time.
     */
    public static double getTimeNowInDecimals(Calendar calendar, @NonNull String timeZoneId)
    {
        calendar.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        return calendar.get(Calendar.HOUR_OF_DAY)
                + ((double) calendar.get(Calendar.MINUTE) / 60);
    }

    /**
     * Based on the timezone id passed calculate the current time in seconds.
     * @param calendar    A calendar instance.
     * @param timeZoneId  A string with a timezone for the calendar instance.
     * @return a long with the current time in seconds.
     */
    public static long getTimeNowInSeconds(Calendar calendar, @NonNull String timeZoneId)
    {
        calendar.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        return calendar.getTimeInMillis() / 1000;
    }

    /**
     * This method sets a time therefore it should encapsulate its own calendar instance.
     * @return The time at the start of the day in milliseconds.
     */
    public static long getStartOfDayInMillis()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    /**
     * Wrapper method.
     * @return The time at the start of the day seconds.
     */
    public static long getStartOfDayInSeconds() { return getStartOfDayInMillis() / 1000; }

    public static void toastMaker(String input)
    {
        Toast.makeText(App.getInstance(), input, Toast.LENGTH_SHORT).show();
    }

    /**
     * Opens a the google play store application if available with the supplied package name.
     * If not available it will open the google play website.
     *
     * @param activity    An activity is a single, focused thing that the user can do.
     * @param packageName A string with the full package name of an application.
     */
    public static void openAppInMarket(Activity activity, String packageName)
    {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        //To count with Play market back stack, After pressing back button,
        //to taken back to our application, we need to add following flags to intent.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        else
        {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }

        try { activity.startActivity(goToMarket); }
        catch(ActivityNotFoundException e)
        {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + packageName)));
        }
    }

    /**
     * Opens the passed url in the browser.
     * @param activity  An activity is a single, focused thing that the user can do.
     * @param urlStr    A string with the full url address.
     */
    public static void openWebsiteInBrowser(Activity activity, String urlStr)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlStr));
        activity.startActivity(browserIntent);
    }

    /**
     *
     * @param activity
     * @param appName
     * @return
     */
    public static Intent shareApp(Activity activity, String appName)
    {
        String shareBody = "https://play.google.com/store/apps/details?id=" +
                activity.getApplicationContext().getPackageName();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, appName +
                " - Download it in the Google Play Store");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        return  sharingIntent;
    }

    /**
     *
     * @param appName
     * @return
     */
    public static Intent leaveFeedback(String appName)
    {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"androidbluephoenix@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, appName + " Feedback");
        return intent;
    }

    /**
     * Add the tutorial view to the root layout and display it for the user.
     */
    public static void showFirstLaunchOverlayView(Activity activity, Toolbar toolbar)
    {
        final FrameLayout rootLayout = activity.findViewById(android.R.id.content);
        final FirstLaunchOverlayView firstLaunchOverlayView =
                new FirstLaunchOverlayView(activity);

        //post() will run once the layout has settle. Once this happens we can get the
        //menu item as a view without receiving a NullPointerException. The width/height
        //and location will return 0. Using another post we can get the correct dimensions
        //for the menu buttons.
        toolbar.post(() ->
        {
            View menuAddTileButton = activity.findViewById(R.id.action_search);
            View menuDegreeButton = activity.findViewById(R.id.action_change_degrees);
            View[] views = new View[3]; //how many views to showcase.
            String[] strings =
                    {
                            activity.getString(R.string.intro_add_tile_button),
                            activity.getString(R.string.intro_change_temp_scale_button),
                            activity.getString(R.string.intro_hamburger_button)
                    };

            //Pass the left most menu button (not counting the nav button).
            //The total number of animated items will come from the strings array.
            menuAddTileButton.post(() ->
            {
                views[0] = menuAddTileButton;
                views[1] = menuDegreeButton;
                for(int i = 0; i < toolbar.getChildCount(); i++)
                {
                    if(toolbar.getChildAt(i) instanceof ImageButton)
                    { views[2] = toolbar.getChildAt(i); }
                }
                rootLayout.addView(firstLaunchOverlayView);
                firstLaunchOverlayView.setLayout(rootLayout);
                firstLaunchOverlayView.setTargetViews(views, strings);
                firstLaunchOverlayView.startAnimations();
            });
        });
    }
}
