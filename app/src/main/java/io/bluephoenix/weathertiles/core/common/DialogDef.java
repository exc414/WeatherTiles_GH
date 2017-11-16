package io.bluephoenix.weathertiles.core.common;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Carlos A. Perez
 */
public class DialogDef
{
    @IntDef({ NO_INTERNET, DOWNLOADING, DOWNLOADING_FINISHED })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DialogType { }
    public static final int NO_INTERNET = 0;
    public static final int DOWNLOADING = 1;
    public static final int DOWNLOADING_FINISHED = 2;
}
