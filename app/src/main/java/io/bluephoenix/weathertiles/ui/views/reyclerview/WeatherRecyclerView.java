package io.bluephoenix.weathertiles.ui.views.reyclerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class WeatherRecyclerView extends RecyclerView
{
    public WeatherRecyclerView(Context context) { super(context); }

    public WeatherRecyclerView(Context context, AttributeSet attrs) { super(context, attrs); }

    public WeatherRecyclerView(Context context, AttributeSet attrs, int defStyle)
    { super(context, attrs, defStyle); }

    @Override
    protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params,
                                                   int index, int count)
    {
        final LayoutManager layoutManager = getLayoutManager();
        if(getAdapter() != null && layoutManager instanceof GridLayoutManager)
        {
            GridLayoutAnimationController.AnimationParameters animationParams =
                    (GridLayoutAnimationController.AnimationParameters)
                            params.layoutAnimationParameters;

            if(animationParams == null)
            {
                animationParams = new GridLayoutAnimationController.AnimationParameters();
                params.layoutAnimationParameters = animationParams;
            }

            animationParams.count = count;
            animationParams.index = index;

            final int columns = ((GridLayoutManager) layoutManager).getSpanCount();
            animationParams.columnsCount = columns;
            animationParams.rowsCount = count / columns;

            final int invertedIndex = count - 1 - index;
            animationParams.column = columns - 1 - (invertedIndex % columns);
            animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns;
        }
        else
        {
            super.attachLayoutAnimationParameters(child, params, index, count);
        }
    }
}
