package com.ogata_k.mobile.winp.domain.use_case

/**
 * 非同期型のユースケースのインターフェース
 */
interface AsyncUseCase<in Input, out Output> {
    suspend fun call(input: Input): Output
}