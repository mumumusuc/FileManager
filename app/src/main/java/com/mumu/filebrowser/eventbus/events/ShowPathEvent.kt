package com.mumu.filebrowser.eventbus.events

import android.support.v4.util.Pair

/**
 * Created by leonardo on 17-11-15.
 */
class ShowPathEvent constructor(path: String, alias: Pair<String, String>?) {
    val path = path
    val alias = alias
}