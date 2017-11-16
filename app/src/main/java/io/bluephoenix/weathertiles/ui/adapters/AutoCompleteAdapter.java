package io.bluephoenix.weathertiles.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.bluephoenix.weathertiles.R;
import io.bluephoenix.weathertiles.core.data.model.db.Cities;

/**
 * @author Carlos A. Perez Zubizarreta
 */
public class AutoCompleteAdapter extends ArrayAdapter<Cities>
{
    private List<Cities> suggestions;
    private Cities cities;

    // View lookup cache
    private static class ViewHolder
    {
        TextView txtCity;
        TextView txtRegionCountry;
    }

    public AutoCompleteAdapter(Context context, List<Cities> suggestions)
    {
        super(context, R.layout.suggestion_row, suggestions);
        this.suggestions = suggestions;
    }

    @Override
    public int getCount()
    {
        return suggestions.size();
    }

    @Nullable
    @Override
    public Cities getItem(int position)
    {
        return this.suggestions.get(position);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        cities = suggestions.get(position);
        ViewHolder holder; //view cache

        if(convertView == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.suggestion_row, parent, false);

            holder.txtCity = (TextView) convertView.findViewById(R.id.txtCity);
            holder.txtRegionCountry = (TextView) convertView.findViewById(R.id.txtRegionCountry);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        if(cities.getRegion().equals("") || cities.getRegion() == null)
        {
            holder.txtCity.setText(cities.getCity());
            holder.txtRegionCountry.setText(cities.getCountry());
        }
        else
        {
            holder.txtCity.setText(cities.getCity());
            holder.txtRegionCountry.setText(cities.getRegion()
                    + ", " + cities.getCountry());
        }

        return convertView;
    }

    public void updateSuggestions(List<Cities> suggestions)
    {
        this.suggestions.clear();
        this.suggestions = suggestions;
        notifyDataSetChanged();
    }
}
