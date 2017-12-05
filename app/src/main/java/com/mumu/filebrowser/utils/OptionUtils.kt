package com.mumu.filebrowser.utils

import android.support.annotation.IntDef
import android.util.Log
import com.google.common.io.Files
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

object OptionUtils {
    private val TAG = OptionUtils.javaClass.simpleName

    fun copy(from: String, to: String): Int {
        checkNotNull(from, { "Copy source file name must NOT be null." })
        checkNotNull(to, { "Copy target file name must NOT be null." })
        val sourceFile = File(from)
        val targetFile = File(to)
        try {
            if (sourceFile.isFile) {
                FileUtils.copyFileToDirectory(sourceFile, targetFile)
            } else if (sourceFile.isDirectory) {
                FileUtils.copyDirectoryToDirectory(sourceFile, targetFile)
            }
        } catch (fileIoEx: IOException) {
            Log.e(TAG, "ERROR trying to copy file '$from' to file '$to' - ${fileIoEx.toString()}")
            return IO_FAILED
        }
        return NO_ERROR
    }

    fun move(from: String, to: String): Int {
        checkNotNull(from, { "move source file name must NOT be null." })
        checkNotNull(to, { "move target file name must NOT be null." })
        val sourceFile = File(from)
        val targetFile = File(to)
        try {
            if (sourceFile.isFile) {
                FileUtils.moveFileToDirectory(sourceFile, targetFile, false)
            } else if (sourceFile.isDirectory) {
                FileUtils.moveDirectoryToDirectory(sourceFile, targetFile, false)
            }
        } catch (fileIoEx: IOException) {
            Log.e(TAG, "ERROR trying to move file '$from' to file '$to' \n ${fileIoEx.toString()}")
            return IO_FAILED
        }
        return NO_ERROR
    }

    fun rename(from: String, newName: String): Int {
        val srcFile = File(from)
        val dstFile = File(newName)
        try {
            if (srcFile.isFile) {
                FileUtils.moveFile(srcFile, dstFile)
            } else if (srcFile.isDirectory) {
                FileUtils.moveDirectory(srcFile, dstFile)
            }
        } catch (fileIoEx: IOException) {
            Log.e(TAG, "ERROR trying to rename file '$from' to '$newName' \n ${fileIoEx.toString()}")
            return IO_FAILED
        }
        return NO_ERROR
    }

    fun delete(path: String): Int {
        Log.i(TAG, "need delete $path")
        val file = File(path)
        if (!file.exists()) {
            Log.e(TAG, "delete bad path")
            return ERROR_BAD_PATH
        }
        if (file.isFile || file.list().isEmpty()) {
            try {
                if (file.delete()) {
                    Log.i(TAG, "delete $path")
                    return NO_ERROR
                }
                return IO_FAILED
            } catch (e: IOException) {
                Log.e(TAG, "ERROR on delete $file")
                return IO_FAILED
            }
        } else {
            file.listFiles().forEach { delete(it.absolutePath) }
            delete(path)
        }
        return NO_ERROR
    }

    fun create(name: String?, @CreateType type: Long): Int {
        if (name.isNullOrBlank() || !Utils.checkPath(name!!)) {
            return ERROR_BAD_PATH
        }
        Log.d("create", "name = " + name)
        val file = File(name)
        when (type) {
            CREATE_TYPE_FILE -> {
                if (file.exists() && file.isFile) {
                    return ERROR_FILE_EXIST
                }
                if (!file.createNewFile()) {
                    return IO_FAILED
                }
            }
            CREATE_TYPE_FOLDER -> {
                if (file.exists() && file.isDirectory) {
                    return ERROR_FILE_EXIST
                }
                if (!file.mkdirs()) {
                    return IO_FAILED
                }
            }
        }
        return NO_ERROR
    }

    const val CREATE_TYPE_FILE = 0L
    const val CREATE_TYPE_FOLDER = 1L

    @IntDef(CREATE_TYPE_FILE, CREATE_TYPE_FOLDER)
    @Retention(RetentionPolicy.SOURCE)
    annotation class CreateType

    const val NO_ERROR = 0
    const val ERROR_BAD_PATH = 1
    const val ERROR_FILE_EXIST = 2
    const val IO_FAILED = 3;
}