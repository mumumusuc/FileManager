package com.mumu.filebrowser.views;


import android.support.annotation.NonNull;

public interface IOverview {
    void cleanDisplay();

    void showFocusedview(@NonNull String file);

    void showSelectedView(@NonNull String[] files);
}
