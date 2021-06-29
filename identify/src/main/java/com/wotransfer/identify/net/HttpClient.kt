package com.wotransfer.identify.net

import android.util.Log
import com.wotransfer.identify.Constants
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class HttpClient {

    fun dispatchRequest(
        url: String,
        path: String,
        method: String = "POST",
        body: String? = null,
    ): String {
        return when (path) {
            upload_identity_path -> {
                executeFileRequest(url, path, method, body)
            }
            else -> {
                executeRequest(url, path, method, body)
            }
        }
    }


    private fun executeRequest(
        url: String,
        path: String,
        method: String = "POST",
        body: String? = null,
    ): String {
        var result = ""
        try {
            Log.e(Constants.KYC_TAG, "url: ${url + path}")
            Log.e(Constants.KYC_TAG, "body: $body")
            val urlConnection: HttpURLConnection?
            val httpUrl = URL(url + path)
            urlConnection = httpUrl.openConnection() as HttpURLConnection
            urlConnection.doInput = true
            urlConnection.doOutput = true
            urlConnection.requestMethod = method
            urlConnection.setRequestProperty("Pragma:", "no-cache")
            urlConnection.setRequestProperty("Cache-Control", "no-cache")
            urlConnection.setRequestProperty("Content-Type",
                "application/json;charset=utf-8")
            urlConnection.setRequestProperty("Connection", "keep-alive")
            urlConnection.connectTimeout = 6000
            urlConnection.connect()

            val writer =
                BufferedWriter(OutputStreamWriter(urlConnection.outputStream, "UTF-8"))
            writer.write(body)
            writer.close()

            val responseCode = urlConnection.responseCode
            val responseMessage = urlConnection.responseMessage
            Log.e(Constants.KYC_TAG, "$responseCode=$responseMessage")
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                var line: String?
                while ((inReader.readLine().also { line = it }) != null) {
                    result += line
                }
                Log.e(Constants.KYC_TAG, "run: $result")
            }
        } catch (e: Exception) {
            Log.e(Constants.KYC_TAG, "Post Request Failed....")
            Log.e(Constants.KYC_TAG, "${e.message}")
            e.printStackTrace()
        }
        return result
    }

    /**
     * 上传图片
     */
    private fun executeFileRequest(
        url: String,
        path: String,
        method: String = "POST",
        body: String? = null,
    ): String {
        var result = ""
        val strParams = HashMap<String, Any>()
        val json = JSONObject(body!!)
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            strParams[key] = json[key]
        }
        var file: File? = null
        var conn: HttpURLConnection? = null
        try {
            val httpUrl = URL(url + path)
            conn = httpUrl.openConnection() as HttpURLConnection
            conn.requestMethod = method
            conn.readTimeout = 6000
            conn.connectTimeout = 6000
            conn.doOutput = true
            conn.doInput = true
            conn.useCaches = false //Post 请求不能使用缓存
            conn.setRequestProperty("Connection", "Keep-Alive")
            conn.setRequestProperty("Charset", "UTF-8")
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=$BOUNDARY")
            val dos = DataOutputStream(conn.outputStream)
            val strSb = StringBuilder()
            //其他参数
            for ((key, value) in strParams) {
                if ("file" != key) {
                    strSb.append(PREFIX)
                        .append(BOUNDARY)
                        .append(LINE_END)
                        .append("Content-Disposition: form-data; name=\"$key\"")
                        .append(LINE_END)
                        .append(LINE_END)
                        .append(value)
                        .append(LINE_END)
                } else {
                    file = File(value as String)
                }
            }
            //图片上传
            strSb.append(PREFIX)
                .append(BOUNDARY)
                .append(LINE_END)
                .append("Content-Disposition: form-data; name=\"file\"; filename=\""
                        + "pic.png" + "\"" + LINE_END)
                .append(LINE_END)
                .append(LINE_END)//很关键
            val `is`: InputStream = FileInputStream(file)
            val buffer = ByteArray(1024)
            var len: Int
            while (`is`.read(buffer).also { len = it } != -1) {
                dos.write(buffer, 0, len)
            }
            `is`.close()
//
//            dos.writeBytes(PREFIX + BOUNDARY + LINE_END)
//            dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
//                    + "pic.png" + "\"")
//            dos.writeBytes(LINE_END + LINE_END)
//            val fStream = FileInputStream(file)
//            val bufferSize = 1024
//            val buffer = ByteArray(bufferSize)
//            var length = -1
//            while (fStream.read(buffer).also { length = it } != -1) {
//                dos.write(buffer, 0, length)
//            }
//            dos.writeBytes(LINE_END)
//            fStream.close()

            dos.writeBytes(strSb.toString())
            dos.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_END)
            dos.flush()
            dos.close()

            Log.e(Constants.KYC_TAG, "postResponseCode() = " + conn.responseCode)
            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                val `in` = conn.inputStream
                val reader = BufferedReader(InputStreamReader(`in`))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    result += line
                }
                Log.e(Constants.KYC_TAG, "run: $result")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            conn?.disconnect()
        }
        return result
    }

    companion object {
        private const val LINE_END = "\r\n"
        private const val PREFIX = "--"
        private const val BOUNDARY: String = "****"
    }
}