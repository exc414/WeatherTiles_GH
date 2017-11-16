package io.bluephoenix.weathertiles.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class WeatherDetailsAdapter extends RecyclerView.Adapter<WeatherDetailsAdapter.TileHolder>
{
    public WeatherDetailsAdapter()
    {

    }

    @Override
    public TileHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return null;
    }

    @Override
    public void onBindViewHolder(TileHolder holder, int position)
    {

    }

    @Override
    public int getItemCount()
    {
        return 0;
    }

    static class TileHolder extends RecyclerView.ViewHolder
    {
        TileHolder(View itemView)
        {
            super(itemView);
        }
    }
}
