package io.bluephoenix.weathertiles.util;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class Constant
{
    //For use with Log
    public static final String TAG = "Weather_Tiles";

    //Key use to get result from the SearchActivity
    public static final String SA_INTENT_KEY_SEARCH_RESULT = "chosen_city";

    //If nothing was retrieve from the SearchActivity send back this.
    public static final int RESULT_NULL = -999;

    //Intent to give the alarm in case we need to delete it or set it again.
    public static final int PENDING_INTENT_ID_1DAY = 11423;

    //Unicode for Fahrenheit, Celsius and the degree symbol.
    public static final String degreeSymbol = "\u00B0";
    public static final String fSymbol = "\u2109";
    public static final String cSymbol = "\u2103";

    //Base URL to make API calls with retrofit.
    //Why no HTTPS? This is weather data and its not sensitive. On API 17 (emulator) a
    //      javax.net.ssl.SSLHandshakeException:
    //      java.security.cert.CertPathValidatorException:
    //      Trust anchor for certification path not found.
    //was thrown. This might be an emulator issue but I rather not risk HTTPS when there is
    //no sensitive data transferred.
    public static final String BASE_OWM_API_URL = "http://api.openweathermap.org/data/2.5/";

    //Scroll speed for the weatherRecyclerView
    public static final int VERY_SLOW_SCROLL_SPEED = 15;
    public static final int NORMAL_SCROLL_SPEED = 2;

    //Package name of applications
    public static final String QUOTE_MACHINE = "xyz.bluephoenix.quotemachine";
    public static final String APP_UNINSTALLER = "xyz.bluephoenix.appuninstaller";
    public static final String STRESS_CPU = "xyz.bluephoenix.stresscpu";

    public static final int DEFAULT_MAX_TILES = 40;

    //Flags for new tile behaviour in the weatherRecyclerView
    public static final int ENABLE_BLINK_ANIMATION = 1;
    public static final int ENABLE_AUTO_SCROLL = 2;

    //Whether the inflated view in the onCreateViewHolder
    //should immediately attach to their parent.
    public static final boolean SHOULD_ATTACH_NOW = false;

    public static final int DONT_REMOVE = -1;
    public static final boolean REGISTER_BUS = true;
    public static final boolean DONT_REGISTER_BUS = false;
    public static final boolean DEREGISTER_BUS = true;
    public static final boolean DONT_DEREGISTER_BUS = false;
}
