package com.mumu.filebrowser.views;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.mumu.filebrowser.R;
import com.mumu.filebrowser.eventbus.EventBus;
import com.mumu.filebrowser.eventbus.events.ShowFileEvent;
import com.mumu.filebrowser.file.IFile;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by leonardo on 17-11-14.
 */

public class OverviewImpl implements IOverview {
    private static final String TAG = OverviewImpl.class.getName();

    @BindView(R.id.overview_name)
    TextView mName;
    @BindView(R.id.overview_type)
    TextView mType;
    @BindView(R.id.overview_size)
    TextView mSize;
    @BindView(R.id.overview_date)
    TextView mDate;
    @BindView(R.id.overview_image)
    ImageView mIcon;

    @BindString(R.string.overview_file_name)
    String FILE_NAME;
    @BindString(R.string.overview_folder_name)
    String FOLDER_NAME;
    @BindString(R.string.overview_file_type)
    String FILE_TYPE;
    @BindString(R.string.overview_file_size)
    String FILE_SIZE;
    @BindString(R.string.overview_folder_size)
    String FOLDER_SIZE;
    @BindString(R.string.overview_file_date)
    String FILE_DATE;
    @BindString(R.string.overview_folder)
    String FOLDER;

    private Resources mResources;
    private View mParent;

    public OverviewImpl(@NonNull View view) {
        checkNotNull(view);
        mParent = view;
        mResources = view.getResources();
        ButterKnife.bind(this, view);
        EventBus.getInstance().register(this);
    }

    private void setSelfVisibility(boolean visible) {
        mParent.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private void showOverview(@Nullable IFile file) {
        if (file == null) {
            setSelfVisibility(false);
            return;
        }
        setSelfVisibility(true);
        boolean isFolder = file.isFolder();
        mIcon.setImageDrawable(file.getIcon(mResources));
        String name = String.format(isFolder ? FOLDER_NAME : FILE_NAME, file.getName());
        mName.setText(name);
        String size = String.format(isFolder ? FOLDER_SIZE : FILE_SIZE + "byte", file.getSize());
        mSize.setText(size);
        String suffix = String.format(FILE_TYPE, isFolder ? FOLDER : file.getSuffix());
        mType.setText(suffix);
        String date = String.format(FILE_DATE, file.getLastDate(null));
        mDate.setText(date);
    }

    @Subscribe
    @Override
    public void onShowOverview(@NonNull ShowFileEvent event) {
        Log.d(TAG, "onShowOverview");
        showOverview(event.getFile());
    }
}
