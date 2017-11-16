package io.bluephoenix.weathertiles.util;

import java.util.Collections;
import java.util.List;

import io.bluephoenix.weathertiles.core.common.SortDef;
import io.bluephoenix.weathertiles.core.common.SortDef.SortType;
import io.bluephoenix.weathertiles.core.data.model.db.Tile;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class SortTiles
{
    /**
     * @param sortType an int which defines the sort type.
     */
    public static void sort(@SortType int sortType, List<Tile> tileList)
    {
        switch(sortType)
        {
            case SortDef.NOSORT: break;

            case SortDef.TEMP_ASCENDING:
                sortAscending(tileList);
                break;

            case SortDef.TEMP_DESCENDING:
                sortDescending(tileList);
                break;

            case SortDef.DAYTIME:
                sortDaytime(tileList);
                break;

            case SortDef.NIGHTTIME:
                sortNighttime(tileList);
                break;

            case SortDef.ALPHABETICALLY_ASCENDING:
                sortAlphabeticallyAscending(tileList);
                break;

            case SortDef.ALPHABETICALLY_DESCENDING:
                sortAlphabeticallyDescending(tileList);
                break;

            default: break;
        }
    }

    private static void sortAscending(List<Tile> tiles)
    {
        Collections.sort(tiles, (tile, tileToCompare) ->
                tile.getTempCelsius() - tileToCompare.getTempCelsius());
    }

    private static void sortDescending(List<Tile> tiles)
    {
        Collections.sort(tiles, (tile, tileToCompare) ->
                tileToCompare.getTempCelsius() - tile.getTempCelsius());
    }

    private static void sortDaytime(List<Tile> tiles)
    {
        Collections.sort(tiles, (tile, tileToCompare) ->
        {
            int daytime = tile.getDayTime() ? 1 : 0;
            int nighttime = tileToCompare.getDayTime() ? 1 : 0;
            return daytime > nighttime ? -1 : daytime < nighttime ? 1 : 0;
        });
    }

    private static void sortNighttime(List<Tile> tiles)
    {
        Collections.sort(tiles, (tile, tileToCompare) ->
        {
            int daytime = tile.getDayTime() ? 1 : 0;
            int nighttime = tileToCompare.getDayTime() ? 1 : 0;
            return daytime < nighttime ? -1 : daytime > nighttime ? 1 : 0;
        });
    }

    private static void sortAlphabeticallyAscending(List<Tile> tiles)
    {
        Collections.sort(tiles, (tile, tileToCompare) ->
                tile.getCity().compareToIgnoreCase(tileToCompare.getCity()));
    }

    private static void sortAlphabeticallyDescending(List<Tile> tiles)
    {
        Collections.sort(tiles, (tile, tileToCompare) ->
                tileToCompare.getCity().compareToIgnoreCase(tile.getCity()));
    }
}
