package com.mumu.filebrowser.presenter;

/**
 * Created by leonardo on 17-11-27.
 */

public interface IOptionPresenter {

    void onCopy();

    void onMove();

    void onPaste();

    void onRename();

    void onDelete();

    void onCreate();

    void onCancel();

    void onConfirm(String content);
}
