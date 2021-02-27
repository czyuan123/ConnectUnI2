package com.example.connectuni.CovidUpdate

import retrofit2.http.GET

//retroit talk to API
interface CovidService {
//create one function for each endpoint of the API
    //retrofit will take the JSON data returned from API and turn it into data model
    @GET("datasets/7Fdb90FMDLZir2ROo/items?format=json&clean=1")
    fun getMalaysiaData(): retrofit2.Call<List<CovidData>>
        //add return value
        //@GET("states/daily.json")
    //fun getStatesData(): retrofit2.Call<List<CovidData>>
}