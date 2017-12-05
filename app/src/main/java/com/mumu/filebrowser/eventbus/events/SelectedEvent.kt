package com.mumu.filebrowser.eventbus.events

class SelectedEvent(files: Array<String>?) {
    /**
     * @param files    null:=select_all
     *                 size0:=select_none
     */
    val files = files
}