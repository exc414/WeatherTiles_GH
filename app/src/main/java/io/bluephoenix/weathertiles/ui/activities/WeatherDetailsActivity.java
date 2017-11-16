package io.bluephoenix.weathertiles.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;

import butterknife.BindView;
import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.core.presenters.IDetailsContract;
import io.bluephoenix.weathertiles.ui.adapters.WeatherDetailsAdapter;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * @author Carlos A. Perez Zubizarreta
 */
//TODO Pending
public class WeatherDetailsActivity extends AppCompatActivity implements
        IDetailsContract.IPublishToView
{
    @BindView(R.id.weatherDetailsRV) RecyclerView weatherDetailsRV;
    private WeatherDetailsAdapter weatherDetailsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

       weatherDetailsRV.setLayoutManager(new LinearLayoutManager(this,
               LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new PagerSnapHelper();
        weatherDetailsAdapter = new WeatherDetailsAdapter();
        weatherDetailsRV.setAdapter(weatherDetailsAdapter);
        snapHelper.attachToRecyclerView(weatherDetailsRV);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
