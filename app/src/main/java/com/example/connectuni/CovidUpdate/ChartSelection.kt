package com.example.connectuni.CovidUpdate

enum class ChartSelection {
    POSITIVE, DEATH, RECOVERED, ACTIVECASE
}

enum class TimeScale(val daycount : Int){
    WEEK(7),
    MONTH(30),
    MAX(-1)

}