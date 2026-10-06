package com.omsworld.familycare.ui.tracking

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.omsworld.familycare.R
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.databinding.MyTrackingFrBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyTrackingFragment : BaseFragment<MyTrackingFrBinding>(), OnMapReadyCallback {

    private val vm: MyTrackingViewModel by viewModels()
    private lateinit var adapter: FriendsTrackingAdapter
    private var map: GoogleMap? = null
    private var myMarker: Marker? = null

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        MyTrackingFrBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("MyTrackingFragment started")

        setupMap()
        setupRecyclerView()
        setupClickListeners()
        observeState()
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.Info_Map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun setupRecyclerView() {
        adapter = FriendsTrackingAdapter(
            onCallClick = { item -> dialNumber(item.mobile) },
            onNavigateClick = { item -> navigateTo(item) }
        )
        binding.rvTrackingFriends.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTrackingFriends.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.IVIamHere.setOnClickListener { centerOnMe() }
        binding.IVUpdown.setOnClickListener { toggleMapSize() }
        binding.RLMapZoomInout.setOnClickListener { toggleMapSize() }
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is TrackingUiState.Loading -> binding.mprogressBar.visibility = View.VISIBLE
                is TrackingUiState.Success -> {
                    binding.mprogressBar.visibility = View.GONE
                    adapter.submitList(state.friends)
                    drawFriendMarkers(state.friends)
                }
                is TrackingUiState.Error -> {
                    binding.mprogressBar.visibility = View.GONE
                    snack(state.message)
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.uiSettings?.isMapToolbarEnabled = false

        val lat = prefs.getString(requireContext(), Constants.JOB_NEW_LATITUDE, "0").toDoubleOrNull() ?: 0.0
        val lng = prefs.getString(requireContext(), Constants.JOB_NEW_LONGITUDE, "0").toDoubleOrNull() ?: 0.0

        if (lat != 0.0 && lng != 0.0) {
            val myPos = LatLng(lat, lng)
            myMarker = map?.addMarker(
                MarkerOptions().position(myPos).title("Me").visible(true)
            )
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17f))
        }
    }

    private fun centerOnMe() {
        val lat = prefs.getString(requireContext(), Constants.JOB_NEW_LATITUDE, "0").toDoubleOrNull() ?: return
        val lng = prefs.getString(requireContext(), Constants.JOB_NEW_LONGITUDE, "0").toDoubleOrNull() ?: return
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 17f))
        binding.TVUserTracking.text = "Currently Tracking My Location"
    }

    private fun drawFriendMarkers(friends: List<TrackingItem>) {
        map?.let { gm ->
            for (f in friends) {
                if (f.lat == 0.0 || f.lng == 0.0) continue
                gm.addMarker(
                    MarkerOptions()
                        .position(LatLng(f.lat, f.lng))
                        .title(f.name)
                        .snippet(f.address)
                        .visible(true)
                )
            }
        }
    }

    private fun dialNumber(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        startActivity(intent)
    }

    private fun navigateTo(item: TrackingItem) {
        val myLat = prefs.getString(requireContext(), Constants.JOB_NEW_LATITUDE, "0")
        val myLng = prefs.getString(requireContext(), Constants.JOB_NEW_LONGITUDE, "0")
        val uri = Uri.parse(
            "http://maps.google.com/maps?saddr=$myLat,$myLng&daddr=${item.lat},${item.lng}"
        )
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun toggleMapSize() {
        val fragment = childFragmentManager.findFragmentById(R.id.Info_Map) as? SupportMapFragment
        val view = fragment?.view ?: return
        val params = view.layoutParams
        params.height = if (params.height > 500) 370 else 900
        view.layoutParams = params
    }
}