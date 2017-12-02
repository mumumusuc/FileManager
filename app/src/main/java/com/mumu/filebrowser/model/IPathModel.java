package com.mumu.filebrowser.model;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.mumu.filebrowser.file.IFile;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by leonardo on 17-11-25.
 */

public interface IPathModel {

    int CAMERA = 0;
    int MUSIC = 1;
    int PICTURE = 2;
    int VIDEO = 3;
    int DOCUMENT = 4;
    int DOWNLOAD = 5;
    int STORAGE = 6;
    int USB1 = 7;
    int USB2 = 8;
    int USB3 = 9;

    @IntDef({CAMERA, MUSIC, PICTURE, VIDEO, DOCUMENT, DOWNLOAD, STORAGE, USB1, USB2, USB3})
    @Retention(RetentionPolicy.SOURCE)
    @interface Category {
    }

    boolean enter(@Category int category);

    @Category
    int getCategory();

    boolean enter(@NonNull String path);

    @NonNull
    String getPath();

    boolean enterPrevious();

    @NonNull
    List<String> listFiles();

}
