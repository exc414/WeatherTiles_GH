package io.bluephoenix.weathertiles.app;

import okhttp3.OkHttpClient;

/**
 * @author Carlos A. Perez
 */
public class UtilHttpClient
{
    public static OkHttpClient.Builder getHttpClient()
    { return new OkHttpClient.Builder(); }
}