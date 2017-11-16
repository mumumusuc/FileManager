package com.mumu.filebrowser.eventbus;

import android.annotation.NonNull;
import android.util.Log;
import android.util.Singleton;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

/**
 * Created by leonardo on 17-11-9.
 */

public class EventBus implements SubscriberExceptionHandler {

    private static final String TAG = EventBus.class.getSimpleName();

    private static final Singleton<EventBus> sInstance = new Singleton<EventBus>() {
        @Override
        protected EventBus create() {
            return new EventBus();
        }
    };

    public static final EventBus getInstance() {
        return sInstance.get();
    }

    @NonNull
    private com.google.common.eventbus.EventBus mEventBus;

    private EventBus() {
        mEventBus = new com.google.common.eventbus.EventBus(this);
        //mEventBus = new AsyncEventBus(TAG);
    }

    public void register(@NonNull Object subscriber) {
        mEventBus.register(subscriber);
    }

    public void post(@NonNull Object event) {
        mEventBus.post(event);
    }

    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
        Log.e(TAG, Log.getStackTraceString(exception));
    }
}
