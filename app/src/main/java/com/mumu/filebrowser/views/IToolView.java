package com.mumu.filebrowser.views;

import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import com.mumu.filebrowser.model.IModel;

/**
 * Created by leonardo on 17-11-12.
 */

public interface IToolView {
    void showListIcon();

    void showGridIcon();

    boolean onActionItemSelected(@NonNull MenuItem item);

    boolean cancelAllActions();
}
