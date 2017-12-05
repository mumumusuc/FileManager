package com.mumu.filebrowser.views;

import android.content.res.Resources;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by leonardo on 17-11-12.
 */

public interface IOptionView {

    int NULL = 0;
    int CREATE = 1;
    int COPY = 2;
    int CUT = 3;
    int RENAME = 4;
    int DELETE = 5;
    int PASTE = 6;

    @IntDef({NULL, CREATE, COPY, CUT, RENAME, DELETE, PASTE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Option {
    }

    @NonNull
    Resources getResources();

    void showDialog(String title, String msg, String hint);

    void dismissDialog();

    void enableOption(@Option int option, boolean enable);
}
