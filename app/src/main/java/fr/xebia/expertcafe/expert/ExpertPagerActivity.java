package fr.xebia.expertcafe.expert;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

import butterknife.Bind;
import fr.xebia.expertcafe.R;
import fr.xebia.expertcafe.common.BaseActivity;
import timber.log.Timber;

public class ExpertPagerActivity extends BaseActivity {

    @Bind(R.id.viewPager) ViewPager pager;
    @Bind(R.id.pagerIndicator) CirclePageIndicator pageIndicator;

    private ExpertPagerAdapter adapter;

    public ExpertPagerActivity() {
        super(R.layout.activity_home, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupPager();
        getExperts();
    }

    private void getExperts() {
        ParseQuery<Expert> query = new ParseQuery<>(Expert.class);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Expert>() {
            @Override
            public void done(List<Expert> experts, ParseException e) {
                if (e == null) {
                    adapter.setExperts(experts);
                } else {
                    Timber.e(e, "Cannot get experts");
                }
            }
        });
    }

    private void setupPager() {
        adapter = new ExpertPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pageIndicator.setViewPager(pager);
    }

}
