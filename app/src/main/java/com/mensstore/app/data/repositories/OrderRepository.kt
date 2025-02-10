package com.mensstore.app.data.repositories

import com.mensstore.app.data.models.Order
import com.mensstore.app.data.models.OrderStatus
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface OrderRepository {
    // Order Creation and Management
    suspend fun createOrder(order: Order): String // Returns order ID
    suspend fun getOrder(orderId: String): Flow<Order?>
    suspend fun getUserOrders(userId: String): Flow<List<Order>>
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus)
    suspend fun cancelOrder(orderId: String, reason: String)
    
    // Order Filtering and Search
    suspend fun getOrdersByStatus(
        userId: String,
        status: OrderStatus,
        limit: Int = 20,
        offset: Int = 0
    ): Flow<List<Order>>
    
    suspend fun searchOrders(
        userId: String,
        query: String,
        limit: Int = 20,
        offset: Int = 0
    ): Flow<List<Order>>
    
    suspend fun getOrdersByDateRange(
        userId: String,
        startDate: Long,
        endDate: Long,
        limit: Int = 20,
        offset: Int = 0
    ): Flow<List<Order>>
    
    // Order Analytics
    suspend fun getOrderAnalytics(
        userId: String,
        startDate: Long,
        endDate: Long
    ): Flow<OrderAnalytics>
    
    // Order Processing
    suspend fun processPayment(
        orderId: String,
        paymentDetails: Map<String, Any>
    ): PaymentResult
    
    suspend fun updateShippingInfo(
        orderId: String,
        trackingNumber: String,
        carrier: String,
        estimatedDeliveryDate: Long
    )
    
    // Order Notifications
    suspend fun sendOrderConfirmation(orderId: String)
    suspend fun sendShippingUpdate(orderId: String)
    suspend fun sendDeliveryConfirmation(orderId: String)
}

data class OrderAnalytics(
    val totalOrders: Int,
    val totalSpent: BigDecimal,
    val averageOrderValue: BigDecimal,
    val statusBreakdown: Map<OrderStatus, Int>,
    val mostPurchasedProducts: List<ProductOrderSummary>
)

data class ProductOrderSummary(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val totalRevenue: BigDecimal
)

data class PaymentResult(
    val success: Boolean,
    val transactionId: String?,
    val errorMessage: String?,
    val timestamp: Long = System.currentTimeMillis()
)
