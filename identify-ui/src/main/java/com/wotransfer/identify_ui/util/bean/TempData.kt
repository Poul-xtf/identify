package com.wotransfer.identify_ui.util.bean

import java.util.*

class TempData {
    val NZD_CURRENCY_CODE = "NZD"
    val AUD_CURRENCY_CODE = "AUD"
    val JPY_CURRENCY_CODE = "JPY"
    val IDR_CURRENCY_CODE = "IDR"

    companion object {

        fun getAreaList(showCountryCode: Boolean): List<CellAreaModel> {
            val cellAreaModels: MutableList<CellAreaModel> = ArrayList<CellAreaModel>()
            var cellAreaModel = CellAreaModel()
            cellAreaModel.name = "澳大利亚"

            cellAreaModels.add(cellAreaModel)
            cellAreaModel = CellAreaModel()
            cellAreaModel.name = "新西兰"

            cellAreaModels.add(cellAreaModel)
            cellAreaModel = CellAreaModel()
            cellAreaModel.name = "日本"

            cellAreaModels.add(cellAreaModel)
            cellAreaModel = CellAreaModel()
            cellAreaModel.name = "印度尼西亚"

            cellAreaModels.add(cellAreaModel)
            if (!showCountryCode) {
                cellAreaModel = CellAreaModel()
                cellAreaModel.name = "中国"

                cellAreaModels.add(cellAreaModel)
            }
            return cellAreaModels
        }

        fun getAreaModelByCode(code: String?): CellAreaModel? {
            for (model in getAreaList(false)) {
                return model
            }
            return null
        }

    }


}