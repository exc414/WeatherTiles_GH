package io.bluephoenix.weathertiles.ui.views.reyclerview;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class GLMWeather extends GridLayoutManager
{
    private RecyclerView.SmoothScroller smoothScroller;
    private float speed;

    /**
     * Smooth scroller for the weather recyclerView.
     *
     * @param context   Global information about an application environment.
     * @param spanCount An int detonating how many columns the grid will have.
     * @param speed     A float detonating scrolling speed.
     */
    public GLMWeather(Context context, int spanCount, float speed)
    {
        super(context, spanCount);
        this.speed = speed;
    }

    /**
     * Smooth scroll to the specified adapter position.
     *
     * @param recyclerView  The RecyclerView to which this layout manager is attached
     * @param state         Current State of RecyclerView
     * @param position      Scroll to this adapter position.
     */
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView,
                                       RecyclerView.State state,
                                       int position)
    {
        smoothScroller = new SmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    /**
     * Set the scrolling speed of the recyclerView.
     * @param speed A float detonating scrolling speed.
     */
    public void setSpeedForScroll(float speed)
    {
        this.speed = speed;
    }

    /**
     * RecyclerView.SmoothScroller implementation which uses a LinearInterpolator until
     * the target position becomes a child of the RecyclerView and then uses a
     * DecelerateInterpolator to slowly approach to target position.
     * Sets the speed using calculateSpeedPerPixel.
     */
    private class SmoothScroller extends LinearSmoothScroller
    {
        SmoothScroller(Context context) { super(context); }

        /**
         * Compute the scroll vector for a given target position.
         * This method can return null if the layout manager cannot calculate a
         * scroll vector for the given position (e.g. it has no current scroll position).
         *
         * @param targetPosition The position to which the scroller is scrolling
         * @return The scroll vector for a given target position
         */
        @Override
        public PointF computeScrollVectorForPosition(int targetPosition)
        {
            return GLMWeather.this
                    .computeScrollVectorForPosition(targetPosition);
        }

        /**
         * Calculates the scroll speed.
         *
         * @param displayMetrics DisplayMetrics to be used for real dimension calculations.
         * @return The time (in ms) it should take for each pixel. For instance,
         * if returned value is 2 ms, it means scrolling 1000 pixels with
         * LinearInterpolation should take 2 seconds.
         */
        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics)
        {
            return super.calculateSpeedPerPixel(displayMetrics) * speed;
        }

        //Align child view's left or top with parent view's left or top
        @Override
        protected int getVerticalSnapPreference() { return SNAP_TO_START; }
    }
}
