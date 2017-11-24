package com.mumu.filebrowser.views;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.mumu.filebrowser.file.IFile;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by leonardo on 17-11-12.
 */

public interface IOptionView {
    void copy(@NonNull String from, @NonNull String to);

    void move(@NonNull String from, @NonNull String to);

    void rename(@NonNull String from, @NonNull String to);

    void delete(@NonNull IFile file);

    void delete(@NonNull String src);

    void create(@NonNull String name, @NonNull @CreateType int type);

    int CREATE_TYPE_FILE = 0;
    int CREATE_TYPE_FOLDER = 1;

    @IntDef({CREATE_TYPE_FILE, CREATE_TYPE_FOLDER})
    @Retention(RetentionPolicy.SOURCE)
    @interface CreateType {
    }
}
