package com.ogata_k.mobile.winp.domain.use_case

/**
 * 同期型のユースケースのインターフェース
 */
interface SyncUseCase<in Input, out Output> {
    fun call(input: Input): Output
}