package com.ogata_k.mobile.winp.presentation.enumerate

/**
 * アクションを実行できるかどうかを管理するEnum
 */
enum class LaunchActionState {
    CANNOT_LAUNCH,
    CAN_LAUNCH,
    DOING;

    fun canNotLaunch(): Boolean {
        return this == CANNOT_LAUNCH || isInDoingAction()
    }

    fun canLaunch(): Boolean {
        return this == CAN_LAUNCH
    }

    fun isInDoingAction(): Boolean {
        return this == DOING
    }
}