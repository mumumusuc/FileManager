package com.mumu.filebrowser.eventbus.events

import android.os.Parcel
import android.os.Parcelable
import com.mumu.filebrowser.file.IFile

/**
 * Created by leonardo on 17-11-27.
 */
class FocusedEvent constructor(file: String) {
    val file = file
}