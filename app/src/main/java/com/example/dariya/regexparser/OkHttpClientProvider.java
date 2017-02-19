package com.example.dariya.regexparser;

/**
 * Created by Dariya on 18.02.2017.
 */
import okhttp3.OkHttpClient;

public interface OkHttpClientProvider {
    OkHttpClient getOkHttpClient();
}
