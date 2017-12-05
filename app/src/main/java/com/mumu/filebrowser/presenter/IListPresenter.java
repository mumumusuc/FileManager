package com.mumu.filebrowser.presenter;

import android.support.annotation.NonNull;

import com.mumu.filebrowser.model.ILayoutModel;

import java.util.List;

/**
 * Created by leonardo on 17-11-24.
 */

public interface IListPresenter<T> {
    @NonNull
    List<T> getList();

    void onItemClick(@NonNull T item);

    void onItemLongClick(@NonNull T item);

    boolean isItemFocused(@NonNull T item);

    boolean isItemSelected(@NonNull T item);

    @ILayoutModel.LayoutStyle
    int getCurrentLayoutStyle();
}
