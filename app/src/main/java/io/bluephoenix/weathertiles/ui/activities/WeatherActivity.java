package io.bluephoenix.weathertiles.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;

import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindInt;
import butterknife.BindView;
import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.core.common.DialogDef;
import io.bluephoenix.weathertiles.core.common.DialogDef.DialogType;
import io.bluephoenix.weathertiles.core.common.SortDef;
import io.bluephoenix.weathertiles.core.common.SortDef.SortType;
import io.bluephoenix.weathertiles.core.data.model.db.Tile;
import io.bluephoenix.weathertiles.core.presenters.IWeatherContract;
import io.bluephoenix.weathertiles.core.presenters.WeatherPresenter;
import io.bluephoenix.weathertiles.ui.activities.settings.SettingsActivity;
import io.bluephoenix.weathertiles.ui.adapters.IOnTileActionPublish;
import io.bluephoenix.weathertiles.ui.adapters.WeatherAdapter;
import io.bluephoenix.weathertiles.ui.dialogs.Dialogs;
import io.bluephoenix.weathertiles.ui.dialogs.DialogsPublish;
import io.bluephoenix.weathertiles.ui.views.SnackView;
import io.bluephoenix.weathertiles.ui.views.reyclerview.GLMWithSmoothScroller;
import io.bluephoenix.weathertiles.ui.views.reyclerview.TileItemAnimator;
import io.bluephoenix.weathertiles.ui.views.reyclerview.WeatherRecyclerView;
import io.bluephoenix.weathertiles.ui.views.reyclerview.gestures.DragDropSwipeHelper;
import io.bluephoenix.weathertiles.util.Constant;
import io.bluephoenix.weathertiles.util.SortTiles;
import io.bluephoenix.weathertiles.util.Util;
import io.realm.Realm;

import static io.bluephoenix.weathertiles.core.common.TempScaleDef.CELSIUS;
import static io.bluephoenix.weathertiles.core.common.TempScaleDef.FAHRENHEIT;

/**
 * @author Carlos A. Perez Zubizarreta
 */
