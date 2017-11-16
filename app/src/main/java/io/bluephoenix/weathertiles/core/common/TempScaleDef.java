package io.bluephoenix.weathertiles.core.common;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Carlos A. Perez Zubizarreta
 */

public class TempScaleDef
{
    @IntDef({ CELSIUS, FAHRENHEIT })
    @Retention(RetentionPolicy.SOURCE)
    public @interface TempScaleType { }
    public static final int CELSIUS = 0;
    public static final int FAHRENHEIT = 1;
}
