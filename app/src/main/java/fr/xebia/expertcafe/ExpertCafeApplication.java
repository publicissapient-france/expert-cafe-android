package fr.xebia.expertcafe;

import android.app.Application;

import com.parse.Parse;

public class ExpertCafeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, BuildConfig.PARSE_APPLICATION_ID, BuildConfig.PARSE_CLIENT_KEY);
    }

}
