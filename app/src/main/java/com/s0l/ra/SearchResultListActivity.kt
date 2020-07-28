package com.s0l.ra

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.s0l.ra.Constants.Companion.EXTRA_ADULT
import com.s0l.ra.Constants.Companion.EXTRA_CHILDREN
import com.s0l.ra.Constants.Companion.EXTRA_DATE
import com.s0l.ra.Constants.Companion.EXTRA_DESTINATION
import com.s0l.ra.Constants.Companion.EXTRA_DESTINATION_CODE
import com.s0l.ra.Constants.Companion.EXTRA_ORIGIN
import com.s0l.ra.Constants.Companion.EXTRA_ORIGIN_CODE
import com.s0l.ra.Constants.Companion.EXTRA_TEENS
import com.s0l.ra.domain.searchresult.Flight
import com.s0l.ra.domain.searchresult.SearchResult
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.activity_search_result_list.*
import kotlinx.android.synthetic.main.item_flight_brieef_info.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class SearchResultListActivity : AppCompatActivity(R.layout.activity_search_result_list),
    SeekBar.OnSeekBarChangeListener {
    companion object {
        val TAG = SearchResultListActivity::class.java.simpleName
    }

    private var searchResult: SearchResult? = null
    private var flights = mutableListOf<Flight>()

    private var url: String = ""

    // Reference to the RecyclerView adapter
    private lateinit var adapterOfResults: SearchResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the Intent that started this activity and extract the info
        val adults = intent.getIntExtra(EXTRA_ADULT, 1)
        val teens = intent.getIntExtra(EXTRA_TEENS, 0)
        val children = intent.getIntExtra(EXTRA_CHILDREN, 0)

        val date = intent.getStringExtra(EXTRA_DATE)

        val origin = intent.getStringExtra(EXTRA_ORIGIN)
        val originCode = intent.getStringExtra(EXTRA_ORIGIN_CODE)
        val destination = intent.getStringExtra(EXTRA_DESTINATION)
        val destinationCode = intent.getStringExtra(EXTRA_DESTINATION_CODE)

        title = "From: '$origin' To: '$destination'"

        //default values for seek bar
        seekBarPrice.min = 150
        seekBarPrice.max = 1000
        seekBarPrice.setOnSeekBarChangeListener(this)

        switchEnableFiltering.setOnCheckedChangeListener { _, isChecked ->
            if (flights.size > 0)
                seekBarPrice.isEnabled = isChecked

            //if filtering is enabled, show only the filtered list
            if (isChecked)
                setFilterToFlight(seekBarPrice.progress)
            else
                //if filtering is not enabled, show a list of all flights
                adapterOfResults.setDate(flights)
        }

        // Create RecyclerView adapter
        adapterOfResults = SearchResultAdapter("") { item: Flight -> itemClicked(item) }

        // set up RecyclerView
        searchResults.apply {
            scrollToPosition(0)
            setHasFixedSize(true)
            adapter = adapterOfResults
            visibility = View.GONE
        }

        //get final URL
        url = getUrl(date, originCode, destinationCode, adults, teens, children)

        //load flights data
        loadPartsAndUpdateList()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        setFilterToFlight(progress)
    }

    private fun setFilterToFlight(progress: Int) {
        //we get the filtered list of flights
        val flightsFiltered =
            flights.filter {
                it.regularFare?.fares?.get(0)?.amount != null
                        && it.regularFare?.fares?.get(0)?.amount!! >= progress
            }
        //update screen
        adapterOfResults.setDate(flightsFiltered)
    }

    //no need
    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    //no need
    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

