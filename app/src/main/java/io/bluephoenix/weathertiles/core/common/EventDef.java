package io.bluephoenix.weathertiles.core.common;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class EventDef
{
    @IntDef({ CREATED, CREATE_ERROR, UPDATED, UPDATE_ERROR, GENERAL_ERROR })
    @Retention(RetentionPolicy.SOURCE)
    public @interface EventType { }
    public static final int CREATED = 0;
    public static final int CREATE_ERROR = 1;
    public static final int UPDATED = 2;
    public static final int UPDATE_ERROR = 3;
    public static final int GENERAL_ERROR = 4;
}
