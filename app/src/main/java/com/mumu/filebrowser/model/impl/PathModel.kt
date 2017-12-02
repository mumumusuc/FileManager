package com.mumu.filebrowser.model.impl

import android.util.Log
import com.mumu.filebrowser.model.IPathModel
import com.mumu.filebrowser.model.IPathModel.*
import com.mumu.filebrowser.utils.FileUtils
import java.io.File
import android.os.FileObserver
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.ContentChangeEvent
import com.mumu.filebrowser.eventbus.events.ContentChangeEvent.Companion.INSERT
import com.mumu.filebrowser.eventbus.events.ContentChangeEvent.Companion.REMOVE
import com.mumu.filebrowser.eventbus.events.PathChangeEvent

object PathModel : IPathModel {
    private val TAG = PathModel.javaClass.simpleName

    private var mCategory: Int = STORAGE
    private var mPath: String = ""
    private var mListener: PathListener? = null

    init {
        checkCategoryPath()
        //init
        enter(STORAGE)
    }

    override fun enter(category: Int): Boolean {
        mCategory = category
        return enter(FileUtils.getCategoryPath(mCategory))
    }

    override fun getCategory(): Int = mCategory

    override fun enter(path: String): Boolean {
        // == allowed in kotlin
        if (mPath == path) {
            Log.i(TAG, "enter -> same path,ignore")
            return false
        }
        if (!File(path).exists()) {
            Log.i(TAG, "enter -> bad path,ignore")
            return false
        }
        if (!path.startsWith(FileUtils.getCategoryPath(mCategory))) {
            Log.i(TAG, "enter -> not in same category,ignore")
            return false
        }
        mPath = path
        //TODO: refresh file list cache
        refresh()
        mListener?.stopWatching()
        mListener = PathListener(mPath)
        mListener?.startWatching()
        return true
    }

    override fun getPath(): String = mPath

    override fun enterPrevious(): Boolean {
        if (mPath == FileUtils.getCategoryPath(mCategory)) {
            Log.i(TAG, "category top,ignore")
            return false
        }
        return enter(File(mPath).parent)
    }

    override fun listFiles(): List<String> {
        val list = mutableListOf<String>()
        File(mPath).listFiles().map {
            list.add(it.path)
        }
        val start = System.currentTimeMillis()
        Log.i(TAG, "find ${list.size} files, use ${System.currentTimeMillis() - start} ms")
        return list
    }

    private fun checkCategoryPath() {
        listOf(CAMERA, MUSIC, PICTURE, VIDEO, DOCUMENT, DOWNLOAD, STORAGE)
                .map {
                    val file = File(FileUtils.getCategoryPath(it))
                    if (!file.exists()) {
                        file.mkdirs()
                    }
                }
    }

    private fun refresh() {
        EventBus.getInstance().post(PathChangeEvent())
    }

    /**/
    private class PathListener(path: String) : FileObserver(path, CREATE.or(DELETE)) {
        override fun onEvent(event: Int, path: String?) {
            if (path.isNullOrEmpty()) {
                return
            }
            val name: String
            val type: Int
            when {
                event.and(CREATE) == CREATE -> {
                    name = "CREATE "
                    type = INSERT
                }
                event.and(DELETE) == DELETE -> {
                    name = "DELETE "
                    type = REMOVE
                }
                else -> {
                    name = "OTHER"
                    type = 0
                }
            }
            Log.d(TAG, "onEvent -> code=$event, event=$name, path=$path")
            EventBus.getInstance().post(ContentChangeEvent(type, mPath + File.separatorChar + path))
        }
    }
}