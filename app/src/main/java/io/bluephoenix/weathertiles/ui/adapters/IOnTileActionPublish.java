package io.bluephoenix.weathertiles.ui.adapters;

import io.bluephoenix.weathertiles.core.data.model.db.Tile;

/**
 * @author Carlos A. Perez Zubizarreta
 */

public interface IOnTileActionPublish
{
    /**
     * If a tile is moved out of position reset the sort.
     * This takes care not to remove the sort on swipe,
     * deletion or adding of items.
     */
    void onTileMovedResetSort();

    /**
     * Notifies the user that their tile was added.
     */
    void onTileAddComplete();

    /**
     * Notifies the user that their tiles were updated.
     */
    void onTileUpdateComplete(int numberOfTilesUpdated);

    /**
     * Remove item from the list and database.
     * @param tile an object containing weather information.
     */
    void onTileDeletedPublish(Tile tile);
}
