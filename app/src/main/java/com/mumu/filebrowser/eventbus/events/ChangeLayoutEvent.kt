package com.mumu.filebrowser.eventbus.events

import com.mumu.filebrowser.views.IListView

/**
 * Created by leonardo on 17-11-16.
 */

class ChangeLayoutEvent constructor(type:Int, @IListView.LayoutStyle layout:Int?){
    companion object {
        val ASK = 0
        val ACK = 1
    }
    val type = type
    val layout = layout
}