//https://www.ryanair.com/api/booking/v4/en-gb/Availability?
// dateout=2020-12-30&
// roundtrip=false&
// origin=WRO&
// destination=DUB&
// flexdaysout=3&
// flexdaysin=3&
// flexdaysbeforeout=3&
// flexdaysbeforein=3&
// adt=1&
// chd=0&
// teen=0&
// inf=0&
// ToUs=AGREED&
// Disc=0&
// datein=2020-12-31

    // This is very simplified code
    private fun getUrl(
        dateout: String?,
        origin: String?,
        destination: String?,
        adt: Int,
        chd: Int,
        teen: Int
    ): String {
        val base = "https://www.ryanair.com/api/booking/v4/en-gb/Availability?"
        return base +
                "dateout=$dateout&" +
                "roundtrip=false&" +
                "origin=$origin&" +
                "destination=$destination&" +
                "flexdaysout=3&flexdaysin=3&flexdaysbeforeout=3&flexdaysbeforein=3&" +
                "adt=$adt&" +
                "chd=$chd&" +
                "teen=$teen&" +
                "inf=0&ToUs=AGREED&Disc=0&" +
                "datein=$dateout"
    }

    private fun loadPartsAndUpdateList() {
        // Launch Kotlin Coroutine on Android's main thread
        GlobalScope.launch(Dispatchers.Main) {

            //Get JSON with flight list
            searchResult = withContext(Dispatchers.IO) {
                try {
                    //Create Moshi for JSON parsing
                    val moshi = Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                    val jsonAdapter: JsonAdapter<SearchResult> =
                        moshi.adapter<SearchResult>(SearchResult::class.java)

                    jsonAdapter.fromJson(URL(url).readText())
                } catch (e: Exception) {
                    // Error with network request
                    Log.e(MainActivity.TAG, "Exception " + e.printStackTrace())
                    null
                }
            }

            if (searchResult != null) {
                // Set currency
                adapterOfResults.currency = searchResult?.currency

                searchResult?.trips?.get(0)?.dates?.forEach { it ->
                    it.flights?.forEach {
                        flights.add(it)
                    }
                }

                // Assign the list to the recycler view.
                adapterOfResults.setDate(flights)

                //Do some Magic with UI
                if (flights.size > 0) {
                    progress.visibility = View.GONE
                    searchResults.visibility = View.VISIBLE
                    linearLayout.visibility = View.VISIBLE
                } else {
                    linearLayout.visibility = View.GONE
                    progress.visibility = View.GONE
                    tvNoFound.visibility = View.VISIBLE
                }
            } else {
                progress.visibility = View.GONE
                tvNoFound.visibility = View.VISIBLE
            }
        }
    }

    private fun itemClicked(item: Flight) {
        val message =
            "From: " + searchResult?.trips?.get(0)?.originName + " (" + searchResult?.trips?.get(
                0
            )?.origin + ")\n" +
                    "To: " + searchResult?.trips?.get(0)?.destinationName + " (" + searchResult?.trips?.get(
                0
            )?.destination + ")\n" +
                    "Infants Left: " + item.infantsLeft + "\n" +
                    "Fare Class: " + item.regularFare?.fareClass + "\n" +
                    "Discount In Percent: " + item.regularFare?.fares?.get(0)?.discountInPercent + "\n"

        // Show additional info
        AlertDialog.Builder(this)
            .setTitle("Flight Summary:")
            .setMessage(message)
            .setPositiveButton(
                android.R.string.ok
            ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .setCancelable(true)
            .show()
    }

    class SearchResultAdapter(
        var currency: String?,
        private val clickListener: (Flight) -> Unit
    ) : RecyclerView.Adapter<SearchResultAdapter.SearchViewHolder>() {
        // SearchResult->List<Trip>->List<Date>
        private var flights = mutableListOf<Flight>()

        fun setDate(result: List<Flight>) {
            flights = result as MutableList<Flight>
            // Inform recycler view that data has changed.
            // Makes sure the view re-renders itself
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
            return SearchViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_flight_brieef_info, parent, false)
            )
        }

        override fun getItemCount() = flights.size

        override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
            holder.bind(flights[position], clickListener)
        }

        @SuppressLint("SetTextI18n")
        inner class SearchViewHolder(view: View) :
            RecyclerView.ViewHolder(view) {
            fun bind(item: Flight, clickListener: (Flight) -> Unit) {
                itemView.tvFlightDate.text = "Flight date: " + item.time?.get(0)
                itemView.tvFlightNumber.text = "Flight number: " + item.flightNumber
                itemView.tvDuration.text = "Duration: " + item.duration
                val price = item.regularFare?.fares?.get(0)?.amount
                // by default price is N/A (hardcoded)
                price?.let {
                    itemView.tvPrice.text =
                        "Regular fare price: " + price + " ($currency)"
                }

                itemView.setOnClickListener { clickListener(item) }
            }
        }
    }

}