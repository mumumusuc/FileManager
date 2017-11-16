package com.mumu.filebrowser.processor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.mumu.filebrowser.Config;
import com.mumu.filebrowser.eventbus.EventBus;
import com.mumu.filebrowser.eventbus.FileUtils;
import com.mumu.filebrowser.eventbus.events.ChangeLayoutEvent;
import com.mumu.filebrowser.eventbus.events.OpenEvent;
import com.mumu.filebrowser.eventbus.events.ShowFileEvent;
import com.mumu.filebrowser.eventbus.events.ShowPathEvent;
import com.mumu.filebrowser.file.FileWrapper;
import com.mumu.filebrowser.file.IFile;
import com.mumu.filebrowser.views.IListView;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.mumu.filebrowser.views.IListView.LAYOUT_STYLE_GRID;
import static com.mumu.filebrowser.views.IListView.LAYOUT_STYLE_LIST;

/**
 * Created by leonardo on 17-11-14.
 */

public class PathManager implements IPathManager, IListView.OnItemClickListener<IFile> {

    private static final String TAG = PathManager.class.getSimpleName();

    private Context mContext;
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
    private Set<IFile> mSelectedState;

    public PathManager(@NonNull Context context, @NonNull IListView view) {
        checkNotNull(view);
        checkNotNull(context);
        mListView = view;
        mContext = context;
        mFileList = Lists.newArrayList();
        mListView.setList(mFileList);
        mListView.setOnItemClickListener(this);
        mSelectedState = Sets.newHashSet();
        EventBus.getInstance().register(this);
    }

    @Subscribe
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

    @Override
    public void open(@NonNull IFile file) {
        checkNotNull(file);
        if (file.isFolder()) {
            openFolder(file);
        } else {
            openFile(file);
        }
        focus(true, file);
    }

    @Override
    public void focus(boolean focus, @NonNull IFile file) {
        EventBus.getInstance().post(new ShowFileEvent(focus ? file : null));
    }

    @Override
    public void select(boolean select, @NonNull IFile... files) {
        checkNotNull(files);
        for (int i = files.length - 1; i >= 0; i--) {
            mListView.setItemSelected(files[i], select);
            if (select) {
                mSelectedState.add(files[i]);
            } else if (mSelectedState.contains(files[i])) {
                mSelectedState.remove(files[i]);
            }
        }
    }

    @Subscribe
    public void changeLayout(@NonNull ChangeLayoutEvent event) {
        if (event.getType() == ChangeLayoutEvent.Companion.getASK()) {
            int layout;
            if (event.getLayout() != null) {
                layout = event.getLayout();
                mListView.showAs(layout, true);
            } else if ((layout = mListView.getCurrentLayoutStyle()) == LAYOUT_STYLE_LIST) {
                mListView.showAsGrid(true);
            } else if ((layout = mListView.getCurrentLayoutStyle()) == LAYOUT_STYLE_GRID) {
                mListView.showAsList(true);
            }
            EventBus.getInstance().post(new ChangeLayoutEvent(ChangeLayoutEvent.Companion.getACK(), layout));
        }
    }

    private void openFolder(@NonNull IFile file) {
        //WARNING: this may cause ANR
        mFileList.clear();
        clearSelectedState();
        mFileList.addAll(mFileOrdering.sortedCopy(FileUtils.Companion.listFiles(file.getPath())));
        mListView.notifyDataSetChanged();
        EventBus.getInstance().post(new ShowPathEvent(
                file.getPath(),
                Pair.create(FileUtils.Companion.getStoragePath(), "内部存储")));
    }

    private void openFile(@NonNull IFile file) {
        String type = FileUtils.Companion.getMIMEType(file.getSuffix());
        if (type == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(
                    mContext,
                    mContext.getBasePackageName() + ".fileProvider",
                    ((FileWrapper) file).asFile());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(((FileWrapper) file).asFile());
        }
        intent.setDataAndType(uri, type);
        mContext.startActivity(intent);
    }

    @Override
    public void onItemClick(@NonNull View v, @NonNull IFile file) {
        checkArgument(v != null && file != null);
        if (mListView.getCurrentMode() == IListView.MODE_VIEW) {
            if (Config.Companion.doubleClickOpen()) {
                focus(true, file);
            } else {
                open(file);
            }
        } else if (mListView.getCurrentMode() == IListView.MODE_MULTI_SELECT) {
            select(!((FileWrapper) file).isSelected(), file);
            if (mSelectedState.size() == 0) {
                mListView.switchToViewMode(true);
            }
        }
    }

    @Override
    public void onItemLongClick(@NonNull View v, @NonNull IFile file) {
        mListView.switchToMultiSelectMode(true);
        select(!((FileWrapper) file).isSelected(), file);
    }

    private void clearSelectedState() {
        mSelectedState.clear();
    }
}
