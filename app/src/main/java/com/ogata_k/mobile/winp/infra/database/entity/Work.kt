package com.ogata_k.mobile.winp.infra.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(
    tableName = "works",
    indices = [
        Index(value = ["began_at", "ended_at"]),
        Index(value = ["completed_at"], orders = [Index.Order.DESC]),
    ],
)
data class Work(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "work_id")
    val workId: Long,

    @ColumnInfo
    val title: String,

    @ColumnInfo
    val description: String,

    @ColumnInfo(name = "began_at")
    val beganAt: OffsetDateTime?,

    @ColumnInfo(name = "ended_at")
    val endedAt: OffsetDateTime?,

    @ColumnInfo(name = "completed_at")
    val completedAt: OffsetDateTime?,

    @ColumnInfo(name = "created_at")
    val createdAt: OffsetDateTime,
)
