package com.weatherapp.ui.fragments

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.weatherapp.R
import com.weatherapp.databinding.FragmentWeatherDashboardBinding
import com.weatherapp.db.entity.WeatherEntity
import com.weatherapp.services.GpsTracker
import com.weatherapp.ui.activity.MainActivity
import com.weatherapp.ui.adapter.WeatherAdapter
import com.weatherapp.utils.Constants
import com.weatherapp.utils.Status
import com.weatherapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.widget.Toast

import android.app.ListActivity

import androidx.recyclerview.widget.RecyclerView

import androidx.recyclerview.widget.ItemTouchHelper
import com.weatherapp.utils.CheckPermissions


@AndroidEntryPoint
class WeatherDashboardFragment : Fragment() {

    private val weatherViewModel: WeatherViewModel by activityViewModels()

    private lateinit var binding: FragmentWeatherDashboardBinding
    private lateinit var navController: NavController
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var searchView: SearchView

    private lateinit var weatherList: MutableList<WeatherEntity>
    private var isSearchPerform = false

    companion object {
        private val TAG = WeatherDashboardFragment::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWeatherDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        val saveItem = menu.findItem(R.id.action_save)
        val searchItem = menu.findItem(R.id.action_search)

        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
            searchView.queryHint = getString(R.string.search_message)
        }

        if (::searchView.isInitialized) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            val queryTextListener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    Log.i("$TAG onQueryTextChange", newText)
                    return true
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.i("$TAG onQueryTextSubmit", query)
                    isSearchPerform = true
                    performSearch(query)
                    return true
                }
            }

            searchView.setOnQueryTextListener(queryTextListener)
        }

        val navHostFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.myNavHostFragment) as NavHostFragment

        //hide the menu item for other fragments than weather dashboard
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.weatherDashboardFragment -> {
                    handleMenuItems(saveItem, searchItem, true)
                }
                else -> {
                    handleMenuItems(saveItem, searchItem, false)
                }
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            //on save menu item click
            R.id.action_save -> {
                if (!CheckPermissions.hasLocationPermissions(requireContext()) ||
                    (::weatherList.isInitialized && weatherList.isEmpty())
                ) {
                    val gpsTracker = GpsTracker(requireContext())
                    if (gpsTracker.canGetLocation()) {
                        Log.e(TAG, "Lat : ${gpsTracker.getLatitude()} Long : ${gpsTracker.getLongitude()}")
                        weatherViewModel.fetchWeatherDetails(
                            gpsTracker.getLatitude().toString(),
                            gpsTracker.getLongitude().toString()
                        )
                    }
                } else weatherViewModel.getAllWeatherList()
                false
            }
            else -> {
                false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWeatherAdapter()
        observerFinalWeatherLiveData()
        observeWeatherLiveData()
    }

    //handle menu items visibility
    private fun handleMenuItems(saveItem: MenuItem?, searchItem: MenuItem?, flag: Boolean) {
        saveItem?.isVisible = flag
        searchItem?.isVisible = flag
    }

    //set the adapter with recyclerview
    private fun setWeatherAdapter() {
        binding.rvWeather.apply {
            weatherAdapter = WeatherAdapter(::onWeatherSelected)
            adapter = weatherAdapter
            val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    //swipe to delete feature
    private val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            //Remove swiped item from list and notify the RecyclerView
            val position = viewHolder.absoluteAdapterPosition
            weatherViewModel.deleteWeather(weatherList[position])
            weatherList.remove(weatherList[position])
        }
    }

    //on user tap to weather list item
    private fun onWeatherSelected(position: Int) {
        if (!weatherList.isNullOrEmpty()) {
            //handling the arguments
            val bundle = bundleOf(Constants.WEATHER_ID to weatherList[position].weatherId)
            //navigate to weather details
            findNavController().navigate(R.id.action_myHomeFragment_to_mySecondFragment, bundle)
        }
    }

    //fetch weather list from database
    private fun observerFinalWeatherLiveData() {
        weatherViewModel.finalWeatherLiveData.observe(this, { weatherList ->
            weatherList?.let {
                if (it.isNotEmpty()) {
                    binding.rvWeather.visibility = View.VISIBLE
                    binding.tvEmptyStatement.visibility = View.GONE
                    this.weatherList = mutableListOf()
                    this.weatherList = it as MutableList
                    if (isSearchPerform) {
                        this.weatherList.reverse()
                        isSearchPerform = false
                    }
                    weatherAdapter.submitList(this.weatherList)
                } else {
                    binding.rvWeather.visibility = View.GONE
                    binding.tvEmptyStatement.visibility = View.VISIBLE
                }
            }
        })
    }

    //perform search by city name through API
    private fun performSearch(query: String) {
        weatherViewModel.searchForWeather(query)
    }

    private fun observeWeatherLiveData() {
        weatherViewModel.weatherLiveData.observe(this, {
            when (it.status) {
                Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })
    }

}