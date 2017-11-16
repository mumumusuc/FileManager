package com.mumu.filebrowser.eventbus

import android.content.res.Resources
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresApi
import android.util.Log
import com.google.common.collect.Lists
import com.mumu.filebrowser.file.FileWrapper
import com.mumu.filebrowser.file.IFile
import java.io.File

/**
 * Created by leonardo on 17-11-12.
 */
class FileUtils {
    companion object {
        var MIME_MAP: Map<String, String>? = null;

        fun checkPathLegality(path: String): Boolean = File(path).exists()

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
                }
            }
            return result
        }

        fun getStoragePath(): String = Environment.getExternalStorageDirectory().absolutePath

        fun getMIMEType(suffix: String) = MIME_MAP!!.get(suffix)

    }
}