package com.wotransfer.identify.manager

import com.wotransfer.identify.view.util.EnumType


open class BaseManager {
    companion object {
        var statusMap = HashMap<EnumType, Boolean>()
    }
    init {
        //默认只做证件认证
        statusMap(EnumType.CARD, false)
        statusMap(EnumType.FACE, false)
    }
}

operator fun <K, V> HashMap<K, V>.invoke(k: K, v: V) {
    BaseManager.statusMap[k as EnumType] = v as Boolean
}