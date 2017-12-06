package com.mumu.filebrowser.presenter.impl

import android.content.Context
import android.util.Log
import com.google.common.eventbus.Subscribe
import com.mumu.filebrowser.Config
import com.mumu.filebrowser.R
import com.mumu.filebrowser.eventbus.EventBus
import com.mumu.filebrowser.model.IPathModel
import com.mumu.filebrowser.model.impl.PathModel
import com.mumu.filebrowser.views.IListView
import com.mumu.filebrowser.presenter.IListPresenter
import com.mumu.filebrowser.presenter.IPresenter
import android.os.Handler
import android.os.Message
import android.view.View
import com.google.common.collect.Ordering
import com.google.common.collect.Queues
import com.mumu.filebrowser.eventbus.events.*
import com.mumu.filebrowser.eventbus.events.ContentChangeEvent.Companion.INSERT
import com.mumu.filebrowser.eventbus.events.ContentChangeEvent.Companion.REMOVE
import com.mumu.filebrowser.model.ILayoutModel
import com.mumu.filebrowser.model.impl.LayoutModel
import com.mumu.filebrowser.utils.PathUtils

class ListPresenterImpl : IListPresenter<String>, IPresenter {

    private val TAG = ListPresenterImpl::class.java.simpleName
    private val MODE_NORMAL_VIEW = 10L
    private val MODE_MULTI_SELECT = 11L

    private val mPathModel: IPathModel = PathModel
    private val mLayoutModel: ILayoutModel = LayoutModel
    private val mStateModel = SelectState
    private var mListView: IListView<String>? = null
    private var mCacheList: MutableList<String>
    private var mContext: Context? = null
    private var mCurrentViewMode = MODE_NORMAL_VIEW
    private val mBufferedQueen = BufferedQueue()
    private val mFileOrdering = object : Ordering<String>() {
        override fun compare(left: String?, right: String?): Int {
            checkNotNull(left) // for GWT
            checkNotNull(right)
            return left!!.toLowerCase().compareTo(right!!.toLowerCase())
        }
    }

    init {
        EventBus.getInstance().register(this)
        mCacheList = mFileOrdering.sortedCopy(mPathModel.listFiles())
    }

    override fun getList(): List<String> {
        return mCacheList.toList()
    }

    override fun <IListView> bindView(view: IListView?) {
        mListView = if (view == null) null else view as com.mumu.filebrowser.views.IListView<String>
        mListView?.setEmptyView(R.layout.empty_list)
        if (mListView != null && mListView is View) {
            mContext = (mListView as View).context
        } else {
            mContext = null
        }
    }

    override fun onItemClick(item: String) {
        Log.d("onItemClick", "$mCurrentViewMode : ${item}")
        if (mCurrentViewMode == MODE_NORMAL_VIEW) {
            if (Config.doubleClickOpen()) {
                mListView?.focus(item, true)
            } else {
                if (PathUtils.get(item).isFolder()) {
                    mPathModel.enter(item)
                } else {
                    EventBus.getInstance().post(OpenEvent(item))
                }
            }
            EventBus.getInstance().post(FocusedEvent(item))
        } else if (mCurrentViewMode == MODE_MULTI_SELECT) {
            val selected = isItemSelected(item)
            mListView?.select(item, !selected)
            if (selected) {
                mStateModel.removeSelectedFile(item, true)
            } else {
                mStateModel.addSelectedFile(item, true)
            }
        }
    }

    override fun onItemLongClick(item: String) {
        mCurrentViewMode = MODE_MULTI_SELECT
        onItemClick(item)
    }

    override fun getCurrentLayoutStyle() = mLayoutModel.layoutStyle

    override fun isItemFocused(item: String): Boolean = mStateModel.getSelectedFiles().contains(item)

    override fun isItemSelected(item: String): Boolean = mStateModel.getSelectedFiles().contains(item)

    @Subscribe
    fun onPathChangeEvent(event: PathChangeEvent) {
        Log.d(TAG, "onPathChangeEvent -> ")
        mCacheList = mFileOrdering.sortedCopy(mPathModel.listFiles())
        mStateModel.clean(true)
        mListView?.notifyDataSetChanged()
        EventBus.getInstance().post(FocusedEvent(mPathModel.path))
    }

