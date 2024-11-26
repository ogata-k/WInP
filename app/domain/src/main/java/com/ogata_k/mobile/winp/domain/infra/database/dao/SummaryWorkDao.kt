package com.ogata_k.mobile.winp.domain.infra.database.dao

import com.ogata_k.mobile.winp.domain.model.work.Summary
import java.time.OffsetDateTime

/**
 * タスクに関連したデータのサマリーのDAO
 */
interface SummaryWorkDao {
    /**
     * 指定した期間のサマリーデータを取得する
     */
    suspend fun getSummary(from: OffsetDateTime, to: OffsetDateTime): Summary
}