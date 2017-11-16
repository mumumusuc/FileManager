package com.mumu.filebrowser.file;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mumu.filebrowser.R;
import com.mumu.filebrowser.eventbus.FileUtils;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by leonardo on 17-11-10.
 * this class keeps a real file object and convert it to the IFile interface
 */

public class FileWrapper implements IFile {

    private static final String TAG = FileWrapper.class.getSimpleName();
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private File mFile;
    private String mSuffix;
    @Category.CATEGORY
    private int mCategory = Category.UNKNOWN;
    @State.STATE
    private int mState = State.NONE;
    private Drawable mIcon;

    static final class Category {
        public final static int MUSIC = 0;
        public final static int VIDEO = 1;
        public final static int PICTURE = 2;
        public final static int DOCUMENT = 3;
        public final static int ZIP = 4;
        public final static int APK = 5;
        public final static int UNKNOWN = 999;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({MUSIC, VIDEO, PICTURE, DOCUMENT, ZIP, APK, UNKNOWN})
        public @interface CATEGORY {
        }
    }

    static final class State {
        public final static int NONE = 0;
        public final static int FOCUSED = 0x10;
        public final static int SELECTED = 0x11;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({NONE, FOCUSED, SELECTED})
        public @interface STATE {
        }
    }

    public FileWrapper(@NonNull File file) {
        init(file);
    }

    public FileWrapper(@NonNull String path) {
        checkNotNull(path);
        init(new File(path));
    }

    private void init(@NonNull File file) {
        checkNotNull(file);
        checkArgument(file.exists());
        mFile = file;
    }

    @NonNull
    @Override
    public String getName() {
        return mFile.getName();
    }

    @NonNull
    @Override
    public String getPath() {
        return mFile.getAbsolutePath();
    }

    @Nullable
    @Override
    public String getParent() {
        return mFile.getParent();
    }

    @NonNull
    @Override
    public Drawable getIcon(@Nullable Resources res) {
        checkNotNull(res);
        if (mIcon == null) {
            int id = -1;
            switch (getCategory()) {
                case Category.MUSIC:
                    break;
                case Category.VIDEO:
                    break;
                case Category.PICTURE:
                    break;
                case Category.DOCUMENT:
                    break;
                case Category.ZIP:
                    break;
                case Category.APK:
                    break;
                default:
                    id = isFolder() ? R.drawable.ic_item_floder : R.drawable.ic_item_file;
                    break;
            }
            return mIcon = res.getDrawable(id, null);
        } else {
            return mIcon;
        }
    }

    @Override
    public int getProperty() {
        return 0;
    }

    @Nullable
    @Override
    public String getSuffix() {
        if (mSuffix == null) {
            String name = getName();
            int start = name.lastIndexOf(".");
            if (start >= name.length() - 1) return null;
            return mSuffix = name.substring(start + 1, name.length());
        } else {
            return mSuffix;
        }
    }

    @Override
    public boolean isFolder() {
        return mFile.isDirectory();
    }

    @Override
    public long getSize() {
        return isFolder() ? mFile.list().length : mFile.length();
    }

    @NonNull
    @Override
    public String getLastDate(@Nullable String format) {
        String useformat = format == null ? DEFAULT_DATE_FORMAT : format;
        return new SimpleDateFormat(useformat).format(mFile.lastModified());
    }

    @State.STATE
    public int getState() {
        return mState;
    }

    @Category.CATEGORY
    public int getCategory() {
        return mCategory;
    }

    public void setSelected(boolean selected) {
        mState = selected ? State.SELECTED : mState == State.SELECTED ? State.NONE : mState;
    }

    public boolean isSelected() {
        return mState == State.SELECTED;
    }

    public void setFocused(boolean focused) {
        mState = focused ? State.FOCUSED : mState == State.FOCUSED ? State.NONE : mState;
    }

    public boolean isFocused() {
        return mState == State.FOCUSED;
    }
}
