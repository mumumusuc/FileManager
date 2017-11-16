package com.mumu.filebrowser.processor;

import android.support.annotation.NonNull;

import com.google.common.eventbus.Subscribe;
import com.mumu.filebrowser.eventbus.events.OpenEvent;
import com.mumu.filebrowser.file.IFile;

/**
 * Created by leonardo on 17-11-12.
 * this interface managers IListView-presenter
 */

public interface IPathManager {
    @Subscribe
    void open(@NonNull OpenEvent event);

    void openFolder(@NonNull IFile file);

    void openFile(@NonNull IFile file);
}
