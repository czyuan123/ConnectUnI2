package com.example.connectuni.CovidUpdate

import com.google.gson.annotations.SerializedName
import java.util.*

data class CovidData(
    //define the mapping of Json key to the variable we going to map to
    @SerializedName("lastUpdatedAtApify")val lastUpdatedAtApify : Date,
    @SerializedName("testedPositive")val testedPositive : Int,
    @SerializedName("testedNegative")val testedNegative : Int,
    @SerializedName("activeCases")val activeCases : Int,
    @SerializedName("recovered")val recovered : Int,
    @SerializedName("inICU")val inICU : Int,
    @SerializedName("deceased")val deceased : Int
)