package io.bluephoenix.weathertiles.core.presenters;

import java.util.List;

import io.bluephoenix.weathertiles.core.common.DialogDef.DialogType;
import io.bluephoenix.weathertiles.core.common.SortDef.SortType;
import io.bluephoenix.weathertiles.core.common.TempScaleDef.TempScale;
import io.bluephoenix.weathertiles.core.data.model.db.Tile;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public interface IWeatherContract
{
    interface IPublishToView
    {
        /**
         * Let the user know of a change to the data of the tiles,
         * through a toast.
         * @param message a string containing the message for the user.
         */
        void tileEvent(String message);

        /**
         * Pass the tiles from the database to the adapter.
         * @param tileList a list of tiles objects.
         */
        void initTiles(List<Tile> tileList);

        /**
         * Adds a single tile to the recycler view.
         * @param tile an object with weather information.
         */
        void addTile(Tile tile);

        /**
         * Updates existing tiles in the recycler view.
         * @param tiles a list of tile objects with weather information.
         */
        void updateTiles(List<Tile> tiles);

        /**
         * Warn the user about something that happen.
         */
        void notifyUserAlert(@DialogType int dialogType);
    }

    interface IPresenter
    {
        /**
         * Load tile(s) from the database on startup
         */
        void initTiles();

        /**
         * Adds a tile from Open Weather API.
         * @param cityId a long containing an API city id.
         */
        void createTile(long cityId);

        /**
         * Delete a tile.
         * @param tile an object with weather information.
         */
        void deleteTile(Tile tile);

        /**
         * Set the position of tiles in the database which corresponds to the
         * Weather Adapter position.
         * @param citiesId a long array of cities id.
         */
        void saveTilePosition(long[] citiesId);

        /**
         * On configuration changes save the currently loaded tiles.
         * @param tileList a list of tiles objects.
         */
        void saveTiles(List<Tile> tileList);

        /**
         * Save the sort type the user has chosen in preferences.
         * @param sortType an int detonating the type of sort
         */
        void setDefaultSort(@SortType int sortType);

        /**
         * Get the sort type the user has chosen in preferences.
         * @return an int detonating the type of sort
         */
        @SortType int getDefaultSort();

        /**
         * Set the temp scale based on the int value.
         * @param tempScale the type of scale that will be saved.
         */
        void setDefaultTempScale(@TempScale int tempScale);

        /**
         * Get the temp scale based on the previously set value.
         * @return whether the temp scale should be set to celsius (0/default) or
         *         fahrenheit (1).
         */
        @TempScale
        int getDefaultTempScale();

        /**
         * Set to true when the application runs for the first time.
         */
        void setHasRunOnce();

        /**
         * @return a boolean whether the applications has run at least once.
         */
        boolean getHasRunOnce();

        /**
         * Number of tiles the user is allowed to have in the grid before he/she has to
         * delete existing tile(s)
         * @return An int with the max number of tiles.
         */
        int getMaxNumberOfTiles();

        /**
         * Gets whether the recyclerView will automatically scroll when a new tile is added.
         * Whether the tile will blink or not.
         * @return A boolean detonating if automatic scroll is on.
         */
        int getNewTileBehaviourFlags();

    }
}
