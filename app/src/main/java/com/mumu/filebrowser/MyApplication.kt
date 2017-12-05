package com.mumu.filebrowser

import android.app.Application
import android.util.Log
import com.mumu.filebrowser.model.ILayoutModel.LAYOUT_STYLE_LIST
import com.mumu.filebrowser.model.impl.LayoutModel
import com.mumu.filebrowser.utils.Utils

class MyApplication : Application() {
    var initialized = false

    override fun onCreate() {
        super.onCreate()
        if (!initialized) {
            val start = System.currentTimeMillis();
            Utils.init(baseContext)
            val end = System.currentTimeMillis()
            Log.i("", String.format("load memi.json use %d ms", end - start))
            /**/
            LayoutModel.setLayoutStyle(LAYOUT_STYLE_LIST)
            initialized = true
        }
    }
}