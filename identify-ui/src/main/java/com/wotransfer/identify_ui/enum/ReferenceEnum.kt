package com.wotransfer.identify_ui.enum

enum class ReferenceEnum(s: String) {
    PASSPORT("护照"),
    DRIVER("驾照"),
    IDENTIFY("身份证"),
    VISA("签证");

    private var value = s

    fun value(): String {
        return value
    }
}