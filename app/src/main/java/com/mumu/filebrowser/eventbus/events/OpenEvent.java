package com.mumu.filebrowser.eventbus.events;

import android.support.annotation.NonNull;

/**
 * Created by leonardo on 17-11-14.
 */

public class OpenEvent {
    private String path;
    private boolean reload = false;

    public OpenEvent(@NonNull String path, boolean reload) {
        this.path = path;
        this.reload = reload;
    }

    public String getPath() {
        return path;
    }

    public boolean getReload() {
        return reload;
    }
}
