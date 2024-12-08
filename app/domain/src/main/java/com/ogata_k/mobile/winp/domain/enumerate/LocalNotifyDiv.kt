package com.ogata_k.mobile.winp.domain.enumerate

/**
 * 通知タイミングを表す区分値
 */
enum class LocalNotifyDiv(val value: Int) {
    TODAY_EVERY_DAY(1),
    TOMORROW_EVERY_DAY(2);

    companion object {
        fun lookup(value: Int): LocalNotifyDiv {
            return entries.find { it.value == value } ?: throw IllegalArgumentException()
        }
    }
}