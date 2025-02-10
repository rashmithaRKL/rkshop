package com.mensstore.app.ui.profile

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.mensstore.app.data.models.User
import com.mensstore.app.data.repositories.UserRepository
import kotlinx.coroutines.launch
import java.util.Locale

class ProfileViewModel(
    application: Application,
    private val userRepository: UserRepository
) : AndroidViewModel(application) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _userLocation = MutableLiveData<LatLng>()
    val userLocation: LiveData<LatLng> = _userLocation

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val currentUser = userRepository.getCurrentUser()
                _user.value = currentUser
                
                // Get the location from the first address if available
                currentUser.addresses.firstOrNull()?.let { address ->
                    getLocationFromAddress("${address.streetAddress}, ${address.city}, ${address.state}, ${address.postalCode}")
                }
            } catch (e: Exception) {
                _error.value = "Failed to load profile: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun getLocationFromAddress(address: String) {
        try {
            val geocoder = Geocoder(getApplication(), Locale.getDefault())
            val addresses = geocoder.getFromLocationName(address, 1)
            
            if (!addresses.isNullOrEmpty()) {
                val location = addresses[0]
                _userLocation.value = LatLng(location.latitude, location.longitude)
            }
        } catch (e: Exception) {
            _error.value = "Failed to get location from address: ${e.message}"
        }
    }

    fun getCurrentLocation() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    _userLocation.value = LatLng(it.latitude, it.longitude)
                }
            }
        } catch (e: SecurityException) {
            _error.value = "Location permission not granted"
        }
    }

    fun updateUserLocation(latLng: LatLng) {
        viewModelScope.launch {
            try {
                _loading.value = true
                // Update user's default address with new coordinates
                // This would typically involve reverse geocoding to get the address from coordinates
                // and then updating the user's address in the repository
                val geocoder = Geocoder(getApplication(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    // Update user's address in repository
                    // This is a placeholder - implement according to your repository
                    // userRepository.updateAddress(address)
                }
                
                _userLocation.value = latLng
            } catch (e: Exception) {
                _error.value = "Failed to update location: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                _loading.value = true
                userRepository.logout()
                // Navigation should be handled by the Fragment
            } catch (e: Exception) {
                _error.value = "Failed to logout: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}
