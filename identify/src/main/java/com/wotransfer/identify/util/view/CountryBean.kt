package com.wotransfer.identify.util.view

data class CountryBean(
    val code: Int,
    val model: List<Model>,
    val suc: Boolean
)

data class Model(
    val `data`: List<Data>,
    val index: String
)

data class Data(
    val countryName: String,
    val countryEnName: String,
    val countryCode: String
)