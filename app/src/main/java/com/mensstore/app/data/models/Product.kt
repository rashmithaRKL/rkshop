    package com.mensstore.app.data.models

import java.math.BigDecimal

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: BigDecimal = BigDecimal.ZERO,
    val category: String = "",
    val subCategory: String = "",
    val images: List<String> = listOf(),
    val sizes: List<String> = listOf(),
    val colors: List<String> = listOf(),
    val brand: String = "",
    val inStock: Boolean = true,
    val stockQuantity: Int = 0,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val tags: List<String> = listOf(),
    val discountPercentage: Int = 0,
    val featured: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis()
) {
    val discountedPrice: BigDecimal
        get() = if (discountPercentage > 0) {
            price.multiply(BigDecimal.ONE.subtract(BigDecimal(discountPercentage).divide(BigDecimal(100))))
        } else {
            price
        }
}
