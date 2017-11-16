package io.bluephoenix.weathertiles.core.data.repository;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.bluephoenix.weathertiles.core.data.model.bus.Events;
import io.bluephoenix.weathertiles.core.data.model.db.Cities;
import io.bluephoenix.weathertiles.core.data.model.db.Tile;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.Sort;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class SearchRepository implements IRepository.Search
{
    private static final String CITY_ID_FIELD = "cityId";
    private static final String CITY_FIELD = "city";
    private static final String SORT_WEIGHT = "sortWeight";
    private Realm realmInstance = null;
    private List<Cities> suggestions;
    private List<Cities> results;
    private final int TOTAL_SUGGESTIONS_RETURNED = 30;

    @Override
    public void getSuggestions(String userInput)
    {
        try
        {
            //Make sure to copy or else it will complain that you are trying
            //to use the realm instance in an unauthorized/wrong thread.
            //Most likely has something to do with EventBus.
            realmInstance = Realm.getDefaultInstance();
            realmInstance.executeTransactionAsync(realm ->
            {
                RealmQuery<Cities> generalQuery = realm.where(Cities.class);
                results = generalQuery.beginsWith(CITY_FIELD, userInput, Case.INSENSITIVE)
                        .findAllSorted(SORT_WEIGHT, Sort.DESCENDING);

                if(results.size() > TOTAL_SUGGESTIONS_RETURNED)
                {
                    suggestions = realm.copyFromRealm(
                            results.subList(0, TOTAL_SUGGESTIONS_RETURNED));
                }
                else { suggestions = realm.copyFromRealm(results); }

                postResult(suggestions);
            });
        }
        finally { if(realmInstance != null) { realmInstance.close(); } }
    }

    @Override
    public Boolean isCityIdDuplicated(long cityId)
    {
        Boolean isDup;
        try
        {
            realmInstance = Realm.getDefaultInstance();
            RealmQuery<Tile> query = realmInstance.where(Tile.class);
            Tile result = query.equalTo(CITY_ID_FIELD, cityId).findFirst();
            isDup =  result != null;
        }
        finally { if(realmInstance != null) { realmInstance.close(); } }

        return isDup;
    }

    private void postResult(List<Cities> trimmedSuggestions)
    {
        //Post the result
        Events.Search searchEvent = new Events().new Search();
        searchEvent.setSuggestions(trimmedSuggestions);
        EventBus.getDefault().post(searchEvent);
    }
}
