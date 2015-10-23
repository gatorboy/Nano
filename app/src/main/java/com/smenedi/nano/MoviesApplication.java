package com.smenedi.nano;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.stetho.Stetho;

import android.app.Application;

/**
 * Created by smenedi on 9/12/15.
 */
public class MoviesApplication extends Application {

    private static MoviesApplication INSTANCE;


    public static synchronized MoviesApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (MoviesApplication.class) {
            INSTANCE = this;
        }

        // Initialize Fresco library
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this).setDownsampleEnabled(true).build();
        Fresco.initialize(this, config);

        if(BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
        }
    }
}
