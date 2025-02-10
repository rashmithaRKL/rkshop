package com.mensstore.app.data.models

import java.math.BigDecimal

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<OrderItem> = listOf(),
    val status: OrderStatus = OrderStatus.PENDING,
    val shippingAddress: Address = Address(),
    val billingAddress: Address = Address(),
    val paymentMethod: PaymentMethod = PaymentMethod(),
    val subtotal: BigDecimal = BigDecimal.ZERO,
    val shippingCost: BigDecimal = BigDecimal.ZERO,
    val tax: BigDecimal = BigDecimal.ZERO,
    val total: BigDecimal = BigDecimal.ZERO,
    val dateCreated: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis(),
    val trackingNumber: String = "",
    val estimatedDeliveryDate: Long? = null,
    val notes: String = ""
)

data class OrderItem(
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val price: BigDecimal = BigDecimal.ZERO,
    val selectedSize: String = "",
    val selectedColor: String = "",
    val discountPercentage: Int = 0
) {
    val subtotal: BigDecimal
        get() = if (discountPercentage > 0) {
            price.multiply(BigDecimal.ONE.subtract(BigDecimal(discountPercentage).divide(BigDecimal(100))))
                .multiply(BigDecimal(quantity))
        } else {
            price.multiply(BigDecimal(quantity))
        }
}

data class PaymentMethod(
    val type: PaymentType = PaymentType.CREDIT_CARD,
    val lastFourDigits: String = "",
    val cardType: String = "",
    val expiryMonth: Int = 0,
    val expiryYear: Int = 0
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}

enum class PaymentType {
    CREDIT_CARD,
    DEBIT_CARD,
    PAYPAL,
    BANK_TRANSFER,
    CASH_ON_DELIVERY
}
