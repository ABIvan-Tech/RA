package com.s0l.ra

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.s0l.ra.Constants.Companion.EXTRA_ADULT
import com.s0l.ra.Constants.Companion.EXTRA_CHILDREN
import com.s0l.ra.Constants.Companion.EXTRA_DATE
import com.s0l.ra.Constants.Companion.EXTRA_DESTINATION
import com.s0l.ra.Constants.Companion.EXTRA_DESTINATION_CODE
import com.s0l.ra.Constants.Companion.EXTRA_ORIGIN
import com.s0l.ra.Constants.Companion.EXTRA_ORIGIN_CODE
import com.s0l.ra.Constants.Companion.EXTRA_TEENS
import com.s0l.ra.domain.station.ResponseStation
import com.s0l.ra.domain.station.Station
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity(R.layout.activity_main) {
    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    //global value for UI and Network
    private val codes = hashMapOf<String, String>()

    //Search activity
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Spinner Drop down elements
        val number = arrayListOf("0", "1", "2", "3")
        val numberAdult = arrayListOf("1", "2", "3")

        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, number)
        val dataAdapterAdults =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, numberAdult)

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dataAdapterAdults.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        spinnerAdults.adapter = dataAdapterAdults
        spinnerTeens.adapter = dataAdapter
        spinnerChildren.adapter = dataAdapter

        //There are no validations for the input, so the flight search query will run as is
        buttonSearch.setOnClickListener {
            val adults = spinnerAdults.selectedItem.toString()
            val teens = spinnerTeens.selectedItem.toString()
            val children = spinnerChildren.selectedItem.toString()

            val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = dateFormat.format(calendarView.date)

            val origin = teOrigin.text
            val destination = teDestination.text

            val originCode = codes.filterValues { it == origin.toString() }
            val destinationCode = codes.filterValues { it == destination.toString() }

            val intent = Intent(this, SearchResultListActivity::class.java).apply {
                putExtra(EXTRA_ORIGIN, origin.toString())
                putExtra(
                    EXTRA_ORIGIN_CODE,
                    originCode.keys.toString().replace("[", "").replace("]", "")
                )
                putExtra(EXTRA_DESTINATION, destination.toString())
                putExtra(
                    EXTRA_DESTINATION_CODE,
                    destinationCode.keys.toString().replace("[", "").replace("]", "")
                )
                putExtra(EXTRA_DATE, date)
                putExtra(EXTRA_ADULT, adults.toInt())
                putExtra(EXTRA_TEENS, teens.toInt())
                putExtra(EXTRA_CHILDREN, children.toInt())

                //For test only
/*                putExtra(EXTRA_ORIGIN, "Dublin")
                putExtra(EXTRA_ORIGIN_CODE, "VCE")
                putExtra(EXTRA_DESTINATION, "London Stansted")
                putExtra(EXTRA_DESTINATION_CODE, "PMF")
                putExtra(EXTRA_DATE, "2020-07-28")
                putExtra(EXTRA_ADULT, 1)
                putExtra(EXTRA_TEENS, 2)
                putExtra(EXTRA_CHILDREN, 1)*/

            }
            startActivity(intent)
        }

        downloadData()
    }

    private fun downloadData() {

        GlobalScope.launch(Dispatchers.Main) {
            //Hide UI
            mainLayout.visibility = View.GONE
            progress.visibility = View.VISIBLE

            //Get JSON with stations list
            val responseStation: ResponseStation? = withContext(Dispatchers.IO) {
                try {
                    //Create Moshi for JSON parsing
                    val moshi = Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                    val jsonAdapter: JsonAdapter<ResponseStation> =
                        moshi.adapter<ResponseStation>(ResponseStation::class.java)

                    val stationsURL = "https://tripstest.ryanair.com/static/stations.json"

                    jsonAdapter.fromJson(URL(stationsURL).getText())
                } catch (e: Exception) {
                    // Error with network request
                    Log.e(TAG, "Exception " + e.printStackTrace())
                    null
                }
            }

            // Creating adapter
            responseStation?.stations?.forEach { station: Station ->
                //put all stations names and codes to hashmap
                station.let {
                    codes.put(it.code!!, it.name!!)
                }
            }

            //create adapter for AutoCompleteTextView
            val dataAdapter =
                ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_item,
                    codes.values.toList()
                )

            //set adapter
            teOrigin.setAdapter(dataAdapter)
            teDestination.setAdapter(dataAdapter)

            //Show UI
            mainLayout.visibility = View.VISIBLE
            progress.visibility = View.GONE
        }
    }

    private fun URL.getText(): String {
        return openConnection().run {
            this as HttpURLConnection
            inputStream.bufferedReader().readText()
        }
    }
}