package com.mumu.filebrowser.eventbus.events

import com.mumu.filebrowser.file.IFile

/**
 * Created by leonardo on 17-11-22.
 */
class SelectedEvent constructor(files: Array<IFile>?) {
    val files = files
}