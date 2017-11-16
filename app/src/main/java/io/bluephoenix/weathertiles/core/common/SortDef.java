package io.bluephoenix.weathertiles.core.common;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class SortDef
{
    @IntDef({ NOSORT, TEMP_ASCENDING, TEMP_DESCENDING, DAYTIME, NIGHTTIME,
            ALPHABETICALLY_ASCENDING, ALPHABETICALLY_DESCENDING })
    @Retention(RetentionPolicy.SOURCE)
    public @interface SortType { }
    public static final int NOSORT = 0;
    public static final int TEMP_ASCENDING = 1;
    public static final int TEMP_DESCENDING = 2;
    public static final int DAYTIME = 3;
    public static final int NIGHTTIME = 4;
    public static final int ALPHABETICALLY_ASCENDING = 5;
    public static final int ALPHABETICALLY_DESCENDING = 6;
}
