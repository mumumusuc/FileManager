package com.mumu.filebrowser.eventbus

import android.os.Environment
import android.support.v4.util.Pair
import android.util.Log
import com.google.common.base.Preconditions.checkArgument
import com.google.common.collect.HashBasedTable
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
        var PATH_TABLE: Map<String, Pair<String, Integer>>? = null

        fun checkPathLegality(path: String): Boolean = File(path).exists()

        fun isTopPath(path: String, alias: String): Boolean {
            checkArgument(checkPathLegality(path))
            if (getNavigationPath(alias).equals(path))
                return true
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
            return PATH_TABLE!![alias]?.first
        }

        fun getNavigationName(alias: String): Integer? {
            return PATH_TABLE!![alias]?.second
        }

        fun getMIMEType(suffix: String) = MIME_MAP!!.get(suffix)
    }
}