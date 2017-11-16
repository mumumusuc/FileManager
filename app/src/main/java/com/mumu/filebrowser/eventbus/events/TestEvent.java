package com.mumu.filebrowser.eventbus.events;

import android.annotation.NonNull;

/**
 * Created by leonardo on 17-11-10.
 */

public class TestEvent {
    private String msg;

    public TestEvent(@NonNull String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
