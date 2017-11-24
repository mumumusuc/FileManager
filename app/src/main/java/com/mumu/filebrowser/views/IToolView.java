package com.mumu.filebrowser.views;

import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by leonardo on 17-11-12.
 */

public interface IToolView {
    boolean onActionItemSelected(@NonNull MenuItem item);

    boolean cancelAllActions();
}
