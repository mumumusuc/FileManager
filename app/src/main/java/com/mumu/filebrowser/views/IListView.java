package com.mumu.filebrowser.views;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.mumu.filebrowser.file.IFile;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by leonardo on 17-11-10.
 */

public interface IListView<T> {
    public static final int LAYOUT_STYLE_LIST = 0;
    public static final int LAYOUT_STYLE_GRID = 1;

    @IntDef({LAYOUT_STYLE_LIST, LAYOUT_STYLE_GRID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LayoutStyle {
    }

    public static final int MODE_VIEW = 10;
    public static final int MODE_MULTI_SELECT = 11;

    @IntDef({MODE_VIEW, MODE_MULTI_SELECT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    interface OnItemClickListener<T>{
        void onItemClick(@NonNull View v,@NonNull T file);
        void onItemLongClick(@NonNull View v,@NonNull T file);
    }

    /*list*/
    void setList(@NonNull List<T> list);

    @Nullable
    List<T> getList();

    void notifyDataSetChanged();

    /*style*/
    void showAs(@LayoutStyle int style, boolean anim);

    void showAsList(boolean anim);

    void showAsGrid(boolean anim);

    @LayoutStyle
    int getCurrentLayoutStyle();

    /*touch*/
    void setOnItemClickListener(@Nullable OnItemClickListener listener);

    /*mode*/
    void switchTo(@Mode int mode, boolean anim);

    void switchToViewMode(boolean anim);

    void switchToMultiSelectMode(boolean anim);

    @Mode
    int getCurrentMode();

    /*select*/
    void setItemSelected(@Nullable T item, boolean selected);

    void selectAll(boolean selected);

    /*add & remove*/
    void addItem(int position, @NonNull T item);

    void removeItem(@NonNull T... items);
}
