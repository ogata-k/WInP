package com.ogata_k.mobile.winp.infra.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(
    tableName = "work_comments",
    indices = [
        Index(value = ["work_id", "created_at"]),
    ],
)
data class WorkComment(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "work_comment_id")
    val workCommentId: Long,

    @ColumnInfo(name = "work_id")
    val workId: Long,

    @ColumnInfo
    val comment: String,

    @ColumnInfo(name = "modified_at")
    val modifiedAt: OffsetDateTime?,

    @ColumnInfo(name = "created_at")
    val createdAt: OffsetDateTime,
)
