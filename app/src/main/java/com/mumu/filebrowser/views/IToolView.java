package com.mumu.filebrowser.views;

import android.support.annotation.NonNull;
import android.view.MenuItem;

/**
 * Created by leonardo on 17-11-12.
 */

public interface IToolView {
    void showListIcon();

    void showGridIcon();

    void enableAction(int action, boolean enable);

    void showSearchWait(boolean show);
}
