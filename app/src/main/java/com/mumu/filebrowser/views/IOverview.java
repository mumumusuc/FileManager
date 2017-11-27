package com.mumu.filebrowser.views;


import android.support.annotation.NonNull;
import com.mumu.filebrowser.file.IFile;

/**
 * Created by leonardo on 17-11-14.
 */

public interface IOverview {
    void cleanDisplay();

    void showFocusedview(@NonNull IFile file);

    void showSelectedView(@NonNull IFile[] files);
}
