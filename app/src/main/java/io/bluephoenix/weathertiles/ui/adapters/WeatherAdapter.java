package io.bluephoenix.weathertiles.ui.adapters;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.core.common.SortDef.SortType;
import io.bluephoenix.weathertiles.core.common.TempScaleDef;
import io.bluephoenix.weathertiles.core.common.TempScaleDef.TempScale;
import io.bluephoenix.weathertiles.core.data.model.db.Tile;
import io.bluephoenix.weathertiles.ui.activities.WeatherDetailsActivity;
import io.bluephoenix.weathertiles.ui.views.WeatherView;
import io.bluephoenix.weathertiles.ui.views.reyclerview.GLMWeather;
import io.bluephoenix.weathertiles.ui.views.reyclerview.WeatherRecyclerView;
import io.bluephoenix.weathertiles.ui.views.reyclerview.gestures.IDragDropSwipeHelper;
import io.bluephoenix.weathertiles.util.AnimListener;
import io.bluephoenix.weathertiles.util.Constant;
import io.bluephoenix.weathertiles.util.PlaceTiles;
import io.bluephoenix.weathertiles.util.SortTiles;
import io.bluephoenix.weathertiles.util.Util;

import static io.bluephoenix.weathertiles.util.Constant.ENABLE_AUTO_SCROLL;
import static io.bluephoenix.weathertiles.util.Constant.ENABLE_BLINK_ANIMATION;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.TileHolder>
        implements IDragDropSwipeHelper.Adapter
{
    private final int RESET_VALUE = -1;

    private List<Tile> tileList = new ArrayList<>();
    private IOnTileActionPublish onTileAction;
    private int tempScale = TempScaleDef.CELSIUS;
    private int lastAddedTilePosition = RESET_VALUE;
    private int tilePosition;
    private int checkInterval = 700; //milliseconds
    private boolean isUserMovingTile = false;
    private Activity activity;

    public WeatherAdapter(Activity activity)
    {
        ButterKnife.bind(this, activity);
        this.activity = activity;
    }

    @Override
    public WeatherAdapter.TileHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weather_tile, parent, Constant.SHOULD_ATTACH_NOW);

        return new TileHolder(view, setTileHolderClick());
    }

    public void registerOnTilePublishCallback(IOnTileActionPublish onTileAction)
    {
        this.onTileAction = onTileAction;
    }

    @Override
    public void onBindViewHolder(WeatherAdapter.TileHolder holder, int position)
    {
        holder.tileView.setCityId(tileList.get(position).getCityId());
        holder.tileView.setWeatherIconContent(tileList.get(position).getWeatherId(),
                tileList.get(position).getIsDayTime());
        holder.tileView.setTempContent(tileList.get(position).getTempWithScale(tempScale));
        holder.tileView.setCityContent(tileList.get(position).getCityName());
        holder.tileView.setCountryContent(tileList.get(position).getCountryIso());

        //Animate an added tile to let the user find it easier.
        if(lastAddedTilePosition == position) { blink(holder.itemView).start(); }
    }

    private ObjectAnimator blink(View view)
    {
        lastAddedTilePosition = RESET_VALUE;

        final ObjectAnimator animatorBlink = ObjectAnimator.ofFloat(
                view, "alpha", 1.0F, 0.0F);
        animatorBlink.setDuration(450);
        animatorBlink.setInterpolator(new AccelerateInterpolator());
        animatorBlink.setRepeatMode(ValueAnimator.REVERSE);
        animatorBlink.setRepeatCount(3);
        animatorBlink.addListener(new AnimListener()
        {
            @Override
            protected void animationStarted(Animator animation) { }

            @Override
            protected void animationEnded(Animator animation)
            { onTileAction.onTileAddComplete(); }
        });
        return animatorBlink;
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * Note if this is zero nothing gets loaded. This method is checked first.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount()
    {
        return tileList.size();
    }

    /**
     * Gets whether the user is moving (drag/drop) or swiping the tile.
     * Basically any motion of tiles.
     * @param isItemMoving boolean value whether the item/tile is active
     */
    @Override
    public void onItemMoving(boolean isItemMoving)
    {
        isUserMovingTile = isItemMoving;
    }

    /**
     * Reset the user select sort to NO_SORT if the user moves a tile
     * out of position.
     */
    @Override
    public void onItemMovedResetSort()
    {
        onTileAction.onTileMovedResetSort();
    }

    /**
     * Called when an item has been dragged far enough to trigger a move.
     * This is called every time an item is shifted, and not at the end of a "drop" event.
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then end position of the moved item.
     */
    @Override
    public void onItemDragging(int fromPosition, int toPosition)
    {
        tileList.add(toPosition, tileList.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * Remove item from the list and database. Send the position
     * of the tile to the database BEFORE removing it from the list.
     * @param position an int that represents the item to delete.
     */
    @Override
    public void onItemDeleted(int position)
    {
        onTileAction.onTileDeletedPublish(tileList.get(position));
        tileList.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Trigger when a view is not visible on the screen. Stop any pending animations here.
     * If you don't clear the animation when the tile leaves the window view,
     * it will animate where it left off if it comes into view again.
     * @param holder the bane of my existence.
     */
    @Override
    public void onViewDetachedFromWindow(TileHolder holder)
    {
        holder.clearAnimation();
        super.onViewDetachedFromWindow(holder);
    }

    public List<Tile> getTileList()
    {
        return tileList;
    }

    /**
     * Get tiles to load at RV creation if any exists.
     * @param tileList list of tiles from the database
     */
    public void initTiles(List<Tile> tileList)
    {
        this.tileList = tileList;
        notifyDataSetChanged();
    }

    /**
     * Adds a tile to the list with automatic scrolling and blink
     * animation on newly added tile.
     * @param tile an object with weather information.
     */
    public void addTile(Tile tile, @SortType int sortType, int gridColumns,
                        final WeatherRecyclerView weatherRecyclerView,
                        final GLMWeather glmWeather, int flags)
    {
        tilePosition = PlaceTiles.place(sortType, tile, tileList);
        boolean isTileVisible = isTileVisible(glmWeather, tilePosition, gridColumns);
        boolean autoScroll = false;

        if((flags & ENABLE_AUTO_SCROLL) == ENABLE_AUTO_SCROLL) { autoScroll = true; }

        //If the tile is visible show the animation. If the tile is not visible and
        //autoScroll is off do not show the animation and call the onTileAddComplete
        //immediately as if we do not do this it will never get called because we are
        //not showing the animation. If the tile is not visible but autoScroll is on,
        //then show the animation.
        if((flags & ENABLE_BLINK_ANIMATION) == ENABLE_BLINK_ANIMATION)
        { lastAddedTilePosition = (isTileVisible) ? tilePosition :
                (!autoScroll) ? RESET_VALUE : tilePosition; }

        tileList.add(tilePosition, tile);
        notifyItemInserted(tilePosition);
        if(lastAddedTilePosition == RESET_VALUE) { onTileAction.onTileAddComplete(); }

        if(!isTileVisible && autoScroll)
        {
            //Only scroll when the MOVE animation is finished.
            final Handler scrollHandler = new Handler();
            scrollHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if(weatherRecyclerView.isAnimating())
                    { scrollHandler.postDelayed(this, checkInterval); }
                    else { weatherRecyclerView.smoothScrollToPosition(tilePosition); }
                }
            });
        }
    }

    /**
     * Calculates whether the tile that will be added will be visible on the screen
     * without the need for scrolling.
     *
     * @param glmWeather A grid layout manager extended with smooth scrolling.
     * @param tilePosition          An int detonating the position of the tile on the grid.
     * @param gridColumns           An int detonating hte amount of columns that the grid has.
     * @return boolean value whether the tile is visible or not.
     */
    private boolean isTileVisible(final GLMWeather glmWeather,
                                  int tilePosition, int gridColumns)
    {
        //Use the layout manager over the recyclerView to get the height of the tile
        //as the getChildAt method will sometimes return null.
        int firstItem = glmWeather.findFirstCompletelyVisibleItemPosition();
        int tileHeight = (firstItem >= 0) ?
                glmWeather.findViewByPosition(firstItem).getHeight() : 1;

        //Get the max # of tiles that are possible on the screen to check against
        //the tile position. Meaning if the tile position is bigger then we must scroll
        //down.
        int maxNumberOfTiles = ((Util.totalRVHeight / tileHeight) * gridColumns) - 1;

        return (firstItem <= tilePosition && maxNumberOfTiles >= tilePosition);
    }

    /**
     * Update a tile(s) with current information.
     *
     * This could be done without nested for loops using saved position.
     * Saved position cannot be guaranteed to be accurate at the time this executes.
     * Using cityId to compare objects is guaranteed as it is 100% non changing.
     *
     * isUserMovingTile needs to be false to update the tiles. If the tiles are
     * simply updated when the user is moving them it will get reset to its nearest
     * position. While not a problem for the update, its bad for the user experience.
     * This fixes that by checking the variable and if the user is moving
     * the tile it waits one second and then tries again.
     *
     * If the list was bigger. This could be split up in chunks and search in parallel.
     * Using a Collection.synchronizedList().
     *
     * @param tilesForUpdate a list of tile objects with weather information.
     */
    public void updateTiles(List<Tile> tilesForUpdate, @SortType int sortType)
    {
        final Handler updateTilesHandler = new Handler();
        updateTilesHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if(isUserMovingTile) { updateTilesHandler.postDelayed(this, checkInterval); }
                else
                {
                    updateTilesHandler.post(() ->
                    {
                        for(int i = 0; i < tilesForUpdate.size(); i++)
                        {
                            for(int j = 0; j < tileList.size(); j++)
                            {
                                if(tilesForUpdate.get(i).equals(tileList.get(j)))
                                {
                                    tileList.set(j, tilesForUpdate.get(i));
                                    break;
                                }
                            }
                        }

                        SortTiles.sort(sortType, tileList);
                        notifyDataSetChanged(); //Don't animate redraw
                        onTileAction.onTileUpdateComplete(tilesForUpdate.size());
                    });
                }
            }
        });
    }

    /**
     * Show temp values in Celsius or Fahrenheit depending on user preference.
     * @param tempScaleType boolean value denoting whether the value is Celsius
     *                      or Fahrenheit.
     */
    public void updateDegreeType(@TempScale int tempScaleType)
    {
        this.tempScale = tempScaleType;
        //notifyItemRangeChanged(0, tileList.size()); animates the change
        //and it looks worse in my opinion than no animation.
        notifyDataSetChanged();
    }

    /**
     * Gets the list item position every time a tile is moved.
     */
    public long[] getTilesPosition()
    {
        long[] citiesId = new long[tileList.size()];
        for(int i = 0; i < tileList.size(); i++)
        {
            citiesId[i] = tileList.get(i).getCityId();
        }
        return citiesId;
    }

    /**
     * Gets the correct tile object base on the position of the click
     * and starts the weather details activity with the correct information.
     * @return an onClick listener for the TileDetailsHolder.
     */
    private TileHolder.IOnTileHolderClick setTileHolderClick()
    {
        return (position, view) ->
        {
            Intent intent = new Intent(activity, WeatherDetailsActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("totalTiles", tileList.size());
            intent.putExtra("tempScale", tempScale);
            activity.startActivity(intent);
        };
    }

    static class TileHolder extends RecyclerView.ViewHolder implements
            IDragDropSwipeHelper.ViewHolder, View.OnClickListener
    {
        @BindView(R.id.tileView) WeatherView tileView;
        private IOnTileHolderClick onTileHolderClick;

        TileHolder(View itemView, IOnTileHolderClick onTileHolderClick)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.onTileHolderClick = onTileHolderClick;
            //Setting clickable here allows for the ripple effect to show.
            //If done on the layout (xml) it consumes the click and cannot be used.
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        void clearAnimation() { itemView.clearAnimation(); }

        @Override
        public void onClick(View view)
        {
            onTileHolderClick.onItemClick(getAdapterPosition(), view);
        }

        @Override
        public void onItemSelected()
        {
            itemView.setSelected(true);
        }

        @Override
        public void onItemActivated()
        {
            itemView.setActivated(true);
        }

        @Override
        public void onItemClear()
        {
            itemView.setSelected(false);
            itemView.setActivated(false);
        }

        interface IOnTileHolderClick { void onItemClick(int position, View view); }
    }
}