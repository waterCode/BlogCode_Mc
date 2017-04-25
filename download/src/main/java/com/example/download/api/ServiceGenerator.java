package com.example.download.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zmc on 2017/4/16.
 */
public class ServiceGenerator {
    public static final String API_BASE_URL="http://gdown.baidu.com/data/wisegame/e7b6b66762c29b2a/shoudiantong_720.apk/";

    private static Retrofit retrofit= new Retrofit.Builder()
                                            .baseUrl(API_BASE_URL).build();

    public static <S> S createService(Class<S> serviceClass){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);
        httpClient.connectTimeout(12, TimeUnit.SECONDS);
        OkHttpClient client = httpClient.build();
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }
}
