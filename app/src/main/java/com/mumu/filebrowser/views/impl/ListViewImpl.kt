package com.mumu.filebrowser.views.impl

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.support.annotation.IntDef
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
import android.widget.ImageView
import android.widget.TextView
import com.mumu.filebrowser.R
import com.mumu.filebrowser.file.FileWrapper
import com.mumu.filebrowser.file.IFile
import com.mumu.filebrowser.views.IListView
import presenter.IListPresenter
import presenter.IPresenter
import presenter.impl.ListPresenterImpl

/**
 * Created by leonardo on 17-11-24.
 */
class ListViewImpl : RecyclerView, IListView<IFile>, View.OnClickListener, View.OnLongClickListener {

    companion object {
        private var sListPresenter: IListPresenter<IFile>? = null
    }

    private var mAdapter: ListAdatpter? = null
    private var mLayoutManager: StaggeredGridLayoutManager? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributes: AttributeSet?) : super(context, attributes) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        if (sListPresenter == null) {
            sListPresenter = ListPresenterImpl()
        }
        mAdapter = ListAdatpter()
        adapter = mAdapter
        itemAnimator = DefaultItemAnimator()
        mLayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        layoutManager = mLayoutManager
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
        sListPresenter?.onItemClick(v!!.tag as IFile)
    }

    override fun onLongClick(v: View?): Boolean {
        sListPresenter?.onItemLongClick(v!!.tag as IFile)
        return true
    }

    override fun showAsList(anim: Boolean) {
        mLayoutManager?.spanCount = 1
    }

    override fun showAsGrid(anim: Boolean) {
        mLayoutManager?.spanCount = 5
    }

    override fun notifyDataSetChanged() {
        mAdapter?.notifyDataSetChanged()
    }

    override fun select(vararg items: IFile?) {
        if (items == null || items.size == 0) {
            TODO("disselect all")
        } else {
            items.map {
                val selected = (it as FileWrapper).isSelected
                for (i in 0..(childCount - 1)) {
                    val view = getChildAt(i)
                    view.isSelected = selected
                    val holder = getChildViewHolder(view) as SimpleViewHolder
                    holder.mDrawable?.setSelected(selected, true)
                    break
                }
            }
        }
    }

    override fun focus(item: IFile?) {
        if (item == null) {
            TODO("disselect all")
        } else {
            val focus = (item as FileWrapper).isFocused
            for (i in 0..(childCount - 1)) {
                val view = getChildAt(i)
                if (focus) view.requestFocus() else view.clearFocus()
                break
            }
        }
    }

    /*Adapter*/
    private inner class ListAdatpter : RecyclerView.Adapter<SimpleViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                    if (mLayoutManager?.spanCount == 1) R.layout.item_view_list else R.layout.item_view_grid,
                    parent,
                    false)
            val holder = SimpleViewHolder(view)
            holder.mItem!!.setOnClickListener { this@ListViewImpl.onClick(it) }
            holder.mItem!!.setOnLongClickListener { this@ListViewImpl.onLongClick(it) }
            return holder
        }

        override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
            val file = sListPresenter!!.list.get(position)
            holder.mItemName!!.setText(file!!.getName())
            val icon = DrawableCompat.wrap(file!!.getIcon(resources))
            val selectedDrawable = resources.getDrawable(R.drawable.ic_item_selected, null)
            val drawable = SelectDrawable(
                    arrayOf(icon.mutate(), selectedDrawable.mutate()))
            holder.mDrawable = drawable
            val selected = (file as FileWrapper).isSelected
            drawable.setSelected(selected, false)
            holder.mItem!!.isSelected = selected
            holder.mItem!!.tag = file
        }

        override fun getItemCount(): Int {
            return sListPresenter!!.list.size
        }

        override fun getItemViewType(position: Int): Int {
            return sListPresenter!!.currentLayoutStyle
        }
    }

    internal inner class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mItem: View = itemView.findViewById(R.id.item)
        val mItemName: TextView = itemView.findViewById(R.id.item_name)
        val mItemIcon: ImageView = itemView.findViewById(R.id.item_icon)
        var mDrawable: SelectDrawable? = null
            set(value) {
                checkNotNull(value)
                mDrawable = value
                mItemIcon!!.setImageDrawable(mDrawable)
            }
    }

    internal inner class SelectDrawable constructor(layers: Array<Drawable>) : LayerDrawable(layers) {

        var animValue: Float = 0F
            set(value) {
                var value = value
                if (value < 0) value = 0f
                if (value > 1f) value = 1f
                animValue = value
                getDrawable(0).alpha = ((1f - value) * 255f).toInt()
                getDrawable(1).alpha = (value * 255f).toInt()
                invalidateSelf()
            }

        private val ALPHA_PROPERTY = object : FloatProperty<SelectDrawable>("ALPHA_PROPERTY") {
            override fun get(obj: SelectDrawable) = obj.animValue

            override fun setValue(obj: SelectDrawable, value: Float) {
                obj.animValue = value
            }
        }


        fun setSelected(selected: Boolean, needAnim: Boolean) {
            if (needAnim) {
                val start: Float = if (selected) 0f else 1f
                val anim = ObjectAnimator
                        .ofFloat<SelectDrawable>(this, ALPHA_PROPERTY, start, 1f - start)
                        .setDuration(resources.getInteger(R.integer.item_icon_animation_duration).toLong())
                anim.setInterpolator(AccelerateInterpolator())
                anim.start()
            } else {
                animValue = if (selected) 1f else 0f
            }
        }
    }
}