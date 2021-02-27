package com.example.connectuni.CovidUpdate

import android.graphics.RectF
import com.robinhood.spark.SparkAdapter

class CovidSparkAdapter(private val dailyData: List<CovidData>) : SparkAdapter(){

    var metric = ChartSelection.POSITIVE
    var daysAgo = TimeScale.MAX

    override fun getY(index: Int): Float {
        val choosedayData = dailyData[index]
        //check value of metric variable
        return when (metric){
            ChartSelection.POSITIVE -> choosedayData.testedPositive.toFloat()
            ChartSelection.DEATH -> choosedayData.deceased.toFloat()
            ChartSelection.RECOVERED -> choosedayData.recovered.toFloat()
            ChartSelection.ACTIVECASE -> choosedayData.activeCases.toFloat()

        }
    }

    override fun getItem(index: Int) = dailyData[index]

    override fun getCount() = dailyData.size

    override fun getDataBounds(): RectF {
        val bound = super.getDataBounds()
        if(daysAgo != TimeScale.MAX) {
            //count is from function get count ,chop out data except latest 7 days
            bound.left = count - daysAgo.daycount.toFloat()
        }
        return bound
    }


}
