package fr.xebia.expertcafe;

import android.app.Application;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import fr.xebia.expertcafe.expert.Expert;
import timber.log.Timber;

import static fr.xebia.expertcafe.common.ParseConstant.EXPERT;

public class ExpertCafeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initializeLog();
        initializeParse();
        initializeLocalDatastore();
    }

    private void initializeLocalDatastore() {
        final ParseQuery<Expert> query = new ParseQuery<>(Expert.class);
        query.findInBackground(new FindCallback<Expert>() {
            @Override
            public void done(final List<Expert> experts, ParseException e) {
                if (e == null) {
                    ParseObject.unpinAllInBackground(EXPERT, new DeleteCallback() {
                        public void done(ParseException e) {
                            ParseObject.pinAllInBackground(EXPERT, experts);
                        }
                    });
                } else {
                    Timber.e(e, "Cannot get experts");
                }
            }
        });
    }

    private void initializeLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void initializeParse() {
        ParseObject.registerSubclass(Expert.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, BuildConfig.PARSE_APPLICATION_ID, BuildConfig.PARSE_CLIENT_KEY);
    }

}
