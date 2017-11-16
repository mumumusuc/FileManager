package com.mumu.filebrowser.eventbus.events;

import android.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by leonardo on 17-11-10.
 */

public class FileOptEvent {
    public static final int OPT_COPY = 0;
    public static final int OPT_MOVE = 1;
    public static final int OPT_RENAME = 2;
    public static final int OPT_CREATE = 3;
    public static final int OPT_DELETE = 4;
    public static final int OPT_SEND = 5;/*share*/

    @IntDef({OPT_COPY, OPT_MOVE, OPT_RENAME, OPT_RENAME, OPT_DELETE, OPT_SEND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OPT {
    }

    private int option;
    private String fromPath;
    private String toPath;
    private String newName;
    private String sendTo;

    public FileOptEvent(@OPT int option) {
        this.option = option;
    }

    @OPT
    public int getOption() {
        return option;
    }
}
