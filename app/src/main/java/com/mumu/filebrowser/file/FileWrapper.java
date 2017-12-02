package com.mumu.filebrowser.file;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.io.Files;
import com.mumu.filebrowser.R;

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
    @Type
    private int mCategory = UNKNOWN;
    private int mIconResource;

    public final static int MUSIC = 0;
    public final static int VIDEO = 1;
    public final static int PICTURE = 2;
    public final static int DOCUMENT = 3;
    public final static int ZIP = 4;
    public final static int APK = 5;
    public final static int UNKNOWN = 999;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MUSIC, VIDEO, PICTURE, DOCUMENT, ZIP, APK, UNKNOWN})
    public @interface Type {
    }

    public static IFile gets(@NonNull String path) {
        return new FileWrapper(path);
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

    @NonNull
    @Override
    public Drawable getIcon(@Nullable Resources res) {
        checkNotNull(res);
        return res.getDrawable(getIconResource(), null).mutate();
    }

    @NonNull
    @Override
    public int getIconResource() {
        if (mIconResource <= 0) {
            switch (getCategory()) {
                case MUSIC:
                    break;
                case VIDEO:
                    break;
                case PICTURE:
                    break;
                case DOCUMENT:
                    break;
                case ZIP:
                    break;
                case APK:
                    break;
                default:
                    mIconResource = isFolder() ? R.drawable.ic_item_floder : R.drawable.ic_item_file;
                    break;
            }
        }
        return mIconResource;
    }

    @Nullable
    @Override
    public String getSuffix() {
        if (mSuffix == null) {
            /*String name = getName();
            int start = name.lastIndexOf(".");
            if (start >= name.length() - 1) return null;
            return mSuffix = name.substring(start + 1, name.length()).toLowerCase();*/
            return mSuffix = Files.getFileExtension(getPath());
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
        return isFolder() ? (mFile.list() == null ? 0 : mFile.list().length) : mFile.length();
    }

    @NonNull
    @Override
    public String getLastDate(@Nullable String format) {
        String useformat = format == null ? DEFAULT_DATE_FORMAT : format;
        return new SimpleDateFormat(useformat).format(mFile.lastModified());
    }

    @Type
    public int getCategory() {
        return mCategory;
    }

    @Override
    public File asFile() {
        return mFile;
    }
}
