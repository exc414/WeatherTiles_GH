package io.bluephoenix.weathertiles.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.core.common.TempScaleDef;
import io.bluephoenix.weathertiles.core.common.TempScaleDef.TempScale;
import io.bluephoenix.weathertiles.core.data.model.db.WeatherDetail;
import io.bluephoenix.weathertiles.core.presenters.IWeatherDetailsContract;
import io.bluephoenix.weathertiles.core.presenters.WeatherDetailsPresenter;
import io.bluephoenix.weathertiles.ui.adapters.IDetailPositionChange;
import io.bluephoenix.weathertiles.ui.adapters.WeatherDetailsAdapter;
import io.bluephoenix.weathertiles.util.Constant;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class WeatherDetailsActivity extends BaseActivity implements
        IWeatherDetailsContract.IPublishToView, IDetailPositionChange
{
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.weatherDetailsRV) RecyclerView weatherDetailsRV;
    private WeatherDetailsAdapter weatherDetailsAdapter;
    private WeatherDetailsPresenter presenter;
    private SharedPreferences sharedPreferences;

    private int maxDetails = 3;
    private int startPos = -1;
    private int totalTiles = -1;
    //Load 3 details = 2 offsetCheck, 5 = 3, 7 = 4. maxDetails + 1 / 2 = offsetCheck.
    private int offsetCheck = 2;
    private @TempScale
    int tempScale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setActionBarBackButton(toolbar);

        startPos = getIntent().getIntExtra("position", 0);
        totalTiles = getIntent().getIntExtra("totalTiles", 0) - 1;
        tempScale = getIntent().getIntExtra("tempScale", TempScaleDef.CELSIUS);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);

        weatherDetailsRV.setLayoutManager(linearLayoutManager);
        SnapHelper snapHelper = new PagerSnapHelper();
        weatherDetailsAdapter = new WeatherDetailsAdapter(this);
        weatherDetailsAdapter.registerOnDetailPositionCallback(this);
        weatherDetailsRV.setAdapter(weatherDetailsAdapter);
        snapHelper.attachToRecyclerView(weatherDetailsRV);

        //Create a share preference object to be passed to the presenter.
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());

        attachPresenter();
        presenter.initDetails(startPos, totalTiles);
    }

    private void attachPresenter()
    {
        presenter = (WeatherDetailsPresenter) getLastCustomNonConfigurationInstance();
        if(presenter == null) { presenter = new WeatherDetailsPresenter(
                tempScale, sharedPreferences); }
        presenter.attachView(this, Constant.DONT_REGISTER_BUS);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() { return presenter; }

    /**
     * First detail to be added thus the position will always be zero. This detail
     * directly corresponds to the tile the user tap/clicked on.
     * @param weatherDetail an object with detail weather information.
     */
    @Override
    public void initDetailFirst(WeatherDetail weatherDetail)
    {
        weatherDetailsAdapter.addDetailTile(weatherDetail, 0, Constant.DONT_REMOVE);
    }

    /**
     * Adds details to the left of the first detail thus the need for the offset.
     * @param weatherDetail an object with detail weather information.
     * @param offset an int that determines the incoming detail's position on the list.
     */
    @Override
    public void initDetailsToLeftOfFirst(WeatherDetail weatherDetail, int offset)
    {
        int position = weatherDetailsAdapter.getItemCount() - offset;
        weatherDetailsAdapter.addDetailTile(weatherDetail, position, Constant.DONT_REMOVE);
        weatherDetailsRV.scrollToPosition(offset);
    }

    /**
     * Adds details to the right of the first detail. Using getItemCount() which is
     * always +1 the position of items in the list.
     * @param weatherDetail an object with detail weather information.
     */
    @Override
    public void initDetailsToRightOfFirst(WeatherDetail weatherDetail)
    {
        int position = weatherDetailsAdapter.getItemCount();
        weatherDetailsAdapter.addDetailTile(weatherDetail, position, Constant.DONT_REMOVE);
    }

    /**
     * Add detail to the left of the center tile. If lastPos is less than maxDetails
     * don't remove any details. This check is necessary because if the user starts at
     * the last detail only 2 would load. Therefore, when the users swipe to the left
     * this will get called and delete the right detail (detail in lastPos) which is
     * not wanted if there is only 2 details loaded.
     * @param weatherDetail an object with detail weather information.
     */
    @Override
    public void addDetailLeft(WeatherDetail weatherDetail)
    {
        int lastPos = weatherDetailsAdapter.getItemCount();
        if(lastPos < maxDetails)
        {
            weatherDetailsAdapter.addDetailTile(weatherDetail, 0, Constant.DONT_REMOVE);
        }
        else { weatherDetailsAdapter.addDetailTile(weatherDetail, 0, lastPos); }
    }

    /**
     * Add detail to the right of the center tile. Read addDetailLeft but instead of last
     * detail position it will be the starting position of 0.
     * @param weatherDetail an object with detail weather information.
     */
    @Override
    public void addDetailRight(WeatherDetail weatherDetail)
    {
        int lastPos = weatherDetailsAdapter.getItemCount();
        if(lastPos < maxDetails)
        {
            weatherDetailsAdapter.addDetailTile(weatherDetail, lastPos, Constant.DONT_REMOVE);
        }
        else { weatherDetailsAdapter.addDetailTile(weatherDetail, lastPos, 0); }
    }

    @Override
    public void setMaxNumberOfTiles(int maxNumberOfTiles)
    {
        weatherDetailsAdapter.setSelectedPosArray(maxNumberOfTiles);
    }

    /**
     * To get the direction of swipe/scrolling of the adapter. Tried 3 things. Extending
     * PagerSnapHelper and overriding the findSnapView. This did not work because it if
     * the scrolling was to fast it would skip items until scrolling was slowed down.
     * <p>
     * Tried using the recyclerView state by adding a scroll listener. This did not work
     * because state idle (meaning when scrolling is done) also skips items.
     * Scroll Settling does not, this in conjunction with onScrolled which gives the scroll
     * direction was tried as well.
     * The problem here is the scrolling direction can go rapidly (skipping values)
     * from -50 to 0 or 50. Depending on how the user is dragging, which creates
     * unexpected or rather hard to cope with behaviour.
     * <p>
     * Using onViewAttachedToWindow and onViewDetachedFromWindow. This works because no
     * matter the scroll speed it will always be called. Using on onViewAttachedToWindow
     * you can get direction of the scroll by comparing the starting position and the
     * new attached view position. Bigger = right, less = left.
     * <p>
     * The offsetCheck is gather based on the number of tiles that can be loaded at one
     * time. The check is done to make sure that when nearing 0 or the last position
     * there is no array out of bounds exception thrown.
     * <p>
     * The increment and decrement of startPos is done so that the position of the
     * center detail tile is always known. Else the addition to its left and right
     * would not work properly.
     *
     * @param detailsPos an int which dictates which way the recyclerView was swiped.
     */
    @Override
    public void nextOrPrev(int detailsPos)
    {
        if(detailsPos < startPos && (startPos - offsetCheck) >= 0)
        {
            presenter.addDetailLeft(startPos - offsetCheck);
            startPos--;
        }
        else if(detailsPos > startPos && (startPos + offsetCheck) <= totalTiles)
        {
            presenter.addDetailRight(startPos + offsetCheck);
            startPos++;
        }
    }

    @Override
    protected void onDestroy()
    {
        presenter.detachView(Constant.DONT_DEREGISTER_BUS);
        super.onDestroy();
    }
}