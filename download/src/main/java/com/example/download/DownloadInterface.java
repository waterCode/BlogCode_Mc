package com.example.download;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by zmc on 2017/4/15.
 */
public interface DownloadInterface {
    @Streaming
    @GET
    Call<ResponseBody> getFileResult(@Url String url, @Header("Range")String contentRange);

    @Streaming
    @GET
    Call<ResponseBody> getFileResult(@Url String url);
}
