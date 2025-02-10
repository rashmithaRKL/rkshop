package com.mensstore.app.data.repositories.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mensstore.app.data.models.Product
import com.mensstore.app.data.repositories.ProductRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.math.BigDecimal

class FirebaseProductRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ProductRepository {

    private val productsCollection = firestore.collection("products")

    override suspend fun getProducts(
        category: String?,
        query: String?,
        minPrice: BigDecimal?,
        maxPrice: BigDecimal?,
        sortBy: String?,
        limit: Int,
        offset: Int
    ): Flow<List<Product>> = callbackFlow {
        var queryRef = productsCollection

        // Apply filters
        category?.let { queryRef = queryRef.whereEqualTo("category", it) }
        minPrice?.let { queryRef = queryRef.whereGreaterThanOrEqualTo("price", it.toDouble()) }
        maxPrice?.let { queryRef = queryRef.whereLessThanOrEqualTo("price", it.toDouble()) }

        // Apply sorting
        when (sortBy) {
            "price_asc" -> queryRef = queryRef.orderBy("price", Query.Direction.ASCENDING)
            "price_desc" -> queryRef = queryRef.orderBy("price", Query.Direction.DESCENDING)
            "newest" -> queryRef = queryRef.orderBy("dateAdded", Query.Direction.DESCENDING)
            else -> queryRef = queryRef.orderBy("dateAdded", Query.Direction.DESCENDING)
        }

        // Apply pagination
        queryRef = queryRef.limit(limit.toLong()).startAfter(offset)

        val subscription = queryRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            snapshot?.let { querySnapshot ->
                val products = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)
                }
                trySend(products)
            }
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun getProductById(productId: String): Flow<Product?> = callbackFlow {
        val subscription = productsCollection.document(productId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val product = snapshot?.toObject(Product::class.java)
                trySend(product)
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun getFeaturedProducts(limit: Int): Flow<List<Product>> = callbackFlow {
        val subscription = productsCollection
            .whereEqualTo("featured", true)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val products = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Product::class.java)
                    }
                    trySend(products)
                }
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun getNewArrivals(limit: Int): Flow<List<Product>> = callbackFlow {
        val subscription = productsCollection
            .orderBy("dateAdded", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val products = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Product::class.java)
                    }
                    trySend(products)
                }
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun addProduct(product: Product): String {
        val docRef = productsCollection.document()
        val productWithId = product.copy(id = docRef.id)
        docRef.set(productWithId).await()
        return docRef.id
    }

    override suspend fun updateProduct(product: Product) {
        productsCollection.document(product.id).set(product).await()
    }

    override suspend fun deleteProduct(productId: String) {
        productsCollection.document(productId).delete().await()
    }

    override suspend fun updateProductStock(productId: String, newQuantity: Int) {
        productsCollection.document(productId)
            .update("stockQuantity", newQuantity)
            .await()
    }

    override suspend fun getCategories(): Flow<List<String>> = callbackFlow {
        val subscription = productsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val categories = querySnapshot.documents
                        .mapNotNull { it.getString("category") }
                        .distinct()
                    trySend(categories)
                }
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun getBrands(): Flow<List<String>> = callbackFlow {
        val subscription = productsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val brands = querySnapshot.documents
                        .mapNotNull { it.getString("brand") }
                        .distinct()
                    trySend(brands)
                }
            }

        awaitClose { subscription.remove() }
    }

    // Implementation of other interface methods...
    override suspend fun getRelatedProducts(productId: String, limit: Int): Flow<List<Product>> = callbackFlow {
        val product = productsCollection.document(productId).get().await()
            .toObject(Product::class.java)

        product?.let {
            val subscription = productsCollection
                .whereEqualTo("category", it.category)
                .whereNotEqualTo("id", productId)
                .limit(limit.toLong())
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    snapshot?.let { querySnapshot ->
                        val products = querySnapshot.documents.mapNotNull { doc ->
                            doc.toObject(Product::class.java)
                        }
                        trySend(products)
                    }
                }

            awaitClose { subscription.remove() }
        }
    }

    override suspend fun searchProducts(
        query: String,
        filters: Map<String, Any>,
        limit: Int,
        offset: Int
    ): Flow<List<Product>> = callbackFlow {
        var queryRef = productsCollection

        // Apply search query
        queryRef = queryRef.whereArrayContains("tags", query.toLowerCase())

        // Apply filters
        filters.forEach { (key, value) ->
            queryRef = queryRef.whereEqualTo(key, value)
        }

        // Apply pagination
        queryRef = queryRef.limit(limit.toLong()).startAfter(offset)

        val subscription = queryRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            snapshot?.let { querySnapshot ->
                val products = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)
                }
                trySend(products)
            }
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun getProductsByCategory(
        category: String,
        subCategory: String?,
        limit: Int,
        offset: Int
    ): Flow<List<Product>> = callbackFlow {
        var queryRef = productsCollection.whereEqualTo("category", category)

        subCategory?.let {
            queryRef = queryRef.whereEqualTo("subCategory", it)
        }

        queryRef = queryRef.limit(limit.toLong()).startAfter(offset)

        val subscription = queryRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            snapshot?.let { querySnapshot ->
                val products = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)
                }
                trySend(products)
            }
        }

        awaitClose { subscription.remove() }
    }

    override suspend fun getProductsByBrand(
        brand: String,
        limit: Int,
        offset: Int
    ): Flow<List<Product>> = callbackFlow {
        val queryRef = productsCollection
            .whereEqualTo("brand", brand)
            .limit(limit.toLong())
            .startAfter(offset)

        val subscription = queryRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            snapshot?.let { querySnapshot ->
                val products = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)
                }
                trySend(products)
            }
        }

        awaitClose { subscription.remove() }
    }
}
