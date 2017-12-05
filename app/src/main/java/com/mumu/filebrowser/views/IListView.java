package com.mumu.filebrowser.views;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by leonardo on 17-11-10.
 */

public interface IListView<T> {
    /*style*/
    void showAsList(boolean anim);

    void showAsGrid(boolean anim);

    /*list*/
    void notifyDataSetChanged();

    void notifyItemInserted(int index);

    void notifyItemRemoved(int index);

    void setEmptyView(@NonNull View v);

    void setEmptyView(int layout);

    /*select*/
    void select(T item, boolean select);

    /*focus*/
    void focus(T item, boolean focus);
}
