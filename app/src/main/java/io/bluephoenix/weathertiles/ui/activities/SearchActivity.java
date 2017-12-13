package io.bluephoenix.weathertiles.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.OnClick;
import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.app.App;
import io.bluephoenix.weathertiles.core.data.model.db.Cities;
import io.bluephoenix.weathertiles.core.presenters.ISearchContract;
import io.bluephoenix.weathertiles.core.presenters.SearchPresenter;
import io.bluephoenix.weathertiles.ui.adapters.AutoCompleteAdapter;
import io.bluephoenix.weathertiles.ui.views.CityAutoCompleteView;
import io.bluephoenix.weathertiles.util.CityWatcher;
import io.bluephoenix.weathertiles.util.Constant;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class SearchActivity extends BaseActivity implements
        ISearchContract.IPublishToView
{
    @BindView(R.id.btnBack) ImageButton btnBack;
    @BindView(R.id.searchCity) CityAutoCompleteView cityAutoCompleteView;
    private SearchPresenter presenter;
    private AutoCompleteAdapter autoCompleteAdapter;
    private Cities defaultSuggestions;
    private int numberOfChars = 1;
    private final long TIMEOUT_VALUE = 5000; //half the ARN which is 10 secs.
    private String searchTerm = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);

        cityAutoCompleteView.setOnItemClickListener(new OnClickListener());
        cityAutoCompleteView.setOnEditorActionListener(cityAutoCompleteView);
        getIgnoreOutsideTouchesReflection(true);

        defaultSuggestions = new Cities();
        defaultSuggestions.setCity("No Cities Found.");
        defaultSuggestions.setRegion("Double check your spelling");
        defaultSuggestions.setCountry("or try a nearby city.");

        List<Cities> suggestions = new ArrayList<>();
        suggestions.add(0, defaultSuggestions);

        autoCompleteAdapter = new AutoCompleteAdapter(getApplicationContext(),
                suggestions);
        cityAutoCompleteView.setAdapter(autoCompleteAdapter);

        cityAutoCompleteView.addTextChangedListener(new CityWatcher(cityAutoCompleteView)
        {
            @Override
            public void lookup(TextView txtSearch, String searchTerm)
            {
                //Make sure only valid characters make it to the database.
                //Controlling this at the input level did not work satisfactorily.
                if(searchTerm.length() > numberOfChars &&
                        searchTerm.matches("[a-zA-Z -]+"))
                {
                    SearchActivity.this.searchTerm = searchTerm;
                    presenter.searchForSuggestions(searchTerm);
                }
            }
        });

        attachPresenter();
    }

    private void attachPresenter()
    {
        presenter = (SearchPresenter) getLastCustomNonConfigurationInstance();
        if(presenter == null) { presenter = new SearchPresenter(); }
        presenter.attachView(this, Constant.REGISTER_BUS);
    }

    @Override
    protected void onPause()
    {
        hideSoftKeyBoard();
        super.onPause();
    }

    /**
     * Hide the keyboard once we leave this activity.
     */
    private void hideSoftKeyBoard()
    {
        InputMethodManager inputMethodManager = (InputMethodManager)
                getSystemService(INPUT_METHOD_SERVICE);
        //Verify if the soft keyboard is open and not null
        if(inputMethodManager != null && inputMethodManager.isAcceptingText())
        { inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance()
    {
        return presenter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putString("userInput", searchTerm);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        cityAutoCompleteView.setText(savedInstanceState.getString("userInput", ""));
    }

    @Override
    public void suggestedCities(List<Cities> suggestions)
    {
        //Show default suggestions if there are none returned.
        if(suggestions.size() == 0)
        {
            suggestions.add(0, defaultSuggestions);
        }
        autoCompleteAdapter.updateSuggestions(suggestions);
    }

    @Override
    protected void onDestroy()
    {
        presenter.detachView(Constant.DEREGISTER_BUS);
        super.onDestroy();
    }

    @OnClick(R.id.btnBack)
    public void onBackClickNoResults()
    {
        finish();
    }

    /*
     * Intent with the id of the selected city.
     * @param userInput a string containing the select city id.
     */
    private void sendBackResultOfSearch(int resultType, long cityId)
    {
        Intent result = new Intent();
        result.putExtra(Constant.SA_INTENT_KEY_SEARCH_RESULT, cityId);
        setResult(resultType, result);
        finish();
    }

    /**
     * OnItemClick get the item text and send it back to Weather Activity.
     */
    @SuppressWarnings("ConstantConditions")
    private class OnClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            try
            {
                long cityId = autoCompleteAdapter.getItem(position).getCityId();

                //Set the text field because if not then the getCityId object gets toString()
                //and put on as the text. While this is only for 1 second since then the
                //activity is dismissed, it should not happen.
                cityAutoCompleteView.setText(
                        autoCompleteAdapter.getItem(position).getCity());
                Future<Boolean> dupFuture = presenter.isCityIdDuplicated(cityId);
                Boolean isDuplicate = dupFuture.get(TIMEOUT_VALUE, TimeUnit.MILLISECONDS);

                //Check to make sure the tile has not been added already.
                //We block the thread until a value is given to us.
                if(isDuplicate)
                {
                    //Toast alerting the user that the city chosen is already added.
                    toastMaker(getString(R.string.search_city_exists));
                    cityAutoCompleteView.setText("");
                }
                else
                {
                    sendBackResultOfSearch(Activity.RESULT_OK, cityId);
                }
            }
            catch(NullPointerException | InterruptedException | ExecutionException ex)
            {
                //Set autocomplete text edit to nothing and go back with no results.
                cityAutoCompleteView.setText("");
                sendBackResultOfSearch(Constant.RESULT_NULL, -999);
            }
            catch(TimeoutException ex)
            {
                //Let the user know that there was an error and to try again.
                toastMaker(getString(R.string.search_city_error_retrieving));
                cityAutoCompleteView.setText("");
            }
        }
    }

    /**
     * This method uses a hidden (@hide) public method inside of AutoCompleteTextView.
     * This means that this is not guaranteed and could be changed at anytime.
     * What this does however could not be done through normal means as all
     * methods related to it are for use with a SearchDialog.
     * <p>
     * Problem to solve was that the dropdown would be dismissed when the
     * user clicked outside of it or when the user would tap on the edit box
     * itself after dismissing the keyboard. This was not wanted as the
     * dropdown should persist these events. The only way to get the
     * desired effect was using setForceIgnoreOutsideTouch() and setting
     * it to true.
     * <p>
     * Alternative way was to use the interface onDismiss() and use
     * showDropDown() method. This would re-show the dropdown but it
     * would go away fully then show up again, looked more like a bug.
     */
    private void getIgnoreOutsideTouchesReflection(boolean ignore)
    {
        try
        {
            Class<?> c = Class.forName("android.widget.AutoCompleteTextView");
            Object obj = cityAutoCompleteView;
            Method m = c.getDeclaredMethod("setForceIgnoreOutsideTouch", boolean.class);
            m.invoke(obj, ignore);
        }
        catch(ClassNotFoundException | NoSuchMethodException
                | InvocationTargetException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public void toastMaker(String input)
    {
        Toast.makeText(App.getInstance(), input, Toast.LENGTH_SHORT).show();
    }
}