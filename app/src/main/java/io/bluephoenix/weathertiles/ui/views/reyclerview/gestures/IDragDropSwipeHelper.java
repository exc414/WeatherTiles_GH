package io.bluephoenix.weathertiles.ui.views.reyclerview.gestures;

import android.support.v7.widget.RecyclerView;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public interface IDragDropSwipeHelper
{
    interface Adapter
    {
        /**
         * Called when an item has been dragged far enough to trigger a move.
         * This is called every time
         * an item is shifted, and not at the end of a "drop" event.
         *
         * @param fromPosition The start position of the moved item.
         * @param toPosition   Then end position of the moved item.
         * @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolder)
         * @see RecyclerView.ViewHolder#getAdapterPosition()
         */
        void onItemDragging(int fromPosition, int toPosition);

        /**
         * Delete the tile once the clearView() method is finished.
         *
         * @param position an int that represents the item to delete.
         */
        void onItemDeleted(int position);

        /**
         * Reset the user select sort to NO_SORT if the user moves a tile
         * out of position.
         */
        void onItemMovedResetSort();

        /**
         * Gets whether the user is moving (drag/drop) or swiping the tile.
         * Basically any motion of tiles.
         * @param isItemMoving boolean value whether the item/tile is active
         */
        void onItemMoving(boolean isItemMoving);

    }

    interface ViewHolder
    {
        /**
         * Called when the {@link android.support.v7.widget.helper.ItemTouchHelper}
         * first registers an item as being moved or swiped.
         * Implementations should update the item view to indicate it's active state.
         */
        void onItemSelected();

        /**
         * Show/hide the overlay image view to let the user know that a swipe action
         * will delete the tile.
         */
        void onItemActivated();

        /**
         * Called when the {@link android.support.v7.widget.helper.ItemTouchHelper}
         * has completed the move or swipe, and the active item
         * state should be cleared.
         */
        void onItemClear();
    }
}
