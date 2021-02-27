package com.example.connectuni.CovidUpdate

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.connectuni.R
import com.google.gson.GsonBuilder
import com.robinhood.ticker.TickerUtils
import kotlinx.android.synthetic.main.covidupdate.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private const val BASE_URL = "https://api.apify.com/v2/"
class MalaysiaCovidUpdate : AppCompatActivity() {
    private lateinit var presentData: List<CovidData>
    private lateinit var adapter: CovidSparkAdapter
    private lateinit var stateDailyData: Map<String, List<CovidData>>
    private lateinit var malaysiaDailyData: List<CovidData>
    var totalpositivecase = arrayListOf<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.covidupdate)

        val gson = GsonBuilder().create()
        //read_json()
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()

        val covidService = retrofit.create(CovidService::class.java)
        //GET Malaysia Data
        covidService.getMalaysiaData().enqueue(object : Callback<List<CovidData>> {
            override fun onFailure(call: Call<List<CovidData>>, t: Throwable) {
                Log.e("TAG", "onFailure $t")
            }

            override fun onResponse(
                call: Call<List<CovidData>>,
                response: retrofit2.Response<List<CovidData>>
            ) {
                Log.i("TAG", "onResponse $response")
                val malaysiaData = response.body()
                if (malaysiaData == null) {
                    Log.w("TAG", "Invalid response body")
                    return
                }
                //only check on radio button after successfully retrieve the data
                checkOnEventListener()
                //To get latest data first so reversed
                malaysiaDailyData = malaysiaData
                Log.i("TAG", "Update graph with national data")
                updateDisplayData(malaysiaDailyData)

            }


        })
    }

    private fun checkOnEventListener() {
        //Add a listener for the user scrubbing on the chart, respond to radio button
        sparkview.isScrubEnabled = true
        //the value we get back is from the data element at the position
        sparkview.setScrubListener {itemDta ->
            if(itemDta is CovidData){
                updateInfoDaily(itemDta)
            }
        }
        tickerview.setCharacterLists(TickerUtils.provideNumberList())
        //Respond to radio button selection
        selecttime.setOnCheckedChangeListener { _, checkedId ->
            adapter.daysAgo = when (checkedId) {
                R.id.month -> TimeScale.MONTH
                R.id.week -> TimeScale.WEEK
                else -> TimeScale.MAX
            }
            //display metric for the most recent data
            updateInfoDaily(presentData.last())
            adapter.notifyDataSetChanged()
        }

        metricselection.setOnCheckedChangeListener {_, checkedId ->
            when(checkedId){
                //update display metric to positive case
                R.id.radioButtonPositiveCases -> updateDisplayMetric(ChartSelection.POSITIVE)
                R.id.radioButtonDeceased -> updateDisplayMetric(ChartSelection.DEATH)
                R.id.RadioButtonrecovered -> updateDisplayMetric(ChartSelection.RECOVERED)
                R.id.radioButtonActiveCases -> updateDisplayMetric(ChartSelection.ACTIVECASE)
            }
        }
    }

    private fun updateDisplayMetric(chartSelection: ChartSelection) {
        //Change colour of chart
        val colorRes = when(chartSelection){
            ChartSelection.RECOVERED -> R.color.recover
            ChartSelection.POSITIVE -> R.color.colourprimary
            ChartSelection.DEATH -> R.color.colourtitle
            ChartSelection.ACTIVECASE -> R.color.red
        }
        val colorInt = ContextCompat.getColor(this,colorRes)
        sparkview.lineColor = colorInt
        tickerview.textColor= colorInt
        //update metric in adapter
        adapter.metric = chartSelection
        adapter.notifyDataSetChanged()

        //update number shown in the bottom text view
        updateInfoDaily(presentData.last())
    }

    private fun updateDisplayData(dailyData: List<CovidData>) {
        presentData = dailyData
        //render data in sparkchart
        //create sparkadapter with the data
        adapter = CovidSparkAdapter(dailyData)
        sparkview.adapter = adapter
        //update radiobutton  to select positive and max time by default
        radioButtonPositiveCases.isChecked = true
        max.isChecked = true

        updateDisplayMetric(ChartSelection.POSITIVE)

    }

    private fun updateInfoDaily(covidData: CovidData) {
        val casesCount = when (adapter.metric){
            ChartSelection.RECOVERED -> covidData.recovered
            ChartSelection.DEATH -> covidData.deceased
            ChartSelection.POSITIVE -> covidData.testedPositive
            ChartSelection.ACTIVECASE -> covidData.activeCases
        }
        tickerview.text = NumberFormat.getInstance().format(casesCount)
        val dataformat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        datelabel.text = dataformat.format(covidData.lastUpdatedAtApify)
    }
}



    /* covidService.getStatesData().enqueue(object:Callback<List<CovidData>> {
        override fun onFailure(call: Call<List<CovidData>>, t: Throwable) {
            Log.e("TAG", "onFailure $t")
        }

        override fun onResponse(
            call: Call<List<CovidData>>,
            response: Response<List<CovidData>>
        ) {
            Log.i("TAG", "onResponse $response")
            val statesData = response.body()
            if (statesData == null) {
                Log.w("TAG", "Invalid response body")
                return
            }
            //To get latest data first so reversed
            stateDailyData = statesData.reversed().groupBy{it.states}
            Log.i("TAG", "Update spinner with state names")

        }
    })*/

    /*private fun read_json(){

        var json: Any? = null
        try {
            val inputStream: InputStream = assets.open("malaysia/daily.json")
            //READ the json file
            json = inputStream.bufferedReader().use { it.readText() }

            var jsonArray = JSONArray(json)
            for(i in 0 until jsonArray.length()-1)
            {
                var jsonObject = jsonArray.getJSONObject(i)
                dailyactivecases.add("Daily Cases : " + jsonObject.getString("activeCases"))
                deceased.add("Total Deadth : " + jsonObject.getString("deceased"))
                recovered.add("Total Recovered : " + jsonObject.getString("recovered"))
                totalpositivecase.add("Total Positive Cases : " + jsonObject.getString("testedPositive"))
                //totalnegativecase.add("Total Positive Cases : " + jsonObject.getString("testedNegative"))
            }
            var adapt = ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,dailyactivecases)
           // json_list.adapter = adapt

            json_list.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                Toast.makeText(
                    applicationContext,
                    "Total Positive cases" + totalpositivecase[position],
                    Toast.LENGTH_LONG
                ).show()
            }

        }catch (e : IOException){}
    }*/


