package com.example.webviewsample

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface FileDownloadService {

    @Streaming
    @GET
    fun downloadFileWithDynamicUrlAsync(@Url fileUrl: String): Call<ResponseBody>
    // Cookieもパラメータで渡せそう
    // https://stackoverflow.com/questions/38418809/add-cookies-to-retrofit-2-request
}
