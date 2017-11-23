package com.mumu.filebrowser.processor;

import android.support.annotation.NonNull;

import com.mumu.filebrowser.file.IFile;
import com.mumu.filebrowser.views.IOverview;
import com.mumu.filebrowser.views.IPathView;

/**
 * Created by leonardo on 17-11-12.
 * this interface managers IListView-presenter
 */

public interface IPathManager {

    void open(@NonNull IFile file);

    void focus(boolean focus, @NonNull IFile file);

    void select(boolean select, @NonNull IFile... files);

    void setPathView(@NonNull IPathView pathView);

    void setOverview(@NonNull IOverview overview);

}
