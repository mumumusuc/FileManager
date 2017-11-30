package com.mumu.filebrowser

import android.app.Application
import android.os.Environment
import android.support.v4.util.Pair
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mumu.filebrowser.model.ILayoutModel.LAYOUT_STYLE_LIST
import com.mumu.filebrowser.model.impl.LayoutModel
import com.mumu.filebrowser.model.impl.PathModel
import com.mumu.filebrowser.utils.FileUtils
import java.io.BufferedReader
import java.io.File
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
            /**/
            FileUtils.PATH_TABLE = mapOf(
                    kotlin.Pair(
                            getString(R.string.nav_alias_camera),
                            Pair.create(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath,
                                    R.string.nav_name_camera)
                    ),
                    kotlin.Pair(
                            getString(R.string.nav_alias_music),
                            Pair.create(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath,
                                    R.string.nav_name_music
                            )
                    ),
                    kotlin.Pair(
                            getString(R.string.nav_alias_picture),
                            Pair.create(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath,
                                    R.string.nav_name_picture
                            )
                    ),
                    kotlin.Pair(
                            getString(R.string.nav_alias_video),
                            Pair.create(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath,
                                    R.string.nav_name_video
                            )
                    ),
                    kotlin.Pair(
                            getString(R.string.nav_alias_document),
                            Pair.create(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath,
                                    R.string.nav_name_document
                            )
                    ),
                    kotlin.Pair(
                            getString(R.string.nav_alias_download),
                            Pair.create(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath,
                                    R.string.nav_name_download
                            )
                    ),
                    kotlin.Pair(
                            getString(R.string.nav_alias_storage),
                            Pair.create(
                                    Environment.getExternalStorageDirectory().absolutePath,
                                    R.string.nav_name_storage
                            )
                    )
            )
            /**/
            val defaultCategory = getString(R.string.nav_alias_storage)
            val defaultPath = FileUtils.getNavigationPath(defaultCategory)
            PathModel.init(this, defaultCategory, defaultPath!!)
            /**/
            checkNavigationPath()
            /**/
            LayoutModel.setLayoutStyle(LAYOUT_STYLE_LIST)
            initialized = true
        }
    }

    private fun checkNavigationPath() {
        var file = File(FileUtils.getNavigationPath(getString(R.string.nav_alias_camera)))
        if (!file.exists()) {
            file.mkdirs()
        }
        file = File(FileUtils.getNavigationPath(getString(R.string.nav_alias_music)))
        if (!file.exists()) {
            file.mkdirs()
        }
        file = File(FileUtils.getNavigationPath(getString(R.string.nav_alias_video)))
        if (!file.exists()) {
            file.mkdirs()
        }
        file = File(FileUtils.getNavigationPath(getString(R.string.nav_alias_document)))
        if (!file.exists()) {
            file.mkdirs()
        }
        file = File(FileUtils.getNavigationPath(getString(R.string.nav_alias_picture)))
        if (!file.exists()) {
            file.mkdirs()
        }
        file = File(FileUtils.getNavigationPath(getString(R.string.nav_alias_download)))
        if (!file.exists()) {
            file.mkdirs()
        }
    }
}