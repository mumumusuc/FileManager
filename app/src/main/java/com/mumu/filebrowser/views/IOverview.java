package com.mumu.filebrowser.views;


import android.support.annotation.Nullable;

import com.mumu.filebrowser.file.IFile;

/**
 * Created by leonardo on 17-11-14.
 */

public interface IOverview {
    void showOverview(@Nullable IFile file);

    void showSelectedView(@Nullable IFile... files);
}
