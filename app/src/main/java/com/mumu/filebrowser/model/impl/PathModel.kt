package com.mumu.filebrowser.model.impl

import android.content.Context
import android.util.Log
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.PathChangeEvent
import com.mumu.filebrowser.file.IFile
import com.mumu.filebrowser.model.IPathModel
import com.mumu.filebrowser.utils.FileUtils

/**
 * Created by leonardo on 17-11-25.
 */
object PathModel : IPathModel {
    private var initialized: Boolean = false
    private var mCategory: String? = null
    private var mPath: String? = null
    private var mContext: Context? = null
    private val mList: MutableList<IFile> = arrayListOf()
    private var mInvalidateFlag = false


    fun init(context: Context, category: String, path: String) {
        if (!initialized) {
            initialized = true
            if (!FileUtils.checkCategory(category)) {
                TODO("bad category")
            }
            if (!FileUtils.checkPath(path)) {
                TODO("bad path")
            }
            mContext = context
            setPath(category, path, false)
        }
    }

    override fun setPath(category: String, path: String, post: Boolean): Boolean {
        Log.d("PathModel","setPath")
        if (FileUtils.checkCategory(category)
                && FileUtils.checkPath(path)) {
            mCategory = category
            mPath = path
            mInvalidateFlag = true
            if (post) {
                EventBus.getInstance().post(PathChangeEvent())
            }
            return true
        }
        return false
    }

    override fun getCurrentCategory(): String {
        return mCategory!!
    }


    override fun getCurrentPath(): String {
        return mPath!!
    }

    override fun getCurrentFiles(): MutableList<IFile> {
        if (mInvalidateFlag) {
            mInvalidateFlag = false
            mList.clear()
            mList.addAll(FileUtils.listFiles(mPath!!))
        }
        return mList
    }
}