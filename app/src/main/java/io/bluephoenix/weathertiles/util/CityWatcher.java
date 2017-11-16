package io.bluephoenix.weathertiles.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public abstract class CityWatcher implements TextWatcher
{
    private final TextView txtCitySearch;

    protected CityWatcher(TextView txtCitySearch)
    {
        this.txtCitySearch = txtCitySearch;
    }

    protected abstract void lookup(TextView txtSearch, String searchTerm);

    @Override
    public void beforeTextChanged(CharSequence source, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence source, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable source)
    {
        String citySearchString = txtCitySearch.getText().toString();
        lookup(txtCitySearch, citySearchString);
    }
}
