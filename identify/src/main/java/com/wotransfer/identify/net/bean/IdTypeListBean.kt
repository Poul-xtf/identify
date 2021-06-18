package com.wotransfer.identify.net.bean

import java.io.Serializable


data class IdTypeListBean(
    val code: Int,
    val model: Model,
    val suc: Boolean,
)

data class Model(
    val idConfigForSdkROList: List<IdConfigForSdkRO>,
    val reference: String,
)

data class IdConfigForSdkRO(
    val appName: String,
    val id: Int,
    val idConfigForSdkROList: List<IdConfigForSdkROX>,
    val idCountry: String,
    val countryCode: String,
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
    val subtitle: String,
) : Serializable

data class IdConfigForSdkROX(
    val appName: String,
    val id: Int,
    val idCountry: String,
    val countryCode: String,
    val idDesc: String,
    val idName: String,
    val idTag: String,
    val idType: String,
    val imageUrl: String,
    val needOcr: Int,
    val parentId: Int,
    val side: Int,
    val sort: Int,
    val subtitle: String,
    val borderUrl: String,
):Serializable