package com.example.newzbreak.mvvm.retrofit_api

import com.example.newzbreak.common.API_KEY
import com.example.newzbreak.ui_layers.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country") countryCode :String ?= "in",
        @Query("page") pageNumber:Int ?= 1,
        @Query("apiKey") apiKey:String = API_KEY
    ) : Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q") searchQuery :String ,
        @Query("page") pageNumber:Int ?= 1,
        @Query("apiKey") apiKey:String = API_KEY
    ) : Response<NewsResponse>

}