package com.mumu.filebrowser;

import android.Manifest;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mumu.filebrowser.eventbus.events.FileOptEvent;
import com.mumu.filebrowser.eventbus.events.OpenEvent;
import com.mumu.filebrowser.processor.IPathManager;
import com.mumu.filebrowser.processor.PathManager;
import com.mumu.filebrowser.eventbus.EventBus;
import com.mumu.filebrowser.views.IFileOption;
import com.mumu.filebrowser.views.IListView;
import com.mumu.filebrowser.views.IOverview;
import com.mumu.filebrowser.views.ITools;
import com.mumu.filebrowser.views.impl.FileOptionImpl;
import com.mumu.filebrowser.views.impl.OverviewImpl;
import com.mumu.filebrowser.views.impl.PathViewImpl;
import com.mumu.filebrowser.views.impl.ToolbarImpl;

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
    IOverview mOverview;
    ITools mTools;
    IFileOption mFileOption;

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
        mFileOption = new FileOptionImpl(mOverviewPanel);
        mPathManager.setPathView(mPathView);
        mPathManager.setOverview(new OverviewImpl(mOverviewPanel));
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                111);
        onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_storage));
        EventBus.getInstance().register(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToolbar.setTitle("");
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
        } else if (Config.Companion.backControlPath()) {
            EventBus.getInstance().post(new OpenEvent("..", "..", false));
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
        String alias = null;
        if (id == R.id.nav_camera) {
            alias = getString(R.string.nav_alias_camera);
        } else if (id == R.id.nav_music) {
            alias = getString(R.string.nav_alias_music);
        } else if (id == R.id.nav_picture) {
            alias = getString(R.string.nav_alias_picture);
        } else if (id == R.id.nav_video) {
            alias = getString(R.string.nav_alias_video);
        } else if (id == R.id.nav_document) {
            alias = getString(R.string.nav_alias_document);
        } else if (id == R.id.nav_download) {
            alias = getString(R.string.nav_alias_download);
        } else if (id == R.id.nav_storage) {
            alias = getString(R.string.nav_alias_storage);
        } else if (id == R.id.nav_settings) {

        }
        if (alias != null) {
            EventBus.getInstance().post(
                    new OpenEvent(null, alias, false)
            );
        }
        if (mDrawer != null) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}
