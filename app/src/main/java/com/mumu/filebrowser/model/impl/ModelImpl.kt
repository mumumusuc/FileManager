package com.mumu.filebrowser.model.impl

import android.content.Context
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.LayoutChangeEvent
import com.mumu.filebrowser.eventbus.events.PathChangeEvent
import com.mumu.filebrowser.file.IFile
import com.mumu.filebrowser.model.IModel
import com.mumu.filebrowser.model.IModel.LAYOUT_STYLE_LIST
import com.mumu.filebrowser.utils.FileUtils

/**
 * Created by leonardo on 17-11-25.
 */
object ModelImpl : IModel {
    private var initialized: Boolean = false
    private var mCategory: String? = null
    private var mPath: String? = null
    private var mContext: Context? = null
    private val mList: MutableList<IFile> = arrayListOf()
    private var mInvalidateFlag = false
    private @IModel.LayoutStyle
    var mLayoutStyle: Int = LAYOUT_STYLE_LIST

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
            setPath(category, path)
        }
    }

    override fun setPath(category: String, path: String): Boolean {
        if (FileUtils.checkCategory(category)
                && FileUtils.checkPath(path)
                && path.contains(FileUtils.getNavigationPath(category)!!)
                && !path.equals(mPath)) {
            mCategory = category
            mPath = path
            mInvalidateFlag = true
            EventBus.getInstance().post(PathChangeEvent())
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

    override fun setLayoutStyle(@IModel.LayoutStyle style: Int): Boolean {
        mLayoutStyle = style
        EventBus.getInstance().post(LayoutChangeEvent())
        return true
    }

    override fun getLayoutStyle(): Int {
        return mLayoutStyle
    }
}