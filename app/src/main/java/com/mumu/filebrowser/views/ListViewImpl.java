package com.mumu.filebrowser.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Log;
import android.util.Property;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.mumu.filebrowser.R;
import com.mumu.filebrowser.file.FileWrapper;
import com.mumu.filebrowser.file.IFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by leonardo on 17-11-10.
 */

public class ListViewImpl extends RecyclerView implements IListView, View.OnClickListener, View.OnLongClickListener {

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
    private Set<Integer> mSelectedState;


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
        mSelectedState = new HashSet<>();
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
    public int getItemSelectedCount() {
        return mSelectedState.size();
    }

    @Override
    public void setItemSelected(@Nullable View view, int position, boolean selected) {
        int target = -1;
        synchronized (mItemList) {
            if (view != null) {
                Object tag = view.getTag();
                if (tag instanceof IFile) {
                    IFile file = (IFile) tag;
                    target = mItemList.indexOf(file);
                    view.setSelected(selected);
                    SimpleViewHolder holder = (SimpleViewHolder) getChildViewHolder(view);
                    holder.mDrawable.setSelected(selected, true);
                }
            } else {
                target = position;
            }
            if (target >= 0 && target < mAdapter.getItemCount()) {
                Log.d(TAG, "setItemSelected : target = " + target);
                ((FileWrapper) (mItemList.get(target))).setSelected(selected);
                if (selected) {
                    mSelectedState.add(target);
                } else if (mSelectedState.contains(target)) {
                    mSelectedState.remove(target);
                }
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
    public void removeItem(@NonNull Integer... positions) {
        synchronized (mItemList) {
            Set<IFile> temp = new HashSet<>();
            for (int i = positions.length - 1; i >= 0; i--) {
                int index = positions[i] < 0 ? 0 : positions[i] > mItemList.size() ? mItemList.size() : positions[i];
                temp.add(mItemList.get(index));
                if (mSelectedState.contains(index)) {
                    mSelectedState.remove(index);
                }
            }
            for (IFile file : temp) {
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
            Drawable icon = file.getIcon(getResources());
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
