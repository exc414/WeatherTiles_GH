package io.bluephoenix.weathertiles.core.data.repository;

import android.content.SharedPreferences;

import io.bluephoenix.weathertiles.core.common.SortDef;
import io.bluephoenix.weathertiles.core.common.SortDef.SortType;
import io.bluephoenix.weathertiles.core.common.TempScaleDef;
import io.bluephoenix.weathertiles.core.common.TempScaleDef.TempScale;
import io.bluephoenix.weathertiles.util.Constant;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class PreferencesRepository implements IRepository.Preferences
{
    private final String tempScaleKey = "tempScale";
    private final String sortTypeKey = "sortType";
    private final String alarmKey = "alarmKey";
    private final String hasRunOnceKey = "hasRunOnce";
    private final String dataRefreshNeededKey = "dataRefreshNeeded";
    private final String maxNumberOfTilesKey = "maxNumberOfTiles";
    private final String enableBlinkAnimationKey = "enableBlinkAnimation";
    private final String enableAutoScrollingKey = "enableAutoScrollingKey";
    private SharedPreferences sharedPreferences;

    public PreferencesRepository(SharedPreferences sharedPreferences)
    {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void setDefaultTempScale(@TempScale int tempScale)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(tempScaleKey, tempScale);
        editor.apply();
    }

    @Override
    public int getDefaultTempScale()
    {
        return sharedPreferences.getInt(tempScaleKey, TempScaleDef.CELSIUS);
    }

    @Override
    public void setDefaultSort(@SortType int sortType)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(sortTypeKey, sortType);
        editor.apply();
    }

    @Override
    public int getDefaultSort()
    {
        return sharedPreferences.getInt(sortTypeKey, SortDef.NOSORT);
    }

    @Override
    public void setHasAlarmBeenSet(boolean alarmScheduleTime)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(alarmKey, alarmScheduleTime);
        editor.apply();
    }

    @Override
    public boolean getHasAlarmBeenSet()
    {
        return sharedPreferences.getBoolean(alarmKey, false);
    }

    @Override
    public void setHasRunOnce()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(hasRunOnceKey, true);
        editor.apply();
    }

    @Override
    public boolean getHasRunOnce()
    {
        return sharedPreferences.getBoolean(hasRunOnceKey, false);
    }

    @Override
    public void setDataRefreshNeeded(boolean refreshNeeded)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(dataRefreshNeededKey, refreshNeeded);
        editor.apply();
    }

    @Override
    public boolean getDataRefreshNeeded()
    {
        return sharedPreferences.getBoolean(dataRefreshNeededKey, false);
    }

    @Override
    public void setMaxNumberOfTiles(int maxNumberOfTiles)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(maxNumberOfTilesKey, maxNumberOfTiles);
        editor.apply();
    }

    @Override
    public int getMaxNumberOfTiles()
    {
        return sharedPreferences.getInt(maxNumberOfTilesKey, Constant.DEFAULT_MAX_TILES);
    }

    @Override
    public void setEnableBlinkAnimation(int enableBlinkAnimation)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(enableBlinkAnimationKey, enableBlinkAnimation);
        editor.apply();
    }

    @Override
    public int getEnableBlinkAnimation()
    {
        return sharedPreferences.getInt(enableBlinkAnimationKey,
                Constant.ENABLE_BLINK_ANIMATION);
    }

    @Override
    public void setEnableAutoScrolling(int enableAutoScrolling)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(enableAutoScrollingKey, enableAutoScrolling);
        editor.apply();
    }

    @Override
    public int getEnableAutoScrolling()
    {
        return sharedPreferences.getInt(enableAutoScrollingKey,
                Constant.ENABLE_AUTO_SCROLL);
    }
}