@SuppressWarnings("deprecation")
public class WeatherActivity extends BaseActivity implements
        IWeatherContract.IPublishToView, IOnTileActionPublish, DialogsPublish,
        NavigationView.OnNavigationItemSelectedListener
{
    @BindView(R.id.weatherRV) WeatherRecyclerView weatherRecyclerView;
    @BindInt(R.integer.grid_columns) int gridColumns;
    @BindDrawable(R.drawable.ic_temperature_fahrenheit_grey_300_24dp)
    Drawable fahrenheitDrawable;
    @BindDrawable(R.drawable.ic_temperature_celsius_grey_300_24dp)
    Drawable celsiusDrawable;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.navView) NavigationView navigationView;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;

    private SharedPreferences sharedPreferences;
    private Snackbar snackbar;
    private SnackView snackView;
    private Dialogs dialogs;

    private WeatherPresenter presenter;
    private WeatherAdapter weatherAdapter;
    private GLMWithSmoothScroller glmWithSmoothScroller;

    private String appName = "Weather Tiles";
    private final int SEARCH_ACTIVITY_RESULT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Get recyclerView's height once its has been rendered completely.
        ViewTreeObserver viewTreeObserver = weatherRecyclerView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                Util.totalRVHeight = weatherRecyclerView.getHeight();
                ViewTreeObserver viewTreeObserver = weatherRecyclerView.getViewTreeObserver();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                { viewTreeObserver.removeOnGlobalLayoutListener(this); }
                else { viewTreeObserver.removeGlobalOnLayoutListener(this); }
            }
        });

        weatherAdapter = new WeatherAdapter(this);
        weatherAdapter.registerOnTilePublishCallback(this);

        glmWithSmoothScroller = new GLMWithSmoothScroller(this, gridColumns,
                Constant.NORMAL_SCROLL_SPEED);

        weatherRecyclerView.setLayoutManager(glmWithSmoothScroller);
        weatherRecyclerView.setAdapter(weatherAdapter);
        weatherRecyclerView.setItemAnimator(new TileItemAnimator());
        weatherRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(
                this, R.anim.gl_animation_from_bottom));

        //Drag n' Drop
        ItemTouchHelper.Callback callback = new DragDropSwipeHelper(weatherAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(weatherRecyclerView);

        //Create a share preference object to be passed to the presenter.
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext());

        attachPresenter();
        presenter.initTiles();
        dialogs = new Dialogs(this);
        dialogs.registerDialogPublishCallback(this);

        //If first run show the tutorial view to the user
        if(!presenter.getHasRunOnce())
        {
            Util.showFirstLaunchOverlayView(this, toolbar);
            presenter.setHasRunOnce();
        }
    }

    private void attachPresenter()
    {
        presenter = (WeatherPresenter) getLastCustomNonConfigurationInstance();
        if(presenter == null) { presenter = new WeatherPresenter(sharedPreferences); }
        presenter.attachView(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance()
    {
        return presenter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        presenter.saveTiles(weatherAdapter.getTileList());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //Make sure to set the correct temp scale for the adapter. Celsius or Fah.
        weatherAdapter.updateDegreeType(presenter.getDefaultTempScale());
    }

    @Override
    protected void onPause()
    {
        presenter.saveTilePosition(weatherAdapter.getTilesPosition());
        super.onPause();
    }

    //Close/release everything.
    @Override
    protected void onDestroy()
    {
        if(!Realm.getDefaultInstance().isClosed())
        { Realm.getDefaultInstance().close(); }

        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem tempScale = menu.findItem(R.id.action_change_degrees);
        //If fah was the last chosen temp scale before the user exited the app
        //make sure to load over celsius (default).
        if(presenter.getDefaultTempScale() == FAHRENHEIT)
        { tempScale.setIcon(fahrenheitDrawable); }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Handles what happens when a button in the toolbar gets clicked.
     * @param item a MenuItem object with the id of the pressed button.
     * @return whether the item was clicked.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.action_search)
        {
            if(weatherAdapter.getItemCount() >= presenter.getMaxNumberOfTiles())
            {
                Util.toastMaker(getString(R.string.toast_max_number_of_tiles_reached));
            }
            else { start(SearchActivity.class, SEARCH_ACTIVITY_RESULT); }

            return true;
        }
        else if(id == R.id.action_change_degrees)
        {
            //Refresh adapter on click with the correct values.
            //Set the defaultTempScale value to opposite value of the
            //currently selected.
            if(presenter.getDefaultTempScale() == CELSIUS)
            {
                presenter.setDefaultTempScale(FAHRENHEIT);
                weatherAdapter.updateDegreeType(FAHRENHEIT);
                item.setIcon(fahrenheitDrawable);
            }
            else if(presenter.getDefaultTempScale() == FAHRENHEIT)
            {
                presenter.setDefaultTempScale(CELSIUS);
                weatherAdapter.updateDegreeType(CELSIUS);
                item.setIcon(celsiusDrawable);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when an item in the navigation menu is selected.
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.addTile:
                 start(SearchActivity.class, SEARCH_ACTIVITY_RESULT);
                 drawerLayout.closeDrawers();
                 return true;

            case R.id.sortTile:
                 dialogs.openSortMenu(presenter.getDefaultSort());
                 drawerLayout.closeDrawers();
                 return true;

            case R.id.settings:
                 start(SettingsActivity.class);
                 drawerLayout.closeDrawers();
                 return true;

            case R.id.shareApp:
                 startActivity(Intent.createChooser(Util.shareApp(this, appName),
                         "Share via"));
                 return true;

            case R.id.rateApp:
                 Util.openAppInMarket(this, WeatherActivity.this.getPackageName());
                 return true;

            case R.id.leaveFeedback:
                 Intent intent = Util.leaveFeedback(appName);
                 if(intent.resolveActivity(getPackageManager()) != null)
                 { startActivity(intent); }
                 return true;

            case R.id.about:
                 start(AboutActivity.class);
                 return true;

            case R.id.quoteMachine:
                 Util.openAppInMarket(this, Constant.QUOTE_MACHINE);
                 return true;

            case R.id.appUninstaller:
                 Util.openAppInMarket(this, Constant.APP_UNINSTALLER);
                 return true;

            case R.id.clearHandset:
                 Util.openAppInMarket(this, Constant.CLEAR_HANDSET);
                 return true;

            case R.id.stressCPU:
                 Util.openAppInMarket(this, Constant.STRESS_CPU);
                 return true;

            default: break;
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Called after the autocomplete activity has finished to get its result.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == SEARCH_ACTIVITY_RESULT)
        {
            //Data provides the city Id.
            if(resultCode == RESULT_OK)
            {
                //Show loading message to the user while the tile info is downloaded.
                snackBarWithCustomView().show();
                snackView.setMessage(getString(R.string.event_message_loading_adding));
                snackView.startLoadingAnimation();

                presenter.createTile(data.getLongExtra(
                        Constant.SA_INTENT_KEY_SEARCH_RESULT, 0));
            }
            else if(resultCode == Constant.RESULT_NULL)
            {
                tileEvent(getString(R.string.event_message_error_search));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Use to load the tiles on application startup.
     * Run animation at startup.
     * @param tileList list of tile objects.
     */
    @Override
    public void initTiles(List<Tile> tileList)
    {
        weatherAdapter.initTiles(tileList);
        weatherRecyclerView.scheduleLayoutAnimation();
    }

    /**
     * Adds a single tile to the recycler view.
     * @param tile object with weather information.
     */
    @Override
    public void addTile(Tile tile)
    {
        weatherAdapter.addTile(tile, presenter.getDefaultSort(), gridColumns,
                weatherRecyclerView, glmWithSmoothScroller,
                presenter.getNewTileBehaviourFlags());
    }

    /**
     * Updates existing tiles in the adapter.
     * @param tiles a list of tile objects with weather information.
     */
    @Override
    public void updateTiles(List<Tile> tiles)
    {
        weatherAdapter.updateTiles(tiles, presenter.getDefaultSort());
    }

    /**
     * Warn the user there an issue or action being preform.
     */
    @Override
    public void notifyUserAlert(@DialogType int dialogType)
    {
        switch(dialogType)
        {
            case DialogDef.NO_INTERNET:
                 if(snackbar != null && snackbar.isShown()) { snackbar.dismiss(); }
                 dialogs.notifyUser(R.string.dialog_no_internet_title,
                         R.string.dialog_no_internet_body,
                         R.drawable.ic_signal_cellular_off_grey_300_24dp);
                 break;

            case DialogDef.DOWNLOADING:
                 dialogs.downloadingData(R.string.dialog_downloading_data_title,
                         R.string.dialog_downloading_data_body,
                         R.drawable.ic_file_download_grey_300_24dp);
                 break;

            case DialogDef.DOWNLOADING_FINISHED:
                 dialogs.dismissDownloadingData();
                 break;

            default: break;
        }
    }

    /**
     * Use to reset the sort when the user moves a tile out of order.
     * Note that if the user simply drags a tile and drops it back in the
     * same position this wont get called.
     */
    @Override
    public void onTileMovedResetSort()
    {
        presenter.setDefaultSort(SortDef.NOSORT);
    }

    /**
     * Once the addition of the tile is complete stop the loading animation
     * and show a completion message and dismiss the snack bar.
     */
    @Override
    public void onTileAddComplete()
    {
        snackView.setMessageDone(getString(R.string.event_message_success_adding));
        snackView.startFadeAnimation(snackbar);
    }

    /**
     * Once the tiles are updated show the user a snack bar with the number of updates.
     * @param numberOfTilesUpdated An int detonating the number of tiles updated.
     */
    @Override
    public void onTileUpdateComplete(int numberOfTilesUpdated)
    {
        tileEvent(getResources().getQuantityString(
                R.plurals.event_message_success_updating,
                numberOfTilesUpdated, numberOfTilesUpdated));
    }

    /**
     * Delete one tile from the recycler view adapter.
     */
    @Override
    public void onTileDeletedPublish(Tile tile) { presenter.deleteTile(tile); }

    /**
     * Sort tiles as per the user's selection.
     * If the user chooses the same sort that is already active don't do anything.
     * If NOSORT is chosen there is no point in calling sortTiles.
     * @param sortType an int detonating the user's sort choice.
     */
    @Override
    public void setUserSortSelection(@SortType int sortType)
    {
        if(sortType != presenter.getDefaultSort() && sortType != SortDef.NOSORT)
        {
            SortTiles.sort(sortType, weatherAdapter.getTileList());
            weatherAdapter.notifyItemRangeChanged(0, weatherAdapter.getItemCount());
        }
        presenter.setDefaultSort(sortType);
    }

    /**
     * Let the user know of a change to the data of the tiles.
     * @param message a string containing the message for the user.
     */
    @Override
    public void tileEvent(String message)
    {
        Snackbar.make(drawerLayout, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Create a snackBar with a custom view.
     * @return a snackBar.
     */
    private Snackbar snackBarWithCustomView()
    {
        snackbar = Snackbar.make(drawerLayout, "", Snackbar.LENGTH_INDEFINITE);

        snackbar.setCallback(new Snackbar.Callback()
        {
            @Override
            public void onShown(Snackbar sb)
            {
                super.onShown(sb);
                //Measure AFTER the snackbar is shown or else width and height will be 0.
                snackView.measureFromSnackBarDimensions(
                        sb.getView().getWidth(), sb.getView().getHeight());
            }
        });

        Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackView = new SnackView(getApplicationContext());
        snackBarLayout.addView(snackView);
        return snackbar;
    }
}