    @Subscribe
    fun onLayoutChangeEvent(event: LayoutChangeEvent) {
        Log.d(TAG, "onLayoutChangeEvent")
        when (LayoutModel.layoutStyle) {
            ILayoutModel.LAYOUT_STYLE_LIST -> {
                mListView?.showAsList(true)
            }
            ILayoutModel.LAYOUT_STYLE_GRID -> {
                mListView?.showAsGrid(true)
            }
        }
    }

    @Subscribe
    fun onSelectedChangeEvent(event: SelectedEvent) {
        val files = event.files
        Log.d(TAG, "onSelectedFileChange -> ${files?.size ?: 0} changes")
        if (files == null) {
            if(mCurrentViewMode == MODE_NORMAL_VIEW){
                mCurrentViewMode = MODE_MULTI_SELECT
                mStateModel.clean(false)
                mCacheList.forEach {
                    mListView?.select(it,true)
                    mStateModel.addSelectedFile(it,false)
                }
                mStateModel.post()
            }else {
                mCurrentViewMode = MODE_NORMAL_VIEW
                mListView?.select(null, false)
                mStateModel.clean(true)
            }
        }
        if (files?.isEmpty() == true) {
            mCurrentViewMode = MODE_NORMAL_VIEW
            EventBus.getInstance().post(FocusedEvent(mPathModel.path))
        }
    }

    @Subscribe
    fun onContentChangeEvent(event: ContentChangeEvent) {
        when (event.type) {
            INSERT -> {
                Log.d(TAG, "onContentChangeEvent -> INSERT ${event.name}")
                mBufferedQueen.join(INSERT, event.name)
            }
            REMOVE -> {
                Log.d(TAG, "onContentChangeEvent -> REMOVE ${event.name}")
                val index = mCacheList.indexOf(event.name)
                if (index < 0) {
                    return
                }
                mBufferedQueen.join(REMOVE, event.name)
            }
        }
    }

    private fun onInsertOrRemove(list: List<Pair<Int, String>>) {
        if (list.isNotEmpty()) {
            list.forEachIndexed { index, value ->
                run {
                    val type = value.first
                    val path = value.second
                    when (type) {
                        INSERT -> {
                            Log.d(TAG, "onInsertOrRemove -> INSERT $value")
                            var index: Int = mCacheList.size - 1
                            if (index < 0) {
                                index = 0
                            } else {
                                for (i in 0..index) {
                                    if (mFileOrdering.compare(path, mCacheList[i]) < 0) {
                                        index = i
                                        break
                                    }
                                }
                            }
                            mCacheList.add(index, path)
                            //TODO: notify view item added at ${index}
                            mListView?.notifyItemInserted(index)
                        }
                        REMOVE -> {
                            val i = mCacheList.indexOf(path)
                            if (i >= 0) {
                                Log.d(TAG, "onInsertOrRemove -> REMOVE $value")
                                mCacheList.remove(path)
                                //TODO: notify view item added at ${index}
                                mListView?.notifyItemRemoved(i)
                                mStateModel.removeSelectedFile(path, false)
                            } else {
                            }
                        }
                        else -> {
                        }
                    }
                }
            }
        }
        mStateModel.post()
    }

    private object SelectState {
        private val mSelectedState: MutableSet<String> = mutableSetOf<String>()

        fun clean(post: Boolean) {
            mSelectedState.clear()
            if (post) post()
        }

        fun addSelectedFile(file: String, post: Boolean) {
            mSelectedState += file
            if (post) post()
        }

        fun removeSelectedFile(file: String, post: Boolean) {
            if (mSelectedState.contains(file)) {
                mSelectedState -= file
                if (post) post()
            }
        }

        fun getSelectedFiles(): List<String> {
            return mSelectedState.toList()
        }

        fun post() {
            EventBus.getInstance().post(SelectedEvent(getSelectedFiles().toTypedArray()))
        }
    }

    private inner class BufferedQueue {
        private val TIME_DELAY = 200L;
        private val sQueue = Queues.newArrayBlockingQueue<Pair<Int, String>>(10)
        private val sHandler = object : Handler() {
            override fun handleMessage(msg: Message?) {
                if (msg == null) {
                    return
                }
                synchronized(sQueue) {
                    onInsertOrRemove(sQueue.toList())
                    sQueue.clear()
                }
            }
        }

        fun join(type: Int, value: String) {
            sQueue.put(Pair(type, value))
            sHandler.removeCallbacksAndMessages(null)
            sHandler.sendEmptyMessageDelayed(0, TIME_DELAY)
        }
    }
}
