package com.eltafouk.masareefi.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType {
    INCOME,
    EXPENSE
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: TransactionType,
    val amount: Double,
    val category: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
