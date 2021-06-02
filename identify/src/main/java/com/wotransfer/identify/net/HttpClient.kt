package com.wotransfer.identify.net

import java.io.BufferedInputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class HttpClient {

    var tempBuffer: StringBuffer? = null

    /**
     * start request
     */
    fun executeRequest(url: String, method: String = "POST", body: String? = null): String {

        /// boundary就是request头和上传文件内容的分隔符(可自定义任意一组字符串)
        val BOUNDARY = "******";
        // 用来标识payLoad+文件流的起始位置和终止位置(相当于一个协议,告诉你从哪开始,从哪结束)
        var preFix = ("\r\n--$BOUNDARY--\r\n");

        try {
            var urlConnection: HttpURLConnection? = null
            val httpUrl = URL(url)
            urlConnection = httpUrl.openConnection() as HttpURLConnection
            urlConnection.doInput = true
            urlConnection.doOutput = true
            urlConnection.requestMethod = method
            urlConnection.setRequestProperty("Pragma:", "no-cache")
            urlConnection.setRequestProperty("Cache-Control", "no-cache")
            urlConnection.setRequestProperty("Content-Type", "text/xml")
            urlConnection.setRequestProperty("Connection", "keep-alive")
            urlConnection.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=$BOUNDARY")
            urlConnection.connectTimeout = 6000
            urlConnection.connect()

            val out: OutputStream = DataOutputStream(urlConnection.outputStream)

            urlConnection.outputStream.write(body?.toByteArray("utf-8" as Charset))

            tempBuffer = StringBuffer()
            val inputStream = BufferedInputStream(urlConnection.inputStream)
            val rd = InputStreamReader(inputStream, "GBK")
            val c = rd.read()
            while (c != -1) {
                tempBuffer!!.append(c.toChar())
            }
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tempBuffer.toString()
    }


}