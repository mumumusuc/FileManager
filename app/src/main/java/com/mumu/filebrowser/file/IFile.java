package com.mumu.filebrowser.file;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import java.io.File;

/**
 * Created by leonardo on 17-11-10.
 */

public interface IFile {
    @NonNull
    String getName();

    @NonNull
    String getPath();

    @NonNull
    Drawable getIcon(@NonNull Resources res);

    @NonNull
    int getIconResource();

    @Nullable
    String getSuffix();

    /**
     * @return if this file is a folder
     */
    boolean isFolder();

    /**
     * @return file size int Byte
     */
    long getSize();

    /**
     *
     * @param format time-format,default is "yyyy-MM-dd HH:mm:ss"
     * @return last modified time
     */
    @NonNull
    String getLastDate(@Nullable String format);

    @NonNull
    File asFile();
}
