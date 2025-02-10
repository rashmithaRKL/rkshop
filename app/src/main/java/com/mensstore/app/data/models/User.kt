package com.mensstore.app.data.models

data class User(
    val id: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val addresses: List<Address> = listOf(),
    val wishlist: List<String> = listOf(), // Product IDs
    val cart: List<CartItem> = listOf(),
    val orders: List<String> = listOf(), // Order IDs
    val dateJoined: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis()
)

data class Address(
    val id: String = "",
    val name: String = "", // e.g., "Home", "Office"
    val streetAddress: String = "",
    val city: String = "",
    val state: String = "",
    val postalCode: String = "",
    val country: String = "",
    val isDefault: Boolean = false
)

data class CartItem(
    val productId: String = "",
    val quantity: Int = 1,
    val selectedSize: String = "",
    val selectedColor: String = "",
    val dateAdded: Long = System.currentTimeMillis()
)
