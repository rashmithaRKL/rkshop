package com.mensstore.app.data.repositories

import com.mensstore.app.data.models.Product
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface ProductRepository {
    suspend fun getProducts(
        category: String? = null,
        query: String? = null,
        minPrice: BigDecimal? = null,
        maxPrice: BigDecimal? = null,
        sortBy: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): Flow<List<Product>>

    suspend fun getProductById(productId: String): Flow<Product?>

    suspend fun getFeaturedProducts(limit: Int = 10): Flow<List<Product>>

    suspend fun getNewArrivals(limit: Int = 10): Flow<List<Product>>

    suspend fun getRelatedProducts(productId: String, limit: Int = 10): Flow<List<Product>>

    suspend fun searchProducts(
        query: String,
        filters: Map<String, Any> = emptyMap(),
        limit: Int = 20,
        offset: Int = 0
    ): Flow<List<Product>>

    suspend fun getProductsByCategory(
        category: String,
        subCategory: String? = null,
        limit: Int = 20,
        offset: Int = 0
    ): Flow<List<Product>>

    suspend fun getProductsByBrand(
        brand: String,
        limit: Int = 20,
        offset: Int = 0
    ): Flow<List<Product>>

    suspend fun updateProductStock(productId: String, newQuantity: Int)

    suspend fun addProduct(product: Product): String // Returns product ID

    suspend fun updateProduct(product: Product)

    suspend fun deleteProduct(productId: String)

    suspend fun getCategories(): Flow<List<String>>

    suspend fun getBrands(): Flow<List<String>>
}
