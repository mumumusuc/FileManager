package com.mumu.filebrowser.views.impl

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.util.FloatProperty
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.mumu.filebrowser.R
import com.mumu.filebrowser.views.IListView
import com.mumu.filebrowser.presenter.IListPresenter
import com.mumu.filebrowser.presenter.IPresenter
import com.mumu.filebrowser.presenter.impl.ListPresenterImpl
import com.mumu.filebrowser.utils.PathUtils

class ListViewImpl : FrameLayout, IListView<String>, View.OnClickListener, View.OnLongClickListener {
    companion object {
        private val sListPresenter: IListPresenter<String> = ListPresenterImpl()
    }

    private var mEmptyView: View? = null
    private var mRecyclerView: RecyclerView? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attributes: AttributeSet?) : super(context, attributes)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    private fun init(view: RecyclerView) {
        mRecyclerView = view
        mRecyclerView?.adapter = ListAdapter()
        mRecyclerView?.itemAnimator = DefaultItemAnimator()
        mRecyclerView?.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        init(findViewById(R.id.main_list))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (sListPresenter as IPresenter).bindView(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        (sListPresenter as IPresenter).bindView(null)
    }

    override fun onClick(v: View?) {
        sListPresenter?.onItemClick(v?.tag as String)
    }

    override fun onLongClick(v: View?): Boolean {
        sListPresenter?.onItemLongClick(v?.tag as String)
        return true
    }

    override fun showAsList(anim: Boolean) {
        (mRecyclerView?.layoutManager as StaggeredGridLayoutManager).spanCount = 1
    }

    override fun showAsGrid(anim: Boolean) {
        (mRecyclerView?.layoutManager as StaggeredGridLayoutManager)?.spanCount = 6
    }

    override fun notifyDataSetChanged() {
        //keep in main-thread
        handler.post { mRecyclerView?.adapter?.notifyDataSetChanged() }
    }

    override fun notifyItemInserted(index: Int) {
        //keep in main-thread
        handler.post { mRecyclerView?.adapter?.notifyItemInserted(index) }
    }

    override fun notifyItemRemoved(index: Int) {
        //keep in main-thread
        handler.post { mRecyclerView?.adapter?.notifyItemRemoved(index) }
    }

    override fun setEmptyView(v: View) {
        //keep in main-thread
        handler.post {
            if (mEmptyView != null) {
                removeViewAt(0)
            }
            mEmptyView = v
            addView(mEmptyView, 0)
        }
    }

    override fun setEmptyView(layout: Int) {
        setEmptyView(View.inflate(context, layout, null))
    }

    override fun select(items: String?, selected: Boolean) {
        if (items == null) {
            for (i in 0..(mRecyclerView!!.childCount - 1)) {
                val view = mRecyclerView!!.getChildAt(i)
                if (!view.isSelected) {
                    continue
                }
                view.isSelected = false
                val holder = mRecyclerView?.getChildViewHolder(view) as SimpleViewHolder
                holder.mDrawable?.setSelected(false, true)
            }
            return
        }
        for (i in 0..(mRecyclerView!!.childCount - 1)) {
            val view = mRecyclerView!!.getChildAt(i)
            if (items == view.tag) {
                view.isSelected = selected
                val holder = mRecyclerView?.getChildViewHolder(view) as SimpleViewHolder
                holder.mDrawable?.setSelected(selected, true)
                break
            }
        }
    }

    override fun focus(item: String, focus: Boolean) {
        for (i in 0..(mRecyclerView!!.childCount - 1)) {
            val view = mRecyclerView!!.getChildAt(i)
            if (focus) view.requestFocus() else view.clearFocus()
            break
        }

    }

    /*Adapter*/
    private inner class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                    if ((mRecyclerView?.layoutManager as StaggeredGridLayoutManager)?.spanCount == 1) R.layout.item_view_list else R.layout.item_view_grid,
                    parent,
                    false)
            val holder = SimpleViewHolder(view)
            holder.mItem!!.setOnClickListener { this@ListViewImpl.onClick(it) }
            holder.mItem!!.setOnLongClickListener { this@ListViewImpl.onLongClick(it) }
            return holder
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is SimpleViewHolder) {
                val name = sListPresenter!!.list.get(position)
                Log.i("onBindViewHolder", "onBindViewHolder -> bind $name")
                val file = PathUtils.get(name)
                holder.mItemName!!.text = file.getName()
                val icon = DrawableCompat.wrap(file.getIcon(resources))
                val selectedDrawable = resources.getDrawable(R.drawable.ic_item_selected, null)
                val drawable = SelectDrawable(
                        arrayOf(icon.mutate(), selectedDrawable.mutate()))
                holder.setDrawable(drawable)
                val selected = sListPresenter.isItemSelected(name)
                drawable.setSelected(selected, false)
                holder.mItem!!.isSelected = selected
                holder.mItem!!.tag = name
            }
        }

        override fun getItemCount(): Int {
            val size = sListPresenter?.list.size
            mEmptyView?.visibility = if (size == 0) View.VISIBLE else View.GONE
            return size
        }

        override fun getItemViewType(position: Int): Int {
            return sListPresenter?.currentLayoutStyle
        }
    }

    internal inner class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mItem: View = itemView.findViewById(R.id.item)
        val mItemName: TextView = itemView.findViewById(R.id.item_name)
        val mItemIcon: ImageView = itemView.findViewById(R.id.item_icon)
        var mDrawable: SelectDrawable? = null
        fun setDrawable(drawable: SelectDrawable) {
            mDrawable = drawable
            mItemIcon.setImageDrawable(mDrawable)
        }
    }

    internal inner class SelectDrawable constructor(layers: Array<Drawable>) : LayerDrawable(layers) {
        var animValue: Float = 0F
            set(value) {
                var v = value
                if (value < 0) v = 0f
                if (value > 1f) v = 1f
                getDrawable(0).alpha = ((1f - v) * 255f).toInt()
                getDrawable(1).alpha = (v * 255f).toInt()
                invalidateSelf()
            }

        private val ALPHA_PROPERTY = object : FloatProperty<SelectDrawable>("ALPHA_PROPERTY") {
            override fun get(obj: SelectDrawable) = obj.animValue

            override fun setValue(obj: SelectDrawable, value: Float) {
                obj.animValue = value
            }
        }

        fun setSelected(selected: Boolean, needAnim: Boolean) = if (needAnim) {
            val start: Float = if (selected) 0f else 1f
            val anim = ObjectAnimator
                    .ofFloat<SelectDrawable>(this, ALPHA_PROPERTY, start, 1f - start)
                    .setDuration(resources.getInteger(R.integer.item_icon_animation_duration).toLong())
            anim.interpolator = AccelerateInterpolator()
            anim.start()
        } else {
            animValue = if (selected) 1f else 0f
        }
    }
}