package com.example.clonewasap2.api;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class SupabaseClient {
    public static final String SUPABASE_URL = "https://mnmfkxjqhmsuzouwdzay.supabase.co";
    public static final String SUPABASE_KEY = "sb_publishable_Iw36KJKaQ6zCTCWNYXgbPg_LOnA0lyE";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static OkHttpClient client;

    public static OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        okhttp3.Request original = chain.request();
                        okhttp3.Request request = original.newBuilder()
                                .header("apikey", SUPABASE_KEY)
                                .header("Authorization", "Bearer " + SUPABASE_KEY)
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    })
                    .build();
        }
        return client;
    }
}