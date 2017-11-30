package com.mumu.filebrowser.utils

import android.support.annotation.IntDef
import android.util.Log
import com.mumu.filebrowser.file.FileWrapper
import com.mumu.filebrowser.file.IFile
import java.io.File
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy


/**
 * Created by leonardo on 17-11-21.
 */
class OptionUtils {
    companion object {
        fun copy(from: String, to: String) {}

        fun move(from: String, to: String) {}

        fun rename(from: IFile, to: String): Int {
            if (to.isNullOrBlank() || !FileUtils.checkPath(to!!)) {
                return ERROR_BAD_PATH
            }
            Log.d("create", "name = " + to)
            if (!from.asFile().renameTo(File(to))) {
                return FAILED
            }
            return NO_ERROR
        }

        fun delete(file: IFile): Boolean {
            return file.asFile().delete()
        }

        fun delete(src: String): Boolean {
            return delete(FileWrapper(src))
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
                        return FAILED
                    }
                }
                CREATE_TYPE_FOLDER -> {
                    if (file.exists() && file.isDirectory) {
                        return ERROR_FILE_EXIST
                    }
                    if (!file.mkdirs()) {
                        return FAILED
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
        const val FAILED = 3;
    }
}