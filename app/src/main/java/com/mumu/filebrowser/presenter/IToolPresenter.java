package com.mumu.filebrowser.presenter;

import android.annotation.NonNull;

/**
 * Created by leonardo on 17-11-25.
 */

public interface IToolPresenter {
    void onPrevious();

    void onNext();

    void onRefresh();

    void onSelect();

    void onChangeLayout();

    void onSearch(@NonNull String content);

    void onCancelSearch();
}
