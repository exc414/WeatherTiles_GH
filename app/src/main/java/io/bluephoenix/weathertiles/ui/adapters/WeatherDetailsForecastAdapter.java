package io.bluephoenix.weathertiles.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.core.data.model.db.WeatherDetail;
import io.bluephoenix.weathertiles.ui.views.WeatherDetailForecastRowView;
import io.bluephoenix.weathertiles.util.Constant;

/**
 * @author Carlos A. Perez
 */
public class WeatherDetailsForecastAdapter extends
        RecyclerView.Adapter<WeatherDetailsForecastAdapter.ForecastHolder>
        implements ITabSelection.DetailsForecastAdapter
{
    private SparseArray<List<WeatherDetail.DetailRows>> detailRowsList;
    private int selectedPosition = 0; //First day

    WeatherDetailsForecastAdapter(int selectedPosition)
    {
        this.selectedPosition = selectedPosition;
    }

    @Override
    public ForecastHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weather_detail_forecast, parent, Constant.SHOULD_ATTACH_NOW);

        return new ForecastHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastHolder holder, int position)
    {
        holder.forecastRowView.setWeatherIconContent(detailRowsList.get(selectedPosition)
                .get(position).getWeatherId(),
                detailRowsList.get(selectedPosition).get(position).getIsDayTime());

        holder.forecastRowView.setTemp(detailRowsList.get(selectedPosition)
                .get(position).getTemp());
        holder.forecastRowView.setTimeShown(detailRowsList.get(selectedPosition)
                .get(position).getTimeShown());

        holder.forecastRowView.setWindSpeed(detailRowsList.get(selectedPosition)
                .get(position).getWind());

        holder.forecastRowView.setHumidity(detailRowsList.get(selectedPosition)
                .get(position).getHumidity());

        holder.forecastRowView.setRainChange(detailRowsList.get(selectedPosition)
                .get(position).getRain());
    }

    @Override
    public int getItemCount()
    {
        return detailRowsList.get(selectedPosition).size();
    }

    /**
     * Set the data from the Vertical RecyclerView viewHolder.
     * @param detailRowsList an sparse array of lists with each day's worth of data.
     */
    void setDetailRowsList(SparseArray<List<WeatherDetail.DetailRows>> detailRowsList)
    {
        this.detailRowsList = detailRowsList;
        notifyDataSetChanged();
    }

    /**
     * When the user selects a new day tab change the data to the correct day's data.
     * @param selectedPosition an int which contains the user day tab position.
     */
    @Override
    public void setSelectedPosition(int selectedPosition)
    {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    static class ForecastHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.forecastRow) WeatherDetailForecastRowView forecastRowView;

        ForecastHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}