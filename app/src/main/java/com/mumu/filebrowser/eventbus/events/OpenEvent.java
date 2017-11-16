package com.mumu.filebrowser.eventbus.events;

import android.support.annotation.NonNull;

/**
 * Created by leonardo on 17-11-14.
 */

public class OpenEvent {
    private String path;

    public OpenEvent(@NonNull String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
