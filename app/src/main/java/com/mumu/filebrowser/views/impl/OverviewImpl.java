package com.mumu.filebrowser.views.impl;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.graphics.drawable.DrawableWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.mumu.filebrowser.R;
import com.mumu.filebrowser.eventbus.EventBus;
import com.mumu.filebrowser.eventbus.events.SelectedEvent;
import com.mumu.filebrowser.file.IFile;
import com.mumu.filebrowser.views.IOverview;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import presenter.IOverviewPresenter;
import presenter.IPresenter;
import presenter.impl.OverviewPresenterImpl;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by leonardo on 17-11-14.
 */

public class OverviewImpl implements IOverview, View.OnAttachStateChangeListener {
    private static final String TAG = OverviewImpl.class.getName();
    private static final IOverviewPresenter sOverviewPresenter = new OverviewPresenterImpl();

    @BindView(R.id.overview_name)
    TextView mName;
    @BindView(R.id.overview_type)
    TextView mType;
    @BindView(R.id.overview_size)
    TextView mSize;
    @BindView(R.id.overview_date)
    TextView mDate;
    @BindView(R.id.overview_selected_num)
    TextView mCounts;
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
    @BindString(R.string.overview_selected_counts)
    String FILES_SELECTED;

    private Resources mResources;

    public OverviewImpl(@NonNull View view) {
        checkNotNull(view);
        mResources = view.getResources();
        view.addOnAttachStateChangeListener(this);
        ButterKnife.bind(this, view);
    }

    private void showSelectedContent(boolean show) {
        int vis = show ? View.VISIBLE : View.GONE;
        mCounts.setVisibility(vis);
    }

    private void showFocusedContent(boolean show) {
        int vis = show ? View.VISIBLE : View.GONE;
        mName.setVisibility(vis);
        mType.setVisibility(vis);
        mSize.setVisibility(vis);
        mDate.setVisibility(vis);
        //mIcon.setVisibility(vis);
    }

    @Override
    public void cleanDisplay() {
        showFocusedContent(false);
        showSelectedContent(false);
    }

    @Override
    public void showFocusedview(@NonNull IFile file) {
        showFocusedContent(true);
        showSelectedContent(false);
        boolean isFolder = file.isFolder();
        mIcon.setImageDrawable(file.getIcon(mResources).mutate());
        String name = String.format(isFolder ? FOLDER_NAME : FILE_NAME, file.getName());
        mName.setText(name);
        String size = String.format(isFolder ? FOLDER_SIZE : FILE_SIZE + "byte", file.getSize());
        mSize.setText(size);
        String suffix = String.format(FILE_TYPE, isFolder ? FOLDER : file.getSuffix());
        mType.setText(suffix);
        String date = String.format(FILE_DATE, file.getLastDate(null));
        mDate.setText(date);
    }

    @Override
    public void showSelectedView(@NonNull IFile... files) {
        showFocusedContent(false);
        showSelectedContent(true);
        mCounts.setText(String.format(FILES_SELECTED, files.length));
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        ((IPresenter) sOverviewPresenter).bindView(this);
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        ((IPresenter) sOverviewPresenter).bindView(null);
    }
}
