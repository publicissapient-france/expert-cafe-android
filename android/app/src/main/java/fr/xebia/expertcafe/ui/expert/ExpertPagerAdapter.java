package fr.xebia.expertcafe.ui.expert;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import fr.xebia.expertcafe.model.Expert;

public class ExpertPagerAdapter extends FragmentPagerAdapter {

    public final ArrayList<Expert> experts;

    public ExpertPagerAdapter(FragmentManager fm) {
        super(fm);
        this.experts = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return experts.size();
    }

    public void setExperts(List<Expert> experts) {
        this.experts.clear();
        this.experts.addAll(experts);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        if (position < experts.size()) {
            return ExpertFragment.newInstance(experts.get(position).getObjectId());
        }
        return null;
    }

}
