package com.mumu.filebrowser.file;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.FloatProperty;
import android.util.Log;
import android.util.Property;
import android.view.animation.AccelerateInterpolator;

import com.mumu.filebrowser.R;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by leonardo on 17-11-10.
 */

public interface IFile {
    @NonNull
    String getName();

    @NonNull
    String getPath();

    @Nullable
    String getParent();

    @NonNull
    Drawable getIcon(@NonNull Resources res);

    @Nullable
    String getSuffix();

    int getProperty();

    /**
     * @return if this file is a folder
     */
    boolean isFolder();

    /**
     * @return file size int Byte
     */
    long getSize();

    /**
     *
     * @param format time-format,default is "yyyy-MM-dd HH:mm:ss"
     * @return last modified time
     */
    @NonNull
    String getLastDate(@Nullable String format);
}
