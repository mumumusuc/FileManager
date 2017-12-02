package com.mumu.filebrowser.eventbus.events

/**
 * Created by leonardo on 17-11-30.
 */
class ContentChangeEvent(val type: Int, val name: String) {
    companion object {
        val INSERT = 1
        val REMOVE = 2
    }
}