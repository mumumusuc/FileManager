package com.mumu.filebrowser.processor

import com.mumu.filebrowser.file.FileWrapper
import com.mumu.filebrowser.file.IFile
import java.io.File


/**
 * Created by leonardo on 17-11-21.
 */
class OptionManager{
    companion object {
        fun rename(file:IFile,newname:String):Boolean {
            checkNotNull(newname)
            checkNotNull(file)
            val oldname = file.name
            val newfile = File(newname)
            when {
                oldname.equals(newname) -> {
                    return false
                }
                newname.contains('/') -> {
                    return false
                }
                newfile.exists()->{
                    return false
                }
                else->{
                    file.asFile().renameTo(newfile)
                }
            }
            return true
        }
    }
}