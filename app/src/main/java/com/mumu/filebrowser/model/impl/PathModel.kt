package com.mumu.filebrowser.model.impl

import android.util.Log
import com.mumu.filebrowser.model.IPathModel
import com.mumu.filebrowser.model.IPathModel.*
import com.mumu.filebrowser.utils.Utils
import java.io.File
import android.os.FileObserver
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.ContentChangeEvent
import com.mumu.filebrowser.eventbus.events.ContentChangeEvent.Companion.INSERT
import com.mumu.filebrowser.eventbus.events.ContentChangeEvent.Companion.REMOVE
import com.mumu.filebrowser.eventbus.events.PathChangeEvent
import java.util.*

object PathModel : IPathModel {
    private val TAG = PathModel.javaClass.simpleName

    private var mCategory: Int = STORAGE
    private var mPath: String = ""
    private var mListener: PathListener? = null
    private val mPathChain = ChainList<String>()

    init {
        checkCategoryPath()
        //init
        enter(STORAGE)
    }

    override fun enter(category: Int): Boolean {
        if (mCategory != category) {
            mPathChain.clear()
        }
        mCategory = category
        return enter(Utils.getCategoryPath(mCategory))
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
        if (!path.startsWith(Utils.getCategoryPath(mCategory))) {
            Log.i(TAG, "enter -> not in same category,ignore")
            return false
        }
        enter(path, true)
        return true
    }

    private fun enter(path: String, push: Boolean) {
        Log.i(TAG, "enter -> ${String}")
        mPath = path
        if (push) mPathChain.push(mPath)
        refresh()
        mListener?.stopWatching()
        mListener = PathListener(mPath)
        mListener?.startWatching()
    }

    override fun getPath(): String = mPath

    override fun havePreviousPath(): Boolean = mPathChain.havePrevious()

    override fun haveNextPath(): Boolean = mPathChain.haveNext()

    override fun enterPrevious(): Boolean {
        Log.i(TAG, "enterPrevious")
        if (mPathChain.movePrevious() == null) {
            return false
        }
        enter(mPathChain.getCurrent(), false)
        return true
    }

    override fun enterNext(): Boolean {
        Log.i(TAG, "enterNext")
        if (mPathChain.moveNext() == null) {
            return false
        }
        enter(mPathChain.getCurrent(), false)
        return true
    }

    override fun enterParent(): Boolean {
        if (mPath == Utils.getCategoryPath(mCategory)) {
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
                    val file = File(Utils.getCategoryPath(it))
                    if (!file.exists()) {
                        file.mkdirs()
                    }
                }
    }

    override fun refresh() {
        EventBus.getInstance().post(PathChangeEvent())
    }

    /**/
    private class PathListener(path: String) : FileObserver(path, CREATE or DELETE or MOVED_FROM or MOVED_TO or MODIFY) {
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
                event.and(MOVED_FROM) == MOVED_FROM -> {
                    name = "MOVED_FROM"
                    type = REMOVE
                }
                event.and(MOVED_TO) == MOVED_TO -> {
                    name = "MOVED_TO"
                    type = INSERT
                }
                event.and(MODIFY) == MODIFY -> {
                    name = "MODIFY"
                    type = 0
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

    private class ChainList<T> {
        private val mList = Stack<T>()
        private var mPoint = -1

        fun getCurrent(): T = mList[mPoint]

        fun havePrevious():Boolean = mPoint > 0

        fun movePrevious(): ChainList<T>? {
            Log.i(TAG, "movePrevious -> size=${mList.size},point=${mPoint}")
            if (!havePrevious()) {
                return null
            } else {
                mPoint--
                return this
            }
        }

        fun haveNext():Boolean = mPoint < mList.size - 1

        fun moveNext(): ChainList<T>? {
            Log.i(TAG, "moveNext -> size=${mList.size},point=${mPoint}")
            if (!haveNext()) {
                return null
            } else {
                mPoint++
                return this
            }
        }

        fun push(arg: T) {
            mList.push(arg)
            mPoint++
        }

        fun clear() {
            mList.clear()
            mList.removeAllElements()
            mPoint = -1
        }
    }
}