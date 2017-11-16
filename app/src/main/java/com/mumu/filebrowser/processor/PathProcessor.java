package com.mumu.filebrowser.processor;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.mumu.filebrowser.eventbus.EventBus;
import com.mumu.filebrowser.eventbus.FileUtils;
import com.mumu.filebrowser.eventbus.events.EnterPathEvent;
import com.mumu.filebrowser.eventbus.events.FileOptEvent;
import com.mumu.filebrowser.eventbus.events.SwitchStyleEvent;
import com.mumu.filebrowser.file.FileWrapper;
import com.mumu.filebrowser.file.IFile;
import com.mumu.filebrowser.views.IListView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * receive path from eventbus and present in toolbar and listview
 */

public class PathProcessor implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = PathProcessor.class.getSimpleName();

    private IListView mPathView;
    private List<IFile> mList = new CopyOnWriteArrayList<>();

    public PathProcessor() {
        EventBus.getInstance().register(this);
    }

    public void setPathView(@NonNull IListView view) {
        mPathView = view;
        mPathView.setList(mList);
    }

    public void onSwitchStyleEvent(@NonNull SwitchStyleEvent event) {
        if (mPathView == null) {
            return;
        }
        int action = event.getAction();
        mPathView.showAs(action, true);
    }

    public void onEnterPathEvent(@NonNull EnterPathEvent event) {
        //TODO check illegal
        boolean exist = FileUtils.Companion.checkPathLegality(event.getPath());
        if (!exist) {
            Log.w(TAG, "path or file not exist,ignore");
            return;
        }
        //WARNING: this may cause ANR
        mList.clear();
        mList.addAll(FileUtils.Companion.listFiles(event.getPath()));
        mPathView.notifyDataSetChanged();
    }

    public void onFileOptEvent(@NonNull FileOptEvent event) {
        if (mPathView == null) return;
        switch (mPathView.getCurrentMode()) {
            case IListView.MODE_VIEW:
                //mPathView.addItem(0, IFile.create(FileWrapper.class).setName(String.valueOf(mList.size() + 1)));
                break;
            case IListView.MODE_MULTI_SELECT:
                if (mPathView.getCurrentMode() == IListView.MODE_MULTI_SELECT) {
                    Set<Integer> del = new HashSet<>();
                    synchronized (mList = mPathView.getList()) {
                        for (int i = mList.size() - 1; i >= 0; i--) {
                            if (((FileWrapper) mList.get(i)).isSelected()) {
                                del.add(i);
                            }
                        }
                        Log.d(TAG, "onFileOptEvent : delete = " + del);
                        //TODO delete file,invalidate when successed otherwise toast
                        mPathView.removeItem(del.toArray(new Integer[0]));
                        if (mPathView.getItemSelectedCount() == 0) {
                            mPathView.switchToViewMode(true);
                        }
                    }
                }
                break;
        }
    }

    public void onClick(View v) {

    }

    public boolean onLongClick(View v) {
      return true;
    }

    public boolean onBackPressed() {
        if (mPathView == null) return false;
        if (mPathView.getCurrentMode() == IListView.MODE_MULTI_SELECT) {
            mPathView.switchToViewMode(true);
            mPathView.selectAll(false);
            return true;
        }
        return false;
    }
}