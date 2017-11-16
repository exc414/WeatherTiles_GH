package io.bluephoenix.weathertiles.util;

import java.util.List;

import io.bluephoenix.weathertiles.core.common.Operator;
import io.bluephoenix.weathertiles.core.common.SortDef;
import io.bluephoenix.weathertiles.core.data.model.db.Tile;

/**
 * @author Carlos A. Perez Zubizarreta
 */
@SuppressWarnings("PointlessBooleanExpression")
public class PlaceTiles
{
    private static int lastPosition;
    private static final int NEEDS_PLACEMENT = -1;

    /**
     * Find the correct position for a tile to be placed in a sorted list.
     * @param sortType an int detonating the sort chosen by the user this way we
     *                 know what placement method to use.
     * @param tile     an object with weather data.
     * @param tileList a list of tile objects.
     * @return         an int detonating the position the tile needs to be place in the
     *                 list for it to be still sorted after insertion.
     */
    public static int place(@SortDef.SortType int sortType, Tile tile, List<Tile> tileList)
    {
        if(tileList.size() == 0) { return 0; }
        int start = 0;
        int end = tileList.size() - 1;
        int result;
        int daytime = 1;
        int nighttime = 0;
        lastPosition = tileList.size();

        switch(sortType)
        {
            case SortDef.NOSORT: return lastPosition;

            case SortDef.TEMP_ASCENDING:
                result = preCheckTemp(tile.getTempCelsius(), tileList,
                        start, end, Operator.LTE, Operator.GTE);

                return (result == NEEDS_PLACEMENT) ?
                        tilePlacement(tile.getTempCelsius(), tileList,
                        start, end, sortType, Operator.GTE, Operator.LT) : result;

            case SortDef.TEMP_DESCENDING:
                result = preCheckTemp(tile.getTempCelsius(), tileList,
                        start, end, Operator.GTE, Operator.LTE);

                return (result == NEEDS_PLACEMENT) ?
                        tilePlacement(tile.getTempCelsius(), tileList,
                        start, end, sortType, Operator.LTE, Operator.GT) : result;

            case SortDef.DAYTIME:
                return (tile.getDayTime() == false) ? lastPosition :
                        tilePlacement(daytime, tileList,  start, end, sortType,
                        Operator.EQ, Operator.NEQ);

            case SortDef.NIGHTTIME:
                return (tile.getDayTime() == true) ? lastPosition :
                        tilePlacement(nighttime, tileList, start, end, sortType,
                                Operator.EQ, Operator.NEQ);

            case SortDef.ALPHABETICALLY_ASCENDING:
                result = preCheckCityName(tile.getCity(), tileList, start, end,
                        Operator.LTE, Operator.GTE);

                return (result == NEEDS_PLACEMENT) ?
                        tilePlacement(tile.getCity(), tileList, start, end,
                                Operator.GTE, Operator.LT) : result;

            case SortDef.ALPHABETICALLY_DESCENDING:
                result = preCheckCityName(tile.getCity(), tileList, start, end,
                        Operator.GTE, Operator.LTE);

                return (result == NEEDS_PLACEMENT) ?
                        tilePlacement(tile.getCity(), tileList, start, end,
                                Operator.LTE, Operator.GT) : result;

            default: return lastPosition;
        }
    }

    /**
     * Returns an int detonating if the tile position should be at the start of the list,
     * the end or if tilePlacement() needs to be called to find the position. This is
     * done as a pre-check so as not to call tilePlacement() needlessly.
     *
     * @param temp  int with temperature data.
     * @param tl    a list of tile objects.
     * @param start int detonating the first position of the list.
     * @param end   int detonating the last position of the list.
     * @param op1   enum which contains relational operators. For this method these will be
     *              Less than or equal (<=) (Temp Ascending) and Greater than or equal (>=)
     *              (Temp Descending).
     * @param op2   Greater than or equal (>=) (Temp Ascending) and Less than or equal (<=)
     *              (Temp Descending).
     * @return      an int detonating if the tile position should be at the start of the list,
     *              the end or if further processing is required which returns -1.
     */
    private static int preCheckTemp(int temp, List<Tile> tl, int start, int end,
                                    Operator op1, Operator op2)
    {
        if(op1.apply(temp, tl.get(start).getTempCelsius())) { return start; }
        else if(op2.apply(temp, tl.get(end).getTempCelsius())) { return lastPosition; }
        else { return NEEDS_PLACEMENT; }
    }

