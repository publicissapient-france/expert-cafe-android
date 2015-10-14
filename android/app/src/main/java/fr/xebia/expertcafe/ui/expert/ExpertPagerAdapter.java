package fr.xebia.expertcafe.ui.expert;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import fr.xebia.expertcafe.model.Expert;
import fr.xebia.expertcafe.ui.LoopViewPager;

public class ExpertPagerAdapter extends FragmentPagerAdapter {

    public final List<Expert> experts;

    public ExpertPagerAdapter(FragmentManager fm, List<Expert> experts) {
        super(fm);
        this.experts = experts;
    }

    @Override
    public int getCount() {
        return experts.size();
    }

    @Override
    public Fragment getItem(int position) {
        return ExpertFragment.newInstance(experts.get(LoopViewPager.toRealPosition(position, experts.size())).getObjectId());
    }

}
