package io.bluephoenix.weathertiles.ui.dialogs;

import io.bluephoenix.weathertiles.core.common.SortDef.SortType;

/**
 * @author Carlos A. Perez Zubizarreta
 */

public interface DialogsPublish
{
    void setUserSortSelection(@SortType int sortType);

    interface Preferences
    {
        void setPreferences(int value);
    }
}
