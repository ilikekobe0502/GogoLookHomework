package com.example.gogolookhomework.misc.provider

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

private var testCoroutineScope: CoroutineScope? = null
fun setTestCoroutineScope(coroutineScope: CoroutineScope?) {
    testCoroutineScope = coroutineScope
}
val TestCoroutineScope: CoroutineScope?
    get() = testCoroutineScope

object DispatcherProvider {
    private var testIODispatcher: CoroutineDispatcher? = null
    fun setTestIO(dispatcher: CoroutineDispatcher?) {
        testIODispatcher = dispatcher
    }
    val IO: CoroutineDispatcher
        get() = testIODispatcher ?: kotlinx.coroutines.Dispatchers.IO
    val Main: CoroutineDispatcher
        get() = kotlinx.coroutines.Dispatchers.Main
}

