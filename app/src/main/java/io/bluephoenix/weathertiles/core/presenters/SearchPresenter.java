package io.bluephoenix.weathertiles.core.presenters;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.bluephoenix.weathertiles.core.data.model.bus.Events;
import io.bluephoenix.weathertiles.core.data.repository.SearchRepository;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class SearchPresenter extends BasePresenter<ISearchContract.IPublishToView>
        implements ISearchContract.IPresenter
{
    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private SearchRepository searchRepository;

    public SearchPresenter()
    {
        searchRepository = new SearchRepository();
    }

    /**
     * Passes user input to the database
     * @param userInput a string with the inputted user text.
     */
    @Override
    public void searchForSuggestions(String userInput)
    {
        searchRepository.getSuggestions(userInput);
    }

    /**
     * Check to see if the city chosen by the user exists in the list already.
     * @param cityId a long containing an API city id.
     * @return a future object containing a boolean.
     */
    @Override
    public Future<Boolean> isCityIdDuplicated(long cityId)
    {
        return executorService.submit(() ->
        {
            return searchRepository.isCityIdDuplicated(cityId);
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void submitSearchSuggestion(Events.Search searchEvent)
    {
        publishToView.suggestedCities(searchEvent.getSuggestions());
    }
}
