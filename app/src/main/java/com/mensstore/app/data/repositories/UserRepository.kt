package com.mensstore.app.data.repositories

import com.mensstore.app.data.models.Address
import com.mensstore.app.data.models.CartItem
import com.mensstore.app.data.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // Authentication
    suspend fun signUp(email: String, password: String, firstName: String, lastName: String): String // Returns user ID
    suspend fun signIn(email: String, password: String): String // Returns user ID
    suspend fun signOut()
    suspend fun resetPassword(email: String)
    suspend fun getCurrentUser(): Flow<User?>
    suspend fun updateUserProfile(user: User)

    // Cart Operations
    suspend fun getCart(userId: String): Flow<List<CartItem>>
    suspend fun addToCart(userId: String, cartItem: CartItem)
    suspend fun updateCartItem(userId: String, cartItem: CartItem)
    suspend fun removeFromCart(userId: String, productId: String)
    suspend fun clearCart(userId: String)
    
    // Wishlist Operations
    suspend fun getWishlist(userId: String): Flow<List<String>>
    suspend fun addToWishlist(userId: String, productId: String)
    suspend fun removeFromWishlist(userId: String, productId: String)
    
    // Address Operations
    suspend fun getAddresses(userId: String): Flow<List<Address>>
    suspend fun addAddress(userId: String, address: Address)
    suspend fun updateAddress(userId: String, address: Address)
    suspend fun deleteAddress(userId: String, addressId: String)
    suspend fun setDefaultAddress(userId: String, addressId: String)
    
    // User Preferences
    suspend fun updateNotificationPreferences(userId: String, preferences: Map<String, Boolean>)
    suspend fun getNotificationPreferences(userId: String): Flow<Map<String, Boolean>>
    
    // Session Management
    suspend fun isUserLoggedIn(): Flow<Boolean>
    suspend fun getUserToken(): String?
    suspend fun refreshUserToken()
    
    // Account Operations
    suspend fun deleteAccount(userId: String)
    suspend fun updateEmail(userId: String, newEmail: String, password: String)
    suspend fun updatePassword(userId: String, currentPassword: String, newPassword: String)
    suspend fun verifyEmail(userId: String, verificationCode: String)
    
    // Social Authentication
    suspend fun signInWithGoogle(idToken: String): String // Returns user ID
    suspend fun signInWithFacebook(accessToken: String): String // Returns user ID
    
    // User Activity
    suspend fun updateLastLogin(userId: String)
    suspend fun getUserActivity(userId: String): Flow<List<UserActivity>>
}

data class UserActivity(
    val type: ActivityType,
    val timestamp: Long,
    val details: Map<String, Any>
)

enum class ActivityType {
    LOGIN,
    LOGOUT,
    PURCHASE,
    WISHLIST_UPDATE,
    CART_UPDATE,
    PROFILE_UPDATE,
    PASSWORD_CHANGE,
    ADDRESS_UPDATE
}
