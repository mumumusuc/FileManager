package com.mumu.filebrowser.views.impl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Splitter;
import com.mumu.filebrowser.R;
import com.mumu.filebrowser.eventbus.EventBus;
import com.mumu.filebrowser.eventbus.FileUtils;
import com.mumu.filebrowser.eventbus.events.OpenEvent;
import com.mumu.filebrowser.views.IPathView;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by leonardo on 17-11-13.
 */

public class PathViewImpl extends LinearLayout implements IPathView {

    private static final String TAG = PathViewImpl.class.getSimpleName();
    private static final String SPAN_IMG = "img";

    private List<String> mPath;
    private SpannableStringBuilder mSSB;
    private TextView mText;
    private String mAlias;

    public PathViewImpl(Context context) {
        super(context);
        init();
    }

    public PathViewImpl(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        mSSB = new SpannableStringBuilder();
        mText = inflateTextView(null);
        addView(mText);
        mText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void showPath(@NonNull String path, @NonNull String alias) {
        checkNotNull(path);
        checkNotNull(alias);
        mAlias = alias;
        //if (mPath.equals(path)) return;
        if (!path.startsWith("/")) return;
        Log.i(TAG, "showPath -> " + path);
        //mText.setText(path);-
        mText.setText(addClickablePart(path), TextView.BufferType.SPANNABLE);
    }

    private TextView inflateTextView(String text) {
        TextView view = (TextView) inflate(getContext(), R.layout.path_item_view, null);
        view.setText(text == null ? "" : text);
        return view;
    }

    private SpannableStringBuilder addClickablePart(String str) {
        mSSB.clear();
        mSSB.clearSpans();
        String subs = FileUtils.Companion.getNavigationPath(mAlias);
        Log.d(TAG, str + ", " + subs);
        str = str.substring(subs.length(), str.length());
        mPath = Splitter.on('/').omitEmptyStrings().splitToList(str);
        if (mPath == null)
            return null;
        String aliasName = getResources().getString(FileUtils.Companion.getNavigationName(mAlias));
        mSSB.append(aliasName);
        mSSB.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        String target;
                        EventBus.getInstance().post(new OpenEvent(null, mAlias, false));
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(getResources().getColor(R.color.pathArrowColor));
                        ds.setUnderlineText(false);
                    }
                }, 0, aliasName.length(), 0);
        for (int i = 0, size = mPath.size(); i < size; i++) {
            final int start = mSSB.length();
            final String part = mPath.get(i);
            mSSB.append(SPAN_IMG).append(part);
            ImageSpan arrow = new CenterImageSpan(getContext(), R.drawable.ic_path_arrow, CenterImageSpan.ALIGN_CENTER);
            mSSB.setSpan(arrow, start, start + SPAN_IMG.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            final List<String> sub = mPath.subList(0, i + 1);
            mSSB.setSpan(
                    new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            String target = buildFullPath(sub);
                            Toast.makeText(getContext(), target, Toast.LENGTH_SHORT).show();
                            EventBus.getInstance().post(new OpenEvent(target, mAlias, false));
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(getResources().getColor(R.color.pathArrowColor));
                            ds.setUnderlineText(false);
                        }
                    }, start + SPAN_IMG.length(), start + SPAN_IMG.length() + part.length(), 0
            );
        }
        return mSSB;
    }

    private String buildFullPath(List<String> pathlist) {
        if (pathlist == null)
            return null;
        mSSB.clear();
        mSSB.append(FileUtils.Companion.getNavigationPath(mAlias));
        for (String str : pathlist) {
            mSSB.append('/').append(str);
        }
        return mSSB.toString();
    }

    final class CenterImageSpan extends ImageSpan {

        public static final int ALIGN_CENTER = 2;

        public CenterImageSpan(Context context, int resourceId, int verticalAlignment) {
            super(context, resourceId, verticalAlignment);
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            if (mVerticalAlignment == ALIGN_BASELINE || mVerticalAlignment == ALIGN_BOTTOM) {
                super.draw(canvas, text, start, end, x, top, y, bottom, paint);
            } else {
                Drawable b = getDrawable();
                canvas.save();
                Paint.FontMetricsInt fm = paint.getFontMetricsInt();
                int transY = y + (fm.descent + fm.ascent) / 2 - b.getBounds().bottom / 2;
                canvas.translate(x, transY);
                b.draw(canvas);
                canvas.restore();
            }
        }
    }
}
