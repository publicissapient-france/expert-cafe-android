package fr.xebia.expertcafe;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import fr.xebia.expertcafe.model.Expert;
import fr.xebia.expertcafe.model.Meeting;
import timber.log.Timber;

public class ExpertCafeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initializeLog();
        initializeParse();
    }

    private void initializeLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void initializeParse() {
        ParseObject.registerSubclass(Expert.class);
        ParseObject.registerSubclass(Meeting.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, BuildConfig.PARSE_APPLICATION_ID, BuildConfig.PARSE_CLIENT_KEY);
    }

}
