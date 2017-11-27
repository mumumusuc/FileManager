package com.mumu.filebrowser.views.impl

import android.annotation.SuppressLint
import android.content.Context
import android.support.design.widget.NavigationView
import android.util.AttributeSet
import android.util.Log
import android.view.MenuItem
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.mumu.filebrowser.R
import com.mumu.filebrowser.views.INavigationView
import presenter.INavigationPresenter
import presenter.IPresenter
import presenter.impl.NavigationPresenterImpl

/**
 * Created by leonardo on 17-11-24.
 */
class NavigationViewImpl : NavigationView, INavigationView, NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private val sNavigationPresenter: INavigationPresenter = NavigationPresenterImpl()
    }

    private val mAlisaMap: BiMap<Int, String> = HashBiMap.create()

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mAlisaMap.put(R.id.nav_camera, resources.getString(R.string.nav_alias_camera))
        mAlisaMap.put(R.id.nav_music, resources.getString(R.string.nav_alias_music))
        mAlisaMap.put(R.id.nav_picture, resources.getString(R.string.nav_alias_picture))
        mAlisaMap.put(R.id.nav_video, resources.getString(R.string.nav_alias_video))
        mAlisaMap.put(R.id.nav_document, resources.getString(R.string.nav_alias_document))
        mAlisaMap.put(R.id.nav_download, resources.getString(R.string.nav_alias_download))
        mAlisaMap.put(R.id.nav_storage, resources.getString(R.string.nav_alias_storage))
        setNavigationItemSelectedListener(this);
        mAlisaMap.map { Log.d("mAlisaMap", "< " + it.key + " , " + it.value + " >") }
    }

    @SuppressLint("RestrictedApi")
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        (sNavigationPresenter as IPresenter).bindView(null)
    }

    @SuppressLint("RestrictedApi")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (sNavigationPresenter as IPresenter).bindView(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isCheckable = true
        //menu.
        var alias: String? = mAlisaMap.get(item.itemId)
        return sNavigationPresenter?.onNavigation(alias!!) ?: false
    }

    override fun select(navi: String) {
        menu.findItem(mAlisaMap.inverse().get(navi) ?: -1).isChecked = true
        onNavigationItemSelected(menu.findItem(mAlisaMap.inverse().get(navi) ?: -1))
    }

}
