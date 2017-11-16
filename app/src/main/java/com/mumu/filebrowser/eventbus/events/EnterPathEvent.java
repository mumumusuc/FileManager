package com.mumu.filebrowser.eventbus.events;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by leonardo on 17-11-10.
 */

public class EnterPathEvent {
    @NonNull
    private String path;
    @Nullable
    private String alias;

    public EnterPathEvent(String path, String alias) {
        this.path = path;
        this.alias = alias;
    }

    @NonNull
    public String getPath() {
        return path = path == null ? "" : path;
    }

    @Nullable
    public String getAlias() {
        return alias;
    }
}
