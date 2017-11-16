package io.bluephoenix.weathertiles.core.presenters;

import java.util.List;
import java.util.concurrent.Future;

import io.bluephoenix.weathertiles.core.data.model.db.Cities;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public interface ISearchContract
{
    interface IPublishToView
    {
        /**
         * Sets the suggestions.
         * @param suggestions a list of cities objects.
         */
        void suggestedCities(List<Cities> suggestions);
    }

    interface IPresenter
    {
        /**
         * Passes user input to the database
         * @param userInput a string with the inputted user text.
         */
        void searchForSuggestions(String userInput);

        /**
         * Check to see if the city chosen by the user exists in the list already.
         * @param cityId a long containing an API city id.
         * @return a future object containing a boolean.
         */
        Future<Boolean> isCityIdDuplicated(long cityId);
    }
}
