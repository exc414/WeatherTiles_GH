package io.bluephoenix.weathertiles.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * @author Carlos A. Perez
 */
public abstract class BaseActivity extends AppCompatActivity
{
    protected Unbinder unbinder;

    @Override
    public void setContentView(int layoutResID)
    {
        super.setContentView(layoutResID);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home) { finish(); }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy()
    {
        unbinder.unbind();
        super.onDestroy();
    }

    protected void start(Class<? extends BaseActivity> activity)
    {
        startActivity(new Intent(this, activity));
    }

    protected void start(Class<? extends BaseActivity> activity, int result)
    {
        startActivityForResult(new Intent(this, activity), result);
    }

    protected void setActionBarBackButton(Toolbar toolbar)
    {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
