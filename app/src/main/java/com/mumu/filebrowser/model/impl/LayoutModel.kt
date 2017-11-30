package com.mumu.filebrowser.model.impl

import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.eventbus.events.LayoutChangeEvent
import com.mumu.filebrowser.model.ILayoutModel
import com.mumu.filebrowser.model.ILayoutModel.LAYOUT_STYLE_LIST

/**
 * Created by leonardo on 17-11-30.
 */
object LayoutModel : ILayoutModel {
    private @ILayoutModel.LayoutStyle
    var mLayoutStyle: Int = LAYOUT_STYLE_LIST

    override fun setLayoutStyle(@ILayoutModel.LayoutStyle style: Int) {
        mLayoutStyle = style
        EventBus.getInstance().post(LayoutChangeEvent())
    }

    override fun getLayoutStyle(): Int {
        return mLayoutStyle
    }
}