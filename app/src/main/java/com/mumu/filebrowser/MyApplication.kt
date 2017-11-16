package com.mumu.filebrowser

import android.app.Application
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mumu.filebrowser.eventbus.FileUtils
import java.io.BufferedReader
import java.io.InputStreamReader


/**
 * Created by limz on 17-11-16.
 */
class MyApplication : Application() {

    val MIME_FILE = "mime.json"
    var initialized = false

    override fun onCreate() {
        super.onCreate()
        if (!initialized) {
            val start = System.currentTimeMillis();
            val g = GsonBuilder().create()
            val inputReader = InputStreamReader(assets.open(MIME_FILE))!!
            val bufReader = BufferedReader(inputReader)
            val sb = StringBuilder()
            var line: String? = bufReader.readLine()
            while (line != null) {
                sb.append(line)
                line = bufReader.readLine()
            }
            FileUtils.MIME_MAP = g.fromJson(
                    sb.toString(),
                    object : TypeToken<Map<String, String>>() {}.type)
            val end = System.currentTimeMillis()
            Log.i("", String.format("load memi.json use %d ms", end - start))
            initialized = true
        }
    }
}