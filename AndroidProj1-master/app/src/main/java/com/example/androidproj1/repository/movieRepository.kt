package com.example.booster4apps.repository

import com.example.booster4apps.network.ApiServices
import com.example.booster4apps.network.RetrofitClient
import com.example.booster4apps.network.models.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object WeatherRepository {

    private val apiServices: ApiServices by lazy {
        RetrofitClient.getClient().create(ApiServices::class.java)
    }

    private const val apiKey = "539d84bc553bcca9ca9064ae49010d42"
    private var currentMovieName = "cairo"

    private lateinit var MovieData: MovieResponse

    fun requestMovieData(movieNameName: String = currentMovieNameName, callback: MovieCallback) {

        if (this::movieData.isInitialized && movieName == currentMovieNameName) {
            callback.onMovieReady(movieData)
            return
        }

        currentMovieName = movieName

        apiServices.getWeatherByCity(movieName, apiKey)
            .enqueue(object : Callback<MovieResponse> {
                override fun onResponse(
                    call: Call<MovieResponse>, response: Response<MovieResponse>
                ) {
                    println("OnResponseCalled")
                    if (response.isSuccessful) {
                        movieData = response.body()!!
                        callback.onMovieReady(movieData)
                    } else if (response.code() in 400..404) {
                        val msg = "The Movie that you are looking for is not found"
                        callback.onMovieLoadingError(msg)
                    }
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    t.printStackTrace()
                    val msg = "Error while getting weather data"
                    callback.onMovieLoadingError(msg)
                }
            })
    }

    interface MovieCallback {
        fun onMovieReady(weather: MovieResponse)
        fun onMovieLoadingError(errorMsg: String)
    }

}