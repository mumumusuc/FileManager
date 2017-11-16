package com.mumu.filebrowser.eventbus.events;

import com.mumu.filebrowser.views.IListView;

/**
 * Created by leonardo on 17-11-10.
 */

public class SwitchStyleEvent {
    private int action;

    public SwitchStyleEvent(@IListView.LayoutStyle int action) {
        this.action = action;
    }

    @IListView.LayoutStyle
    public int getAction() {
        return action;
    }
}
