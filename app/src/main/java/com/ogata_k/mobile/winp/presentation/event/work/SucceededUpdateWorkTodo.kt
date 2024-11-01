package com.ogata_k.mobile.winp.presentation.event.work

import com.ogata_k.mobile.winp.presentation.event.Event

data class SucceededUpdateWorkTodo(val workId: Long, val workTodoId: Long?) : Event
