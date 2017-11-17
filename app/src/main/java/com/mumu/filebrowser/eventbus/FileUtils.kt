package com.mumu.filebrowser.eventbus

import android.os.Environment
import android.util.Log
import com.google.common.base.Preconditions.checkArgument
import com.google.common.collect.Lists
import com.mumu.filebrowser.file.FileWrapper
import com.mumu.filebrowser.file.IFile
import java.io.File

/**
 * Created by leonardo on 17-11-12.
 */
class FileUtils {


    companion object {
        private val TAG = FileUtils::class.java.simpleName

        var MIME_MAP: Map<String, String>? = null

        fun checkPathLegality(path: String): Boolean = File(path).exists()

        fun isTopPath(path: String): Boolean {
            checkArgument(checkPathLegality(path))
            val parent = File(path).parentFile
            return if (!parent.exists()) true else parent.list() == null
        }

        fun listFiles(path: String): List<IFile> {
            if (!checkPathLegality(path)) {
                throw IllegalArgumentException("given path not exist")
            }
            var file = File(path);
            var list = file.listFiles()
            var size = list?.size ?: 0
            var result = Lists.newArrayListWithCapacity<IFile>(size);
            if (size > 0) {
                for (f in list) {
                    result.add(FileWrapper(f))
                    Log.d(TAG, f.name)
                }
            }
            return result
        }

        fun getNavigationPath(alias: String): String? {
            return when (alias) {
                "camera" -> {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
                }
                "music" -> {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath
                }
                "picture" -> {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
                }
                "video" -> {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath
                }
                "document" -> {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
                }
                "download" -> {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
                }
                "storage" -> {
                    Environment.getExternalStorageDirectory().absolutePath
                }
                else -> null
            }
        }

        fun getMIMEType(suffix: String) = MIME_MAP!!.get(suffix)

    }
}