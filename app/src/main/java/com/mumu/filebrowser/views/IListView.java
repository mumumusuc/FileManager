package com.mumu.filebrowser.views;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.mumu.filebrowser.file.IFile;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import presenter.IListPresenter;

/**
 * Created by leonardo on 17-11-10.
 */

public interface IListView<T> {
    /*style*/
    void showAsList(boolean anim);

    void showAsGrid(boolean anim);

    /*list*/
    void notifyDataSetChanged();

    /*select*/
    void select(T... item);

    /*focus*/
    void focus(T item);
}
