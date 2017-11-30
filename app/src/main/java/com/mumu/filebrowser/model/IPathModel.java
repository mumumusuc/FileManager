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


    boolean setPath(@NonNull String category, @NonNull String path, Boolean post);

    @NonNull
    String getCurrentCategory();

    @NonNull
    String getCurrentPath();

    @NonNull
    List<IFile> getCurrentFiles();


}
