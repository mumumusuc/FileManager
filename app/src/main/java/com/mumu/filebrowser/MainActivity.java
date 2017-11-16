package com.mumu.filebrowser;

import android.Manifest;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
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

import com.google.common.eventbus.Subscribe;
import com.mumu.filebrowser.eventbus.FileUtils;
import com.mumu.filebrowser.eventbus.events.EnterPathEvent;
import com.mumu.filebrowser.eventbus.events.FileOptEvent;
import com.mumu.filebrowser.eventbus.events.OpenEvent;
import com.mumu.filebrowser.eventbus.events.SwitchStyleEvent;
import com.mumu.filebrowser.processor.IPathManager;
import com.mumu.filebrowser.processor.PathManager;
import com.mumu.filebrowser.eventbus.EventBus;
import com.mumu.filebrowser.views.IListView;
import com.mumu.filebrowser.views.IOverview;
import com.mumu.filebrowser.views.OverviewImpl;
import com.mumu.filebrowser.views.PathViewImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mumu.filebrowser.views.IListView.LAYOUT_STYLE_GRID;
import static com.mumu.filebrowser.views.IListView.LAYOUT_STYLE_LIST;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
        mPathManager = new PathManager(mListView);
        EventBus.getInstance().register(this);

        mOverview = new OverviewImpl(mOverviewPanel);
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                111);
        onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_storage));
    }

    @OnClick(R.id.fab)
    public void onFabClick(View view) {
        EventBus.getInstance().post(new FileOptEvent(FileOptEvent.OPT_DELETE));
    }

    @Override
    public void onBackPressed() {

        if (mDrawer != null && mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else /*if (mPath.onBackPressed()) {
            return;
        } else */ {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_style_list:
                EventBus.getInstance().post(new SwitchStyleEvent(LAYOUT_STYLE_LIST));
                item.setChecked(true);
                return true;
            case R.id.action_style_grid:
                EventBus.getInstance().post(new SwitchStyleEvent(LAYOUT_STYLE_GRID));
                item.setChecked(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        item.setCheckable(true);
        item.setChecked(true);
        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_picture) {

        } else if (id == R.id.nav_video) {

        } else if (id == R.id.nav_document) {

        } else if (id == R.id.nav_download) {

        } else if (id == R.id.nav_storage) {
            EventBus.getInstance().post(
                    new OpenEvent(FileUtils.Companion.getStoragePath())
            );
        } else if (id == R.id.nav_settings) {

        }
        if (mDrawer != null) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}
