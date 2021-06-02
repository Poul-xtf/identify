package com.wotransfer.identify.net

import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.wotransfer.identify.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.NullPointerException

class HttpManager(httpClient: HttpClient, httpCallBackListener: HttpCallBackListener) :
    HttpListener {

    private var httpClient: HttpClient? = httpClient
    var httpCallBackListener: HttpCallBackListener? = httpCallBackListener

    companion object {
        private var instance: HttpManager? = null
        fun getInstance(
            httpClient: HttpClient,
            httpCallBackListener: HttpCallBackListener,
        ): HttpManager? {
            synchronized(HttpManager::class) {
                if (instance == null) {
                    instance = HttpManager(httpClient, httpCallBackListener)
                }
            }
            return instance
        }
    }

    private var body: JSONObject? = null
    fun setParams(params: Map<String, Any>?): HttpManager? {
        body = JSONObject()
        params?.onEach { (key, value) ->
            body?.put(key, value)
        }/* ?: let {
            throw NullPointerException("params is null")
        }*/
        Log.d("xtf->params", body.toString())
        return instance
    }

    //开始请求
//    fun startRequest(path: String?, params: Map<String, Any>? = null) {
//        params?.let {
//            setParams(it)
//        }
//        path?.let {
//            httpClient?.executeRequest(Constants.url + path,
//                body = if (body?.length() != 0) body?.toString() else null)
//                ?.also { content ->
//                    httpCallBackListener?.onSuccess(content)
//                }
//        } ?: let {
//            throw NullPointerException("url path is null")
//        }
//    }

    override fun request(path: String?, params: Map<String, Any>?) {
        params?.let {
            setParams(it)
        }
        path?.let {
            GlobalScope.launch(Dispatchers.IO) {
                httpClient?.executeRequest(Constants.url + path,
                    body = if (body?.length() != 0) body?.toString() else null)
                    ?.also { content ->
                        withContext(Dispatchers.Main) {
                            httpCallBackListener?.onSuccess(content)
                        }
                    }
            }
        } ?: let {
            throw NullPointerException("url path is null")
        }
    }
}