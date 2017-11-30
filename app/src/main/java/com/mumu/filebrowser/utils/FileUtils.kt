package com.mumu.filebrowser.utils

import android.support.v4.util.Pair
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
        var PATH_TABLE: Map<String, Pair<String, Int>>? = null

        fun checkPath(path: String): Boolean {
            val storage = getNavigationPath("storage")
            Log.d("checkPath", "path = $path, storage = $storage")
            return path.startsWith(storage!!)
        }

        fun checkCategory(category: String) = PATH_TABLE!!.containsKey(category)

        fun checkFileName(name: String?): Boolean {
            Log.d(TAG, "checkFileName -> " + name)
            if (name == null || name.isEmpty() || name.length > 255) {
                Log.d(TAG, "checkFileName -> bad")
                return false;
            } else
                return name.matches(Regex("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$"));
        }

        fun isTopPath(path: String, alias: String): Boolean {
            checkArgument(checkPath(path))
            if (getNavigationPath(alias).equals(path))
                return true
            val parent = File(path).parentFile
            return if (!parent.exists()) true else parent.list() == null
        }

        fun listFiles(path: String): List<IFile> {
            if (!checkPath(path)) {
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

        fun getNavigationName(alias: String): Int? {
            return PATH_TABLE!![alias]?.second
        }

        fun getMIMEType(suffix: String?) = MIME_MAP!!.get(suffix)

    }
}