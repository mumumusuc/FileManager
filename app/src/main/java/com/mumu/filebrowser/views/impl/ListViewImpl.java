package com.mumu.filebrowser.views.impl;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.mumu.filebrowser.R;
import com.mumu.filebrowser.file.FileWrapper;
import com.mumu.filebrowser.file.IFile;
import com.mumu.filebrowser.views.IListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by leonardo on 17-11-10.
 */

public class ListViewImpl extends RecyclerView implements IListView<IFile>, View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = ListViewImpl.class.getSimpleName();

    @Nullable
    private List<IFile> mItemList;
    @NonNull
    private ListAdatpter mAdapter;
    @NonNull
    private int mLayoutStyle = LAYOUT_STYLE_LIST;
    @NonNull
    private StaggeredGridLayoutManager mLayoutManager;
    @NonNull
    private int mMode = MODE_VIEW;
    @Nullable
    private OnItemClickListener mClickListener;
    @Nullable
    private View.OnLongClickListener mLongClickListener;



    public ListViewImpl(Context context) {
        super(context);
        init();
    }

    public ListViewImpl(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ListViewImpl(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mAdapter = new ListAdatpter();
        setAdapter(mAdapter);
        setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        setLayoutManager(mLayoutManager);
    }

    @Override
    public void setList(List<IFile> list) {
        mItemList = list;
    }

    @Nullable
    @Override
    public List<IFile> getList() {
        return mItemList;
    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showAs(int style, boolean animate) {
        if (mLayoutStyle == style) {
            return;
        }
        mLayoutStyle = style;

        switch (style) {
            case LAYOUT_STYLE_LIST:
                mLayoutManager.setSpanCount(1);
                break;
            case LAYOUT_STYLE_GRID:
                mLayoutManager.setSpanCount(5);
                break;
        }

        if (animate) {
            //TODO add some animation here
        }
    }

    @Override
    public void showAsList(boolean anim) {
        showAs(LAYOUT_STYLE_LIST, anim);
    }

    @Override
    public void showAsGrid(boolean anim) {
        showAs(LAYOUT_STYLE_GRID, anim);
    }

    @Override
    public int getCurrentLayoutStyle() {
        return mLayoutStyle;
    }

    @Override
    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public void switchTo(int mode, boolean anim) {
        mMode = mode;
    }

    @Override
    public void switchToViewMode(boolean anim) {
        mMode = MODE_VIEW;
    }

    @Override
    public void switchToMultiSelectMode(boolean anim) {
        mMode = MODE_MULTI_SELECT;
    }

    @Override
    public int getCurrentMode() {
        return mMode;
    }

    @Override
    public void setItemSelected(@Nullable IFile file, boolean selected) {
        checkNotNull(file);
        int target = -1;
        synchronized (mItemList) {
            target = mItemList.indexOf(file);
            View v = null;
            for(int i = getChildCount()-1;i>=0;i--){
                if((v = getChildAt(i)).getTag() == file){
                    v.setSelected(selected);
                    SimpleViewHolder holder = (SimpleViewHolder) getChildViewHolder(v);
                    holder.mDrawable.setSelected(selected, true);
                    break;
                }
            }
            if (target >= 0 && target < mAdapter.getItemCount()) {
                Log.d(TAG, "setItemSelected : target = " + target);
                ((FileWrapper) file).setSelected(selected);
            }
        }
    }

    @Override
    public void selectAll(boolean selected) {
        synchronized (mItemList) {
            for (int i = mAdapter.getItemCount() - 1; i >= 0; i--) {
                ((FileWrapper) (mItemList.get(i))).setSelected(selected);
                if (i < getChildCount()) {
                    getChildAt(i).setSelected(selected);
                }
            }
        }
    }

    @Override
    public void addItem(int positions, @NonNull IFile file) {
        synchronized (mItemList) {
            int index = positions < 0 ? 0 : positions > mItemList.size() ? mItemList.size() : positions;
            mItemList.add(index, file);
            mAdapter.notifyItemInserted(index);
            scrollToPosition(index);
        }
    }

    @Override
    public void removeItem(@NonNull IFile... items) {
        checkNotNull(items);
        synchronized (mItemList) {
            for (IFile file : items) {
                if (mItemList.contains(file)) {
                    int index = mItemList.indexOf(file);
                    mItemList.remove(file);
                    mAdapter.notifyItemRemoved(index);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null) {
            mClickListener.onItemClick(v, (IFile) v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mClickListener != null) {
            mClickListener.onItemLongClick(v, (IFile) v.getTag());
            return true;
        }
        return false;
    }

    public class ListAdatpter extends Adapter<SimpleViewHolder> {

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(
                            mLayoutManager.getSpanCount() == 1 ? R.layout.item_view_list : R.layout.item_view_grid,
                            parent,
                            false);
            SimpleViewHolder holder = new SimpleViewHolder(view);
            holder.mItem.setOnClickListener(ListViewImpl.this::onClick);
            holder.mItem.setOnLongClickListener(ListViewImpl.this::onLongClick);
            return holder;
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {
            IFile file = mItemList.get(position);
            holder.mItemName.setText(file.getName());
            Drawable icon = DrawableCompat.wrap(file.getIcon(getResources()));
            Drawable selectedDrawable = getResources().getDrawable(R.drawable.ic_item_selected, null);
            SelectDrawable drawable = new SelectDrawable(
                    new Drawable[]{
                            icon.mutate(),
                            selectedDrawable.mutate()
                    });
            holder.setIconDrawable(drawable);
            boolean selected = ((FileWrapper) file).isSelected();
            drawable.setSelected(selected, false);
            holder.mItem.setSelected(selected);
            holder.mItem.setTag(file);
        }

        @Override
        public int getItemCount() {
            return mItemList == null ? 0 : mItemList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mLayoutStyle;
        }
    }

    class SimpleViewHolder extends ViewHolder {
        @BindView(R.id.item)
        View mItem;
        @BindView(R.id.item_name)
        TextView mItemName;
        @BindView(R.id.item_icon)
        ImageView mItemIcon;
        SelectDrawable mDrawable;

        public SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            checkNotNull(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setIconDrawable(@NonNull SelectDrawable drawable) {
            checkNotNull(drawable);
            mDrawable = drawable;
            mItemIcon.setImageDrawable(mDrawable);
        }
    }

    class SelectDrawable extends LayerDrawable {

        float animValue;

        private Property<SelectDrawable, Float> ALPHA_PROPERTY = new FloatProperty<SelectDrawable>(TAG) {
            @Override
            public Float get(SelectDrawable object) {
                return object.animValue;
            }

            @Override
            public void setValue(SelectDrawable object, float value) {
                object.setAnimValue(value);
            }
        };

        private SelectDrawable(@NonNull Drawable[] layers) {
            super(layers);
        }

        private void setAnimValue(float value) {
            if (value < 0) value = 0;
            if (value > 1f) value = 1f;
            animValue = value;
            getDrawable(0).setAlpha((int) ((1f - value) * 255f));
            getDrawable(1).setAlpha((int) (value * 255f));
            invalidateSelf();
        }

        public void setSelected(boolean selected, boolean needAnim) {
            if (needAnim) {
                float start = selected ? 0 : 1f;
                ObjectAnimator anim = ObjectAnimator
                        .ofFloat(this, ALPHA_PROPERTY, start, 1f - start)
                        .setDuration(getResources().getInteger(R.integer.item_icon_animation_duration));
                anim.setInterpolator(new AccelerateInterpolator());
                anim.start();
            } else {
                setAnimValue(selected ? 1 : 0);
            }
        }
    }
}
