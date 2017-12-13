package io.bluephoenix.weathertiles.core.data.repository;

import java.util.List;

import io.bluephoenix.weathertiles.core.common.SortDef.SortType;
import io.bluephoenix.weathertiles.core.common.TempScaleDef.TempScale;
import io.bluephoenix.weathertiles.core.data.model.db.Tile;
import io.bluephoenix.weathertiles.core.data.model.db.TileDetail;
import io.bluephoenix.weathertiles.core.data.model.db.WeatherDetail;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public interface IRepository
{
    interface Preferences
    {
        void setDefaultTempScale(@TempScale int tempScale);
        @TempScale
        int getDefaultTempScale();

        void setDefaultSort(@SortType int sortType);
        @SortType int getDefaultSort();

        void setHasAlarmBeenSet(boolean alarmScheduleTime);
        boolean getHasAlarmBeenSet();

        void setHasRunOnce();
        boolean getHasRunOnce();

        void setDataRefreshNeeded(boolean refreshNeeded);
        boolean getDataRefreshNeeded();

        void setMaxNumberOfTiles(int maxNumberOfTiles);
        int getMaxNumberOfTiles();

        void setEnableBlinkAnimation(int enable);
        int getEnableBlinkAnimation();

        void setEnableAutoScrolling(int enableAutoScrolling);
        int getEnableAutoScrolling();
    }

    interface Weather
    {
        /**
         * Get all the tiles object that are currently in the database.
         * @return a list of tile objects.
         */
        List<Tile> getAllTiles();

        /**
         * Save a tile by passing a tile object to the db.
         * @param tile contains all weather information.
         */
        void createTile(final Tile tile);

        /**
         * @param tileDetailList a list of objects with detailed forecast information.
         */
        void createTileDetails(final List<TileDetail> tileDetailList);

        /**
         * Update the tiles from the tiles details table in three hours intervals.
         * @param calcTimestamp a long to match against the timestamp in the tile detail db.
         * @param onStartUp     a boolean detonating whether the method is being called at
         *                      the start of the application or from a repeat request.
         */
        void updateTiles(long calcTimestamp, boolean onStartUp);

        /**
         * Updates the tiles sunrise/sunset icon.
         */
        void updateTilesSunriseSunset();

        /**
         * Delete an existing tile or tiles.
         * @param tile an object with weather information.
         */
        void deleteTile(final Tile tile);

        /**
         * Delete a single detail tile.
         * @param tile an object with weather information.
         */
        void deleteTileDetail(final Tile tile);

        /**
         * Gets the data to populate the Weather Details View.
         * @param position an int which detonates which row the data is fetched from.
         * @return one WeatherDetail object.
         */
        WeatherDetail getWeatherDetail(int position, @TempScale int tempScale);

        void saveSelectedTabPosition(int position, long cityId);

        /**
         * Change the saved position of the tile object.
         * @param citiesId long array of cities id.
         */
        void saveTilePosition(long[] citiesId);
    }

    interface Search
    {
        /**
         * Returns cities suggestions based on input.
         * @param userInput a string
         */
        void getSuggestions(final String userInput);

        /**
         * Check to see if the city is already added to the list.
         * @param cityId a city id to search against the tile table.
         * @return a boolean detonating if the city is duplicated or not.
         */
        Boolean isCityIdDuplicated(final long cityId);
    }
}
