package com.wotransfer.identify.net

import com.wotransfer.identify.R
import java.lang.NullPointerException


class HttpManager(httpClient: HttpClient, httpCallBackListener: HttpCallBackListener) {

    var httpClient: HttpClient? = httpClient
    var httpCallBackListener: HttpCallBackListener? = httpCallBackListener

    companion object {
        private var instance: HttpManager? = null
        fun getInstance(httpClient: HttpClient, httpCallBackListener: HttpCallBackListener): HttpManager? {
            synchronized(HttpManager::class) {
                if (instance == null) {
                    instance = HttpManager(httpClient, httpCallBackListener)
                }
            }
            return instance
        }
    }

    //开始请求
    fun startRequest(url: String?) {
        url?.let {
            httpClient?.executeRequest(url)?.also { content ->
                httpCallBackListener?.onSuccess(content)
            }
        } ?: let {
            throw NullPointerException("url is null")
        }
    }
}