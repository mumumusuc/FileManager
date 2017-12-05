package com.mumu.filebrowser.presenter;

import android.support.annotation.Nullable;

/**
 * Created by leonardo on 17-11-24.
 */

public interface IPresenter {
    <T> void bindView(@Nullable T view);
}
