package com.magtuz.simplebrowser;

import android.app.Application;
import android.util.Log;

import com.magtuz.simplebrowser.di.AppComponent;
import com.magtuz.simplebrowser.di.DaggerAppComponent;
import com.magtuz.simplebrowser.di.modules.ContextModule;

/**
 * Created by magtuz on 9/28/2017.
 */

public class SBApplication extends Application {
    private final static String LOG_TAG = Application.class.getSimpleName();
    private static AppComponent sAppComponent;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "Application.onCreate - Initializing application...");
        super.onCreate();

        initializeApplication();
        Log.d(LOG_TAG, "Application.onCreate - Application initialized OK");
    }

    private void initializeApplication() {
        sAppComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }
}
