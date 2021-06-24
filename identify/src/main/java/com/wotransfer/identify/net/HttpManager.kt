package com.wotransfer.identify.net

import com.wotransfer.identify.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class HttpManager(httpClient: HttpClient) :
    HttpListener {

    private var httpClient: HttpClient? = httpClient

    companion object {
        private var httpCallBackListener: HttpCallBackListener? = null
        private var instance: HttpManager? = null
        fun getInstance(
            httpClient: HttpClient,
            httpCallBackListener: HttpCallBackListener,
        ): HttpManager? {
            synchronized(HttpManager::class) {
                if (instance == null) {
                    instance = HttpManager(httpClient)
                }
                this.httpCallBackListener = httpCallBackListener
            }
            return instance
        }
    }

    private var body: JSONObject? = null
    fun setParams(params: Map<String, Any>?): HttpManager? {
        body = JSONObject()
        params?.onEach { (key, value) ->
            body?.put(key, value)
        }
        return instance
    }

    override fun request(path: String, params: Map<String, Any>?) {
        params?.let {
            setParams(it)
        }
        GlobalScope.launch(Dispatchers.IO) {
            httpClient?.dispatchRequest(Constants.url, path,
                body = if (body?.length() != 0) body?.toString() else null)
                ?.also { content ->
                    withContext(Dispatchers.Main) {
                        if (content == "") {
                            httpCallBackListener?.complete()
                            return@withContext
                        }
                        val jsonObject = JSONObject(content)
                        when (jsonObject.getInt("code")) {
                            0 -> {
                                httpCallBackListener?.onSuccess(path, content)
                            }
                            else -> {
                                httpCallBackListener?.onFiled(path, jsonObject.getString("msg"))
                            }
                        }
                    }
                }
        }
    }
}