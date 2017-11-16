package io.bluephoenix.weathertiles.ui.views.reyclerview.gestures;

import android.graphics.Canvas;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class DragDropSwipeHelper extends ItemTouchHelper.Callback
{
    private final IDragDropSwipeHelper.Adapter adapter;
    private final float escapeVelocity = 960;
    private IDragDropSwipeHelper.ViewHolder itemViewHolder;
    private int tileOriginalPosition = -1;
    private int tileTargetPosition = -1;
    private boolean hasTileBeenDropped = true;

    public DragDropSwipeHelper(IDragDropSwipeHelper.Adapter adapter)
    {
        this.adapter = adapter;
    }

    /**
     * Should return a composite flag which defines the enabled move directions in each state
     * (idle, swiping, dragging).
     * <p>
     * Instead of composing this flag manually, you can use {@link #makeMovementFlags(int, int)}
     * or {@link #makeFlag(int, int)}.
     * <p>
     * This flag is composed of 3 sets of 8 bits, where first 8 bits are for IDLE state, next
     * 8 bits are for SWIPE state and third 8 bits are for DRAG state.
     * Each 8 bit sections can be constructed by simply OR'ing direction flags defined in
     * {@link ItemTouchHelper}.
     * <p>
     * For example, if you want it to allow swiping LEFT and RIGHT but only allow starting to
     * swipe by swiping RIGHT, you can return:
     * <pre>
     *      makeFlag(ACTION_STATE_IDLE, RIGHT) | makeFlag(ACTION_STATE_SWIPE, LEFT | RIGHT);
     * </pre>
     * This means, allow right movement while IDLE and allow right and left movement while
     * swiping.
     *
     * @param recyclerView The RecyclerView to which IDragDropSwipeHelper is attached.
     * @param viewHolder   The ViewHolder for which the movement information is necessary.
     * @return flags specifying which movements are allowed on this ViewHolder.
     * @see #makeMovementFlags(int, int)
     * @see #makeFlag(int, int)
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
    {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        final int swipeFlags = ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * Returns whether IDragDropSwipeHelper should start a drag and drop operation if an item is
     * long pressed.
     * <p>
     * Default value returns true but you may want to disable this if you want to start
     * dragging on a custom view touch using
     * {@link ItemTouchHelper#startDrag(RecyclerView.ViewHolder)}.
     *
     * @return True if IDragDropSwipeHelper should start dragging an item when it is long
     * pressed, false otherwise. Default value is <code>true</code>.
     * @see ItemTouchHelper#startDrag(RecyclerView.ViewHolder)
     */
    @Override
    public boolean isLongPressDragEnabled()
    {
        return true;
    }

    /**
     * Called when the ViewHolder swiped or dragged by the IDragDropSwipeHelper is changed.
     * <p/>
     * If you override this method, you should call super.
     *
     * @param viewHolder  The new ViewHolder that is being swiped or dragged. Might be null if
     *                    it is cleared.
     * @param actionState One of {@link ItemTouchHelper#ACTION_STATE_IDLE},
     *                    {@link ItemTouchHelper#ACTION_STATE_SWIPE} or
     *                    {@link ItemTouchHelper#ACTION_STATE_DRAG}.
     * @see #clearView(RecyclerView, RecyclerView.ViewHolder)
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState)
    {
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG)
        {
            itemViewHolder = (IDragDropSwipeHelper.ViewHolder) viewHolder;
            itemViewHolder.onItemSelected();
        }
        else if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
        {
            //Once the swipe state has been activate change the background to alert the user
            //of deletion.
            itemViewHolder = (IDragDropSwipeHelper.ViewHolder) viewHolder;
            itemViewHolder.onItemActivated();
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive)
    {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        adapter.onItemMoving(isCurrentlyActive);
    }

    /**
     * Called when
     * {@link #onMove(RecyclerView, RecyclerView.ViewHolder, RecyclerView.ViewHolder)}
     * returns true.
     * <p>
     * ItemTouchHelper does not create an extra Bitmap or View while dragging, instead, it
     * modifies the existing View. Because of this reason, it is important that the View is
     * still part of the layout after it is moved. This may not work as intended when swapped
     * Views are close to RecyclerView bounds or there are gaps between them (e.g. other Views
     * which were not eligible for dropping over).
     * <p>
     * This method is responsible to give necessary hint to the LayoutManager so that it will
     * keep the View in visible area. For example, for LinearLayoutManager, this is as simple
     * as calling {@link LinearLayoutManager#scrollToPositionWithOffset(int, int)}.
     * <p>
     * Default implementation calls {@link RecyclerView#scrollToPosition(int)} if the View's
     * new position is likely to be out of bounds.
     * <p>
     * It is important to ensure the ViewHolder will stay visible as otherwise, it might be
     * removed by the LayoutManager if the move causes the View to go out of bounds. In that
     * case, drag will end prematurely.
     *
     * @param recyclerView The RecyclerView controlled by the ItemTouchHelper.
     * @param viewHolder   The ViewHolder under user's control.
     * @param fromPos      The previous adapter position of the dragged item (before it was
     *                     moved).
     * @param target       The ViewHolder on which the currently active item has been dropped.
     * @param toPos        The new adapter position of the dragged item.
     * @param x            The updated left value of the dragged View after drag translations
     *                     are applied. This value does not include margins added by
     *                     {@link RecyclerView.ItemDecoration}s.
     * @param y            The updated top value of the dragged View after drag translations
     *                     are applied. This value does not include margins added by
     *                     {@link RecyclerView.ItemDecoration}s.
     */
    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y)
    {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);

        /*
         * Get the original position of the tile. We used this method because its position
         * value its not relative to the number of tiles visible in the screen but rather
         * the total number of tiles (unlike clearView).
         */
        if(hasTileBeenDropped)
        {
            tileOriginalPosition = fromPos;
            //Tile is bring dragged now, therefore make sure to not get the new
            //fromPos as we want the original.
            hasTileBeenDropped = false; //reset
        }

        //On all updates the possible position of the tile.
        tileTargetPosition = toPos;
    }

    /**
     * Called when IDragDropSwipeHelper wants to move the dragged item from its old position to
     * the new position.
     * <p>
     * If this method returns true, IDragDropSwipeHelper assumes {@code viewHolder}
     * has been moved to the adapter position of {@code target} ViewHolder
     * ({@link RecyclerView.ViewHolder#getAdapterPosition()
     * ViewHolder#getAdapterPosition()}).
     * <p>
     * If you don't support drag & drop, this method will never be called.
     *
     * @param recyclerView The RecyclerView to which IDragDropSwipeHelper is attached to.
     * @param viewHolder   The ViewHolder which is being dragged by the user.
     * @param target       The ViewHolder over which the currently active item is being
     *                     dragged.
     * @return True if the {@code viewHolder} has been moved to the adapter position of
     * {@code target}.
     * @see #onMoved(RecyclerView, RecyclerView.ViewHolder, int,
     * RecyclerView.ViewHolder, int, int, int)
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target)
    {
        adapter.onItemDragging(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled()
    {
        return true;
    }

    /**
     * Defines the minimum velocity which will be considered as a swipe action by the user.
     * <p>
     * You can increase this value to make it harder to swipe or decrease it to make it easier.
     * Keep in mind that ItemTouchHelper also checks the perpendicular velocity and makes sure
     * current direction velocity is larger then the perpendicular one. Otherwise, user's
     * movement is ambiguous. You can change the threshold by overriding
     * {@link #getSwipeVelocityThreshold(float)}.
     * <p>
     * The velocity is calculated in pixels per second.
     * <p>
     * The default framework value is passed as a parameter so that you can modify it with a
     * multiplier.
     *
     * @param defaultValue The default value (in pixels per second) used by the
     *                     ItemTouchHelper.
     * @return The minimum swipe velocity. The default implementation returns the
     * <code>defaultValue</code> parameter.
     * @see #getSwipeVelocityThreshold(float)
     */
    @Override
    public float getSwipeEscapeVelocity(float defaultValue)
    {
        return super.getSwipeEscapeVelocity(escapeVelocity);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
    {
        adapter.onItemDeleted(viewHolder.getAdapterPosition());
    }

    /**
     * Called by the ItemTouchHelper when the user interaction with an element is over and it
     * also completed its animation.
     * <p>
     * This is a good place to clear all changes on the View that was done in
     * {@link #onSelectedChanged(RecyclerView.ViewHolder, int)},
     *
     * @param recyclerView The RecyclerView which is controlled by the ItemTouchHelper.
     * @param viewHolder   The View that was interacted by the user.
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
    {
        super.clearView(recyclerView, viewHolder);
        itemViewHolder = (IDragDropSwipeHelper.ViewHolder) viewHolder;

        //If the position is not the same as the original then the sorting
        //will be changed to no sort. Its up to the user to set the sort again.
        if(tileOriginalPosition != tileTargetPosition) { adapter.onItemMovedResetSort(); }

        //Reset now that the tile has been dropped for good. So we can get a new fromPos
        //when onMoved is called again.
        hasTileBeenDropped = true;
        itemViewHolder.onItemClear();
    }
}