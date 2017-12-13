package io.bluephoenix.weathertiles.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.core.data.model.db.WeatherDetail;
import io.bluephoenix.weathertiles.ui.views.WeatherDetailDaysView;
import io.bluephoenix.weathertiles.ui.views.WeatherDetailHeaderView;
import io.bluephoenix.weathertiles.util.Constant;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class WeatherDetailsAdapter extends
        RecyclerView.Adapter<WeatherDetailsAdapter.TileDetailsHolder>
        implements ITabSelection.DetailsAdapter
{
    private List<WeatherDetail> tileList = new ArrayList<>();
    private IDetailPositionChange detailPositionChange;
    private int posOnAttach = -1;
    private int[] selectedPosArray;

    public WeatherDetailsAdapter(Activity activity) { ButterKnife.bind(this, activity); }

    @Override
    public TileDetailsHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weather_tile_details, parent, Constant.SHOULD_ATTACH_NOW);

        return new TileDetailsHolder(view);
    }

    public void registerOnDetailPositionCallback(IDetailPositionChange detailPositionChange)
    {
        this.detailPositionChange = detailPositionChange;
    }

    @Override
    public void onBindViewHolder(TileDetailsHolder holder, int position)
    {
        //Current weather display
        holder.headerView.setWeatherIconContent(tileList.get(position).getWeatherId(),
                tileList.get(position).getDayTime());
        holder.headerView.setTemp(tileList.get(position).getTemp());
        holder.headerView.setMaxTemp(tileList.get(position).getTempMax());
        holder.headerView.setMinTemp(tileList.get(position).getTempMin());
        holder.headerView.setWeatherDesc(tileList.get(position).getDescription());
        holder.headerView.setCity(tileList.get(position).getCityName());
        holder.headerView.setProvince(tileList.get(position).getProvinceName(),
                tileList.get(position).getCountryName());
        holder.headerView.setRainChance(tileList.get(position).getRainChance());
        holder.headerView.setWindSpeed(tileList.get(position).getWindSpeed());
        holder.headerView.setHumidity(tileList.get(position).getHumidity());

        int savedPosition = tileList.get(position).getSavedPosition();
        //Needed to determine if a weather tile needs to be added or removed
        holder.daysView.setSavePosition(savedPosition);
        holder.setSavedPosition(savedPosition);

        //Set the hours (vertical) recycler view data.
        holder.setDetailsRowList(tileList.get(position).getDetailRows());

        //Tab (days) positions and which values need to be loaded along with them.
        holder.daysView.setSelectedTab(selectedPosArray[savedPosition]);
        holder.setDetailsForecastDayPosition(selectedPosArray[savedPosition]);

        //Listeners
        holder.daysView.setTabSelectionListener(this);
        holder.setDetailsForecastListener();

        //Set the tab days content
        holder.daysView.setTabContent(tileList.get(position).getTabContent());

        //Color underline bar base on daytime.
        holder.daysView.underLineColor(tileList.get(position).getDayTime());
    }

    /**
     * Add tile and remove details tiles from the screen. At all times there should
     * only be loaded an specific amount of tiles therefore when one is added another
     * must be removed.
     *
     * @param weatherDetail an object with detail weather information.
     * @param addPos an int at which the weatherDetail should be added.
     * @param delPos an int with the position of the tile to remove.
     */
    public void addDetailTile(WeatherDetail weatherDetail, int addPos, int delPos)
    {
        tileList.add(addPos, weatherDetail);
        notifyItemInserted(addPos);

        if(delPos != Constant.DONT_REMOVE)
        {
            tileList.remove(delPos);
            notifyItemRemoved(delPos);
        }
    }

    @Override
    public int getItemCount() { return tileList.size(); }

    /**
     * Called when a view created by this adapter has been attached to a window.
     * <p>
     * <p>This can be used as a reasonable signal that the view is about to be seen
     * by the user. If the adapter previously freed any resources in
     * {@link #onViewDetachedFromWindow(RecyclerView.ViewHolder) onViewDetachedFromWindow}
     * those resources should be restored here.</p>
     *
     * @param holder Holder of the view being attached
     */
    @Override
    public void onViewAttachedToWindow(TileDetailsHolder holder)
    {
        super.onViewAttachedToWindow(holder);
        posOnAttach = holder.getSavedPosition();
    }

    /**
     * Called when a view created by this adapter has been detached from its window.
     * <p>
     * <p>Becoming detached from the window is not necessarily a permanent condition;
     * the consumer of an Adapter's views may choose to cache views offscreen while they
     * are not visible, attaching and detaching them as appropriate.</p>
     *
     * @param holder Holder of the view being detached
     */
    @Override
    public void onViewDetachedFromWindow(TileDetailsHolder holder)
    {
        super.onViewDetachedFromWindow(holder);

       /*
        * Check that the onAttach position and OnDetach position are not same. If it is
        * don't execute nextOrPrev as this means the user went back to the same item.
        * If there is no check this would remove the left most or right most depending
        * on the scrolling of the middle item because onAttach will always be called
        * when a new view comes into the screen. This is not desired because the middle
        * item should always have views to the left and right unless its 0 or the last
        * position.
        *
        * Items - 2 3 4 (User sees number 3 which the center item)  then scrolls to
        * item 4 which gets into the user's view (2 items are on the user's view 3 and 4).
        * User does not drag completely to item 4 and comes back to item 3.
        * if there is no check then item 2 would have been removed and item 5 added
        * creating (User sees this one) -> 3 4 5. Which is incorrect. Two items to the
        * right but none to the left. It should be 2 3 4 until the user goes completely
        * into item number 4 at which point 2 will get removed and 5 added.
        */
        if(holder.getSavedPosition() != posOnAttach)
        {
            detailPositionChange.nextOrPrev(posOnAttach);
        }
    }

    /**
     * Initialized the selected position array.
     * @param maxNumberOfTiles an int which detonates how large the array needs to be made.
     */
    public void setSelectedPosArray(int maxNumberOfTiles)
    {
        //Language spec guarantees initialization with zeroes. Therefore first position.
        selectedPosArray = new int[maxNumberOfTiles];
    }

    /**
     * TODO Toss up on implementing this.
     * On pause saved the values to the tile database. When the user comes back all the
     * values will be remembered. Don't know if this desired or  its better that the values
     * go back to the first tab.
     * @return an array of ints with the user's saved selections.
     */
    public int[] getSelectedPosArray()
    {
        return selectedPosArray;
    }

    /**
     * Gets the selected position from the daysView and saves it to the selectedPosArray.
     * Uses the weather details saved position as the array index. This is needed because
     * the weather details objects are removed/added when the max is reached (in this case 3).
     * At which point the selected position is forgotten. This arrays keeps track of it.
     *
     * @param selectedPosition  an int with the user's selected position
     * @param savedPosition     an int with the saved position of the weather detail think of it
     *                          like an id.
     */
    @Override
    public void setSelectedPosition(int selectedPosition, int savedPosition)
    {
        Log.i(Constant.TAG, "setSelectedPosition - Selected Position : " + selectedPosition);
        Log.i(Constant.TAG, "setSelectedPosition - Saved Position : " + savedPosition);
        selectedPosArray[savedPosition] = selectedPosition;
    }

    static class TileDetailsHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.weatherDetailsHeader) WeatherDetailHeaderView headerView;
        @BindView(R.id.weatherDetailsDays) WeatherDetailDaysView daysView;
        @BindView(R.id.detailInnerRV) RecyclerView detailInnerRV;
        private WeatherDetailsForecastAdapter weatherDetailsForecastAdapter;
        private int savedPosition;
        private int selectedPosition;

        TileDetailsHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            weatherDetailsForecastAdapter = new WeatherDetailsForecastAdapter(selectedPosition);
            detailInnerRV.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            detailInnerRV.setAdapter(weatherDetailsForecastAdapter);
        }

        int getSavedPosition() { return savedPosition; }

        void setSavedPosition(int savedPosition) { this.savedPosition = savedPosition; }

        void setDetailsRowList(SparseArray<List<WeatherDetail.DetailRows>> detailsRowList)
        {
            weatherDetailsForecastAdapter.setDetailRowsList(detailsRowList);
        }

        void setDetailsForecastListener()
        {
            daysView.setTabSelectionListener(weatherDetailsForecastAdapter);
        }

        //Set the position from the selectPosArray so the recyclerView will know
        //which data set (day) to show.
        void setDetailsForecastDayPosition(int selectedPosition)
        {
            this.selectedPosition = selectedPosition;
        }
    }
}