    /**
     * Calculate the tile position on the list using binary search. The difference
     * here though is that unlike the Collections.binarySearch(). This one will put
     * the tile at the end of repeating tiles, something the collections one did not do.
     * This is achieved by the first if statement which compares the tile at the position
     * given and one position behind it which guarantees it will be last whether there
     * are duplicate tiles or not.
     *
     * @param value     an int value detonating what the tile's comparable value is.
     * @param tl        a list of tile objects.
     * @param start     int detonating the first position of the list.
     * @param end       int detonating the last position of the list.
     * @param sortType  an int detonating the sort chosen by the user.
     * @param op1       enum which contains relational operators. For this method these will be
     *                  Greater than or equal (>=) (Temp Ascending) and
     *                  Less than or equal (<=) (Temp Descending)
     * @param op2       Less than (<) (Temp Ascending) and Greater than (>) (Temp Descending)
     * @return          an int detonating the position of the tile in the list.
     */
    private static int tilePlacement(int value, List<Tile> tl, int start, int end,
                                     int sortType, Operator op1, Operator op2)
    {
        while(start <= end)
        {
            int mid = start + (end - start) / 2;

            if(op1.apply(value, tl.get(mid).getComparableField(sortType))
               && op2.apply(value, tl.get(mid + 1).getComparableField(sortType)))
            {
                return mid + 1;
            }
            else if(op2.apply(value, tl.get(mid).getComparableField(sortType)))
            {
                end = mid - 1;
            }
            else { start = mid + 1; }
        }
        return lastPosition;
    }

    /**
     * Returns an int detonating if the tile position should be at the start of the list,
     * the end or if tilePlacement() needs to be called to find the position. This is
     * done as a pre-check so as not to call tilePlacement() needlessly.
     *
     * @param value a string which contains the tile's city name.
     * @param tl    a list of tile objects.
     * @param start int detonating the first position of the list.
     * @param end   int detonating the last position of the list.
     * @param op1   enum which contains relational operators. For this method these will be
     *              Less than or equal (<) (TempAscending) and Greater than or equal (>)
     *              (Temp Descending).
     * @param op2   Greater than or equal (>) (TempAscending) and Less than or equal (<)
     *              (Temp Descending).
     * @return      an int detonating if the tile position should be at the start of the list,
     *              the end or if further processing is required which returns -1.
     */
    private static int preCheckCityName(String value, List<Tile> tl, int start, int end,
                                        Operator op1, Operator op2)
    {
        if(op1.apply(compare(value, tl.get(start).getCity()), 0)) { return start; }
        else if(op2.apply(compare(value, tl.get(end).getCity()), 0)) { return lastPosition; }
        else { return NEEDS_PLACEMENT; }
    }

    /**
     * @param value          a string value to compare.
     * @param valueToCompare a string value to be compare with the first string value.
     * @return               the value 0 if the argument is a string lexicographically
     *                       equal to this string; a value less than 0 if the argument
     *                       is a string lexicographically greater than this string; and a
     *                       value greater than 0 if the argument is a string lexicographically
     *                       less than this string.
     */
    private static int compare(String value, String valueToCompare)
    {
        return value.compareToIgnoreCase(valueToCompare);
    }

    /**
     * Calculate the tile position on the list using binary search. The difference
     * here though is that unlike the Collections.binarySearch(). This one will put
     * the tile at the end of repeating tiles, something the collections one did not do.
     * This is achieved by the first if statement which compares the tile at the position
     * given and one position behind it which guarantees it will be last whether there
     * are duplicate tiles or not.
     *
     * @param value     a string which contains the tile's city name.
     * @param tl        a list of tile objects.
     * @param start     int detonating the first position of the list.
     * @param end       int detonating the last position of the list.
     * @param op1       enum which contains relational operators. For this method these will be
     *                  Greater than or equal (>=) (Temp Ascending) and
     *                  Less than or equal (<=) (Temp Descending)
     * @param op2       Less than (<) (Temp Ascending) and Greater than (>) (Temp Descending)
     * @return          an int detonating the position of the tile in the list.
     */
    private static int tilePlacement(String value, List<Tile> tl,
                                     int start, int end, Operator op1, Operator op2)
    {
        while(start <= end)
        {
            int mid = start + (end - start) / 2;

            if(op1.apply(compare(value, tl.get(mid).getCity()), 0)
                    && op2.apply(compare(value, tl.get(mid + 1).getCity()), 0))
            {
                return mid + 1;
            }
            else if(op2.apply(compare(value, tl.get(mid).getCity()), 0))
            {
                end = mid - 1;
            }
            else { start = mid + 1; }
        }
        return lastPosition;
    }
}