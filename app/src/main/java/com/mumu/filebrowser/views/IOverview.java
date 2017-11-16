package com.mumu.filebrowser.views;


import android.support.annotation.NonNull;
import com.mumu.filebrowser.eventbus.events.ShowFileEvent;

/**
 * Created by leonardo on 17-11-14.
 */

public interface IOverview {
    void onShowOverview(@NonNull ShowFileEvent event);
}
