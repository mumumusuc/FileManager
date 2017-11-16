package com.mumu.filebrowser.eventbus.events

import com.mumu.filebrowser.file.IFile

/**
 * Created by leonardo on 17-11-15.
 */
public class ShowFileEvent constructor(file: IFile) {
    val file = file
}