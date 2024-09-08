package com.ogata_k.mobile.winp.common.model

sealed class PageData<Item> {
    class Succeeded<Item>(val items: List<Item>, val hasNextItem: Boolean) : PageData<Item>()
    class Failed<Item>(val exception: Throwable) : PageData<Item>()
}