package io.bluephoenix.weathertiles.ui.adapters;

/**
 * @author Carlos A. Perez
 */
public interface ITabSelection
{
    interface DetailsAdapter
    {
        void setSelectedPosition(int selectedPosition, int savedPosition);
    }

    interface DetailsForecastAdapter
    {
        void setSelectedPosition(int selectedPosition);
    }
}
