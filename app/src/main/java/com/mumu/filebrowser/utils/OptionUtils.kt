package com.mumu.filebrowser.utils

import android.support.annotation.IntDef
import android.util.Log
import com.google.common.io.Files
import com.mumu.filebrowser.file.FileWrapper
import com.mumu.filebrowser.file.IFile
import java.io.File
import java.io.IOException
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy


/**
 * Created by leonardo on 17-11-21.
 */
object OptionUtils {
    private val TAG = OptionUtils.javaClass.simpleName

    fun copy(from: String, to: String): Int {
        checkNotNull(from, { "Copy source file name must NOT be null." })
        checkNotNull(to, { "Copy target file name must NOT be null." })
        val sourceFile = File(from)
        val targetFile = File(to)
        if (!sourceFile.exists() || targetFile.exists()) {
            Log.e(TAG, "source file not exist or target file already exist")
            return ERROR_FILE_EXIST
        }
        try {
            Files.copy(sourceFile, targetFile)
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
        if (!sourceFile.exists() || targetFile.exists()) {
            Log.e(TAG, "source file not exist or target file already exist")
            return ERROR_FILE_EXIST
        }
        try {
            Files.move(sourceFile, targetFile)
        } catch (fileIoEx: IOException) {
            Log.e(TAG, "ERROR trying to copy file '$from' to file '$to' - ${fileIoEx.toString()}")
            return IO_FAILED
        }
        return NO_ERROR
    }

    fun rename(from: String, newName: String): Int {
        if (!FileUtils.checkFileName(newName)) {
            Log.e(TAG, "rename bad name")
            return ERROR_BAD_PATH
        }
        return move(from, File(from).parent + File.separatorChar + newName)
    }

    fun delete(path: String): Int {
        Log.i(TAG,"need delete $path")
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
        if (name.isNullOrBlank() || !FileUtils.checkPath(name!!)) {
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