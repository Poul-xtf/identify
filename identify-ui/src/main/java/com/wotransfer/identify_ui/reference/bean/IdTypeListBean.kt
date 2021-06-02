package com.wotransfer.identify_ui.reference.bean

data class IdTypeListBean(
    val code: Int,
    val model: Model,
    val suc: Boolean
)

data class Model(
    val idConfigForSdkROList: List<IdConfigForSdkRO>,
    val reference: String
)

data class IdConfigForSdkRO(
    val appName: String,
    val id: Int,
    val idConfigForSdkROList: List<IdConfigForSdkROX>,
    val idCountry: String,
    val idDesc: String,
    val idName: String,
    val idTag: String,
    val idType: String,
    val imageUrl: String,
    val needFace: Int,
    val needOcr: Int,
    val parentId: Int,
    val side: Int,
    val sort: Int,
    val subtitle: String
)

data class IdConfigForSdkROX(
    val appName: String,
    val id: Int,
    val idCountry: String,
    val idDesc: String,
    val idName: String,
    val idTag: String,
    val idType: String,
    val imageUrl: String,
    val needOcr: Int,
    val parentId: Int,
    val side: Int,
    val sort: Int,
    val subtitle: String
)