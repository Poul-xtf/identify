package com.wotransfer.identify.manager


open class BaseManager {
    var statusMap = HashMap<String, Boolean>()
}

var baseManager: BaseManager? = null

operator fun <K, V> HashMap<K, V>.invoke(k: K, v: V) {
    baseManager?.run {
        statusMap[k as String] = v as Boolean
    } ?: kotlin.run {
        baseManager = BaseManager()
    }
}