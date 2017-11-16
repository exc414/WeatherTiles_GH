package io.bluephoenix.weathertiles.core.data.model.bus;

import java.util.List;

import io.bluephoenix.weathertiles.core.common.EventDef.EventType;
import io.bluephoenix.weathertiles.core.data.model.db.Cities;
import io.bluephoenix.weathertiles.core.data.model.db.Tile;

/**
 * @author Carlos A. Perez Zubizarreta
 */

public class Events
{
    /**
     * Use to return weather information from the database to presenter using EventBus.
     */
    public class Weather
    {
        private int eventType;
        private List<Tile> tiles;
        private Tile tile;

        public Tile getTile() { return tile; }

        public void setTile(Tile tile) { this.tile = tile; }

        public @EventType int getEventType() { return eventType; }

        public void setEventType(@EventType int eventType)
        {
            this.eventType = eventType;
        }

        public List<Tile> getTiles() { return tiles; }

        public void setTiles(List<Tile> tiles) { this.tiles = tiles; }
    }

    public class Search
    {
        private List<Cities> suggestions;

        public List<Cities> getSuggestions()
        {
            return suggestions;
        }

        public void setSuggestions(List<Cities> suggestions)
        {
            this.suggestions = suggestions;
        }
    }

    /**
     * Use to return information from retrofit2 callback to CompletableFuture.
     * @param <V> An object of generic type.
     */
    public class APIResponse<V>
    {
        private boolean hasFailed;
        private V response;

        public boolean getHasFailed()
        {
            return hasFailed;
        }

        public void setHasFailed(boolean hasFailed)
        {
            this.hasFailed = hasFailed;
        }

        public V getResponse()
        {
            return response;
        }

        public void setResponse(V response)
        {
            this.response = response;
        }
    }
}
