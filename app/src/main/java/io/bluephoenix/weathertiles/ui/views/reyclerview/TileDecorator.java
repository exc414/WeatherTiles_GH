package io.bluephoenix.weathertiles.ui.views.reyclerview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Carlos A. Perez
 */
public class TileDecorator extends RecyclerView.ItemDecoration
{
    private int padding;

    public TileDecorator(int padding)
    {
        this.padding = padding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state)
    {
        outRect.left = padding;
        outRect.right = padding;
        outRect.top = padding;
        outRect.bottom = padding;
    }
}
