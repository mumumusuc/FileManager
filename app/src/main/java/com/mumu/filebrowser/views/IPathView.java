package com.mumu.filebrowser.views;

import android.support.annotation.NonNull;

import com.google.common.eventbus.Subscribe;
import com.mumu.filebrowser.eventbus.events.ShowPathEvent;

/**
 * Created by leonardo on 17-11-13.
 */

public interface IPathView {
    void showPath(@NonNull ShowPathEvent event);
}
