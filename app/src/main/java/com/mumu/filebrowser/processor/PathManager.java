package com.mumu.filebrowser.processor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mumu.filebrowser.eventbus.EventBus;
import com.mumu.filebrowser.eventbus.FileUtils;
import com.mumu.filebrowser.eventbus.events.OpenEvent;
import com.mumu.filebrowser.eventbus.events.ShowFileEvent;
import com.mumu.filebrowser.eventbus.events.ShowPathEvent;
import com.mumu.filebrowser.file.FileWrapper;
import com.mumu.filebrowser.file.IFile;
import com.mumu.filebrowser.views.IListView;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by leonardo on 17-11-14.
 */

public class PathManager implements IPathManager, IListView.OnItemClickListener {

    private static final String TAG = PathManager.class.getSimpleName();

    private IListView mListView;
    private List<IFile> mFileList;
    private Ordering<IFile> mFileOrdering = new Ordering<IFile>() {
        @Override
        public int compare(@Nullable IFile left, @Nullable IFile right) {
            checkNotNull(left); // for GWT
            checkNotNull(right);
            return left.getName().compareTo(right.getName());
        }
    };

    public PathManager(@NonNull IListView view) {
        checkNotNull(view);
        mListView = view;
        mFileList = Lists.newArrayList();
        mListView.setList(mFileList);
        mListView.setOnItemClickListener(this);
        EventBus.getInstance().register(this);
    }

    @Override
    public void open(@NonNull OpenEvent event) {
        String path = event.getPath();
        if (!FileUtils.Companion.checkPathLegality(path)) {
            Log.w(TAG, "open -> illegal path, ignore");
            return;
        }
        Log.d(TAG, "open -> " + path);
        IFile file = new FileWrapper(path);
        open(file);
    }

    private void open(@NonNull IFile file) {
        checkNotNull(file);
        if (file.isFolder()) {
            openFolder(file);
        } else {
            openFile(file);
        }
        //打开路径成功后设置显示路径和文件详情
        EventBus.getInstance().post(new ShowPathEvent(
                file.getPath(),
                Pair.create(FileUtils.Companion.getStoragePath(), "内部存储")));
        EventBus.getInstance().post(new ShowFileEvent(file));
    }

    @Override
    public void openFolder(@NonNull IFile file) {
        //WARNING: this may cause ANR
        mFileList.clear();
        mFileList.addAll(mFileOrdering.sortedCopy(FileUtils.Companion.listFiles(file.getPath())));
        mListView.notifyDataSetChanged();
    }

    @Override
    public void openFile(@NonNull IFile file) {

    }

    @Override
    public void onItemClick(@NonNull View v, @NonNull IFile file) {
        checkArgument(v != null && file != null);
        open(file);
    }

    @Override
    public void onItemLongClick(@NonNull View v, @NonNull IFile file) {

    }
}
