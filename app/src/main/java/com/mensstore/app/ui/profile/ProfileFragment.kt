package com.mensstore.app.ui.profile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mensstore.app.R
import com.mensstore.app.data.repositories.UserRepository
import com.mensstore.app.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(
            requireActivity().application,
            UserRepository() // Initialize with your actual repository implementation
        )
    }
    
    private var googleMap: GoogleMap? = null
    private var locationPermissionGranted = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMap()
        setupUI()
        observeViewModel()
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupUI() {
        binding.apply {
            editProfileButton.setOnClickListener {
                // Navigate to edit profile screen
                // TODO: Implement navigation
            }

            logoutButton.setOnClickListener {
                viewModel.logout()
            }

            // Add click listener for the map to update delivery location
            googleMap?.setOnMapClickListener { latLng ->
                viewModel.updateUserLocation(latLng)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.apply {
                userName.text = "${user.firstName} ${user.lastName}"
                userEmail.text = user.email
                addressText.text = user.addresses.firstOrNull()?.let { address ->
                    "${address.streetAddress}\n${address.city}, ${address.state} ${address.postalCode}"
                }
            }
        }

        viewModel.userLocation.observe(viewLifecycleOwner) { location ->
            updateMapLocation(location)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.apply {
                // Show/hide loading indicator
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                // Disable/enable interaction while loading
                editProfileButton.isEnabled = !isLoading
                logoutButton.isEnabled = !isLoading
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        enableMyLocation()
        
        // Set up map UI settings
        googleMap?.apply {
            uiSettings.apply {
                isZoomControlsEnabled = true
                isCompassEnabled = true
                isMyLocationButtonEnabled = true
            }
        }
        
        viewModel.getCurrentLocation()
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
            googleMap?.isMyLocationEnabled = true
            viewModel.getCurrentLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            // Show an explanation to the user
            Toast.makeText(
                requireContext(),
                "Location permission is required to show your delivery address on the map",
                Toast.LENGTH_LONG
            ).show()
        }

        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun updateMapLocation(location: LatLng) {
        googleMap?.apply {
            clear()
            addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Delivery Location")
            )
            animateCamera(
                CameraUpdateFactory.newLatLngZoom(location, 15f)
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                    enableMyLocation()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Location permission denied. Some features may be limited.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
