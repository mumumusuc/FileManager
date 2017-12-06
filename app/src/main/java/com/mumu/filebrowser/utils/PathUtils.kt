package com.mumu.filebrowser.utils

import android.content.res.Resources
import android.graphics.drawable.Drawable
import com.google.common.io.Files
import com.mumu.filebrowser.R
import com.mumu.filebrowser.model.impl.PathModel
import org.apache.commons.io.FileUtils
import java.io.File
import java.text.SimpleDateFormat

/**
 * Created by leonardo on 17-12-4.
 */
class PathUtils(path: String) {
    companion object {
        private val DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

        fun get(path: String): PathUtils {
            return PathUtils(path)
        }
    }

    enum class Type { AUDIO, VIDEO, IMAGE, TEXT, ZIP, APK, UNKNOWN }

    private val file = File(path)

    fun getName(): String = file.name

    fun getSuffix(): String? = Files.getFileExtension(file.absolutePath)

    fun getType(): Type {
        val type = Utils.getMIMEType(getSuffix())
        when {
            type == null -> {
                return Type.UNKNOWN
            }
            type.startsWith("audio", true) -> {
                return Type.AUDIO
            }
            type.startsWith("video", true) -> {
                return Type.VIDEO
            }
            type.startsWith("image", true) -> {
                return Type.IMAGE
            }
            type.startsWith("text", true) -> {
                return Type.TEXT
            }
            getSuffix()?.toLowerCase() == "apk" -> {
                return Type.APK
            }
            else -> {
                return Type.UNKNOWN
            }
        }
    }

    fun getIcon(resources: Resources): Drawable {
        if (isFolder()) {
            return resources.getDrawable(R.drawable.ic_folder, null)
        } else {
            when (getType()) {
                Type.AUDIO -> {
                    return resources.getDrawable(R.drawable.ic_audio, null)
                }
                Type.VIDEO -> {
                    return resources.getDrawable(R.drawable.ic_video, null)
                }
                Type.IMAGE -> {
                    return resources.getDrawable(R.drawable.ic_image, null)
                }
                Type.TEXT -> {
                    return resources.getDrawable(R.drawable.ic_text, null)
                }
                Type.APK -> {
                    return resources.getDrawable(R.drawable.ic_apk, null)
                }
                else -> {
                    return resources.getDrawable(R.drawable.ic_none, null)
                }
            }
        }
    }

    fun isFile(): Boolean = file.isFile

    fun isFolder(): Boolean = file.isDirectory

    /**
     * @return the length of the file, or recursive size of the directory, provided (in bytes).\
     * or size of directory in bytes, 0 if directory is security restricted, a negative number when the real total is greater than java.lang.Long.MAX_VALUE.
     */
    fun getSize(): Long {
        if (isFile()) {
            return FileUtils.sizeOf(file)
        } else if (isFolder()) {
            return PathModel.listFiles().size.toLong()
        }
        return -1L
    }

    /**
     *
     * @param format time-format,default is "yyyy-MM-dd HH:mm:ss"
     * @return last modified time
     */
    fun getLastDate(format: String?): String {
        val useformat = format ?: DEFAULT_DATE_FORMAT
        return SimpleDateFormat(useformat).format(file.lastModified())
    }

    fun getSnap() {

    }

    fun getFile(): File = file
}