package com.ogata_k.mobile.winp.presentation.event.work

import com.ogata_k.mobile.winp.presentation.event.Event

data class FailedUpdateWorkTodo(val workId: Int, val workTodoId: Int?) : Event
