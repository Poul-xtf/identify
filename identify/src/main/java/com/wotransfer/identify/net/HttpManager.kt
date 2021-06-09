package com.wotransfer.identify.net

import android.widget.Toast
import com.wotransfer.identify.Constants
import com.wotransfer.identify.util.mContext
import com.wotransfer.identify.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.NullPointerException

class HttpManager(httpClient: HttpClient/*, httpCallBackListener: HttpCallBackListener*/) :
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
                    instance = HttpManager(httpClient/*, httpCallBackListener*/)
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

    override fun request(path: String?, params: Map<String, Any>?) {
        params?.let {
            setParams(it)
        }
        path?.let {
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    httpClient?.executeRequest(Constants.url, path,
                        body = if (body?.length() != 0) body?.toString() else null)
                        ?.also { content ->
                            withContext(Dispatchers.Main) {
                                val jsonObject = JSONObject(content)
                                when (jsonObject.getInt("code")) {
                                    0 -> {
                                        httpCallBackListener?.onSuccess(path, content)
                                    }
                                    else -> {
                                        httpCallBackListener?.onFiled()
                                    }
                                }
                            }
                        }
                }
            }
        } ?: let {
            throw NullPointerException("url path is null")
        }
    }
}