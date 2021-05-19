package com.example.kyc_camera.net

import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class HttpClient {

    var tempBuffer: StringBuffer? = null

    /**
     * start request of GET
     */
    fun executeRequest(url: String, method: String = "GET", body: String? = null): String {
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
            urlConnection.connectTimeout = 6000
            urlConnection.connect()

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