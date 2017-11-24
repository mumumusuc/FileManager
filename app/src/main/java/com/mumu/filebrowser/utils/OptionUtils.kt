package com.mumu.filebrowser.utils

import android.support.annotation.IntDef
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

        fun rename(from: String, to: String): Boolean {
            FileUtils.checkPathLegality(from)
            checkNotNull(to)
            val newfile = File(to)
            when {
                from.equals(to) -> {
                    return false
                }
                to.contains('/') -> {
                    return false
                }
                newfile.exists() -> {
                    return false
                }
                else -> {
                    File(from).renameTo(newfile)
                }
            }
            return true
        }

        fun delete(file: IFile) {}

        fun delete(src: String) {}

        fun create(name: String?, @CreateType type: Long): Boolean {
            if (name == null) return false
            if (FileUtils.checkPathLegality(name)) {
                return false
            }
            val file = File(name)
            when (type) {
                CREATE_TYPE_FILE -> {
                    file.createNewFile()
                }
                CREATE_TYPE_FOLDER -> {
                    file.mkdirs()
                }
            }
            return true
        }

        const val CREATE_TYPE_FILE = 0L
        const val CREATE_TYPE_FOLDER = 1L

        @IntDef(CREATE_TYPE_FILE, CREATE_TYPE_FOLDER)
        @Retention(RetentionPolicy.SOURCE)
        annotation class CreateType
    }
}