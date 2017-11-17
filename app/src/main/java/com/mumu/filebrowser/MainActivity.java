package com.mumu.filebrowser;

import android.Manifest;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mumu.filebrowser.eventbus.FileUtils;
import com.mumu.filebrowser.eventbus.events.FileOptEvent;
import com.mumu.filebrowser.eventbus.events.OpenEvent;
import com.mumu.filebrowser.processor.IPathManager;
import com.mumu.filebrowser.processor.PathManager;
import com.mumu.filebrowser.eventbus.EventBus;
import com.mumu.filebrowser.views.IListView;
import com.mumu.filebrowser.views.IOverview;
import com.mumu.filebrowser.views.ITools;
import com.mumu.filebrowser.views.OverviewImpl;
import com.mumu.filebrowser.views.PathViewImpl;
import com.mumu.filebrowser.views.ToolbarImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getName();

    private int SCREEN_ORIENTATION = -1;
    private DrawerLayout mDrawer;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.main_list)
    IListView mListView;
    IPathManager mPathManager;
    PathViewImpl mPathView;
    @BindView(R.id.overview_panel)
    View mOverviewPanel;
    @BindView(R.id.action_parent_path)
    View mParentButton;
    IOverview mOverview;
    ITools mTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        SCREEN_ORIENTATION = getResources().getConfiguration().orientation;
        mPathView = new PathViewImpl(this);
        mToolbar.addView(mPathView);
        setSupportActionBar(mToolbar);
        mNavigationView.setNavigationItemSelectedListener(this);

        if (SCREEN_ORIENTATION == Configuration.ORIENTATION_PORTRAIT) {
            mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        }
        mPathManager = new PathManager(this, mListView);
        mOverview = new OverviewImpl(mOverviewPanel);
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                111);
        onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_storage));
        mParentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getInstance().post(new OpenEvent("..",false));
            }
        });
        EventBus.getInstance().register(this);
    }

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        EventBus.getInstance().post(new FileOptEvent(FileOptEvent.OPT_DELETE));
    }

    @Override
    public void onBackPressed() {

        if (mDrawer != null && mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else if (mTools.cancelAllActions()) {
            return;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mTools = new ToolbarImpl(getMenuInflater(), menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mTools.onActionItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        item.setCheckable(true);
        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_picture) {

        } else if (id == R.id.nav_video) {

        } else if (id == R.id.nav_document) {

        } else if (id == R.id.nav_download) {

        } else if (id == R.id.nav_storage) {
            Log.d(TAG, "onNavigationItemSelected -> open storage");
            EventBus.getInstance().post(
                    new OpenEvent(FileUtils.Companion.getStoragePath(),false)
            );
        } else if (id == R.id.nav_settings) {

        }
        if (mDrawer != null) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}
