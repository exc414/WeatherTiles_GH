package io.bluephoenix.weathertiles.core.presenters;

import io.bluephoenix.weathertiles.core.data.model.db.WeatherDetail;

/**
 * @author Carlos A. Perez
 */
public interface IWeatherDetailsContract
{
    interface IPublishToView
    {
        void initDetailFirst(WeatherDetail weatherDetail);
        void initDetailsToLeftOfFirst(WeatherDetail weatherDetail, int offset);
        void initDetailsToRightOfFirst(WeatherDetail weatherDetail);
        void addDetailLeft(WeatherDetail weatherDetail);
        void addDetailRight(WeatherDetail weatherDetail);
        void setMaxNumberOfTiles(int maxNumberOfTiles);
    }

    interface IPresenter
    {
        void initDetails(int startPos, int totalTiles);
        void addDetailLeft(int detailPos);
        void addDetailRight(int detailPos);
    }
}