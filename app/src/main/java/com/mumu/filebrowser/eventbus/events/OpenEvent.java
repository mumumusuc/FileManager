package com.mumu.filebrowser.eventbus.events;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by leonardo on 17-11-14.
 */

public class OpenEvent {
    private String path;
    private String alias;
    private boolean reload = false;

    public OpenEvent(@Nullable String path, @NonNull String alias, boolean reload) {
        this.path = path;
        this.alias = alias;
        this.reload = reload;
    }

    public String getPath() {
        return path;
    }

    public String getAlias() {
        return alias;
    }

    public boolean getReload() {
        return reload;
    }
}
