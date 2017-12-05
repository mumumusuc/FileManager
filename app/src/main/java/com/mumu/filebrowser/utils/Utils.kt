package com.mumu.filebrowser.utils

import android.content.Context
import android.content.res.Resources
import android.os.Environment
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mumu.filebrowser.R
import com.mumu.filebrowser.model.IPathModel
import com.mumu.filebrowser.model.IPathModel.*
import java.io.BufferedReader
import java.io.InputStreamReader

object Utils {
    private val TAG = Utils::class.java.simpleName
    val MIME_FILE = "mime.json"
    var MIME_MAP: Map<String, String>? = null

    fun checkPath(path: String): Boolean {
        val storage = getCategoryPath(IPathModel.STORAGE)
        Log.d("checkPath", "path = $path, storage = $storage")
        return path.startsWith(storage!!)
    }

    fun checkFileName(name: String?): Boolean {
        Log.d(TAG, "checkFileName -> " + name)
        if (name == null || name.isEmpty() || name.length > 255) {
            Log.d(TAG, "checkFileName -> bad")
            return false;
        } else
            return name.matches(Regex("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$"));
    }

    fun getCategoryName(res: Resources, @IPathModel.Category category: Int): String {
        val resId =
                when (category) {
                    CAMERA -> {
                        R.string.nav_name_camera
                    }
                    MUSIC -> {
                        R.string.nav_name_music
                    }
                    PICTURE -> {
                        R.string.nav_name_picture
                    }
                    VIDEO -> {
                        R.string.nav_name_video
                    }
                    DOCUMENT -> {
                        R.string.nav_name_document
                    }
                    DOWNLOAD -> {
                        R.string.nav_name_download
                    }
                    STORAGE -> {
                        R.string.nav_name_storage
                    }
                    else -> {
                        TODO("bad category")
                    }
                }
        return res.getString(resId)
    }

    fun getCategoryPath(@IPathModel.Category category: Int): String {
        return when (category) {
            CAMERA -> {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
            }
            MUSIC -> {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath
            }
            PICTURE -> {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
            }
            VIDEO -> {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath
            }
            DOCUMENT -> {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
            }
            DOWNLOAD -> {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
            }
            STORAGE -> {
                Environment.getExternalStorageDirectory().absolutePath
            }
            else -> {
                TODO("bad category")
            }
        }
    }

    fun init(context: Context) {
        val g = GsonBuilder().create()
        val inputReader = InputStreamReader(context.assets.open(MIME_FILE))!!
        val bufReader = BufferedReader(inputReader)
        val sb = StringBuilder()
        var line: String? = bufReader.readLine()
        while (line != null) {
            sb.append(line)
            line = bufReader.readLine()
        }
        MIME_MAP = g.fromJson(
                sb.toString(),
                object : TypeToken<Map<String, String>>() {}.type)
    }

    fun getMIMEType(suffix: String?) = MIME_MAP!!.get(suffix)

    fun getImageSnap(){

    }

    fun getVideoSnap(){

    }

    fun getAudioSnap(){

    }
}