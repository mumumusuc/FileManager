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

public interface IModel {
    int LAYOUT_STYLE_LIST = 0;
    int LAYOUT_STYLE_GRID = 1;

    @IntDef({LAYOUT_STYLE_LIST, LAYOUT_STYLE_GRID})
    @Retention(RetentionPolicy.SOURCE)
    @interface LayoutStyle {
    }

    boolean setPath(@NonNull String category, @NonNull String path);

    @NonNull
    String getCurrentCategory();

    @NonNull
    String getCurrentPath();

    @NonNull
    List<IFile> getCurrentFiles();

    boolean setLayoutStyle(@LayoutStyle int style);

    @LayoutStyle
    int getLayoutStyle();
}
