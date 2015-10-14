package fr.xebia.expertcafe.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import fr.xebia.expertcafe.common.AppConstant;
import fr.xebia.expertcafe.common.ParseConstant;
import timber.log.Timber;

import static fr.xebia.expertcafe.common.ParseConstant.DOMAIN;
import static fr.xebia.expertcafe.common.ParseConstant.EXPERT_TABLE;
import static fr.xebia.expertcafe.common.ParseConstant.TIMES;

@ParseClassName(EXPERT_TABLE)
public class Expert extends ParseObject {

    private List<AppConstant.Time> times;

    public List<AppConstant.Time> getTimes() {
        if (times != null) {
            return times;
        }
        times = new ArrayList<>();
        final JSONArray array = getJSONArray(TIMES);
        AppConstant.Time time;
        for (int i = 0; i < array.length(); i++) {
            try {
                time = AppConstant.Time.from((String) array.get(i));
                if (time != AppConstant.Time.UNKNOWN) {
                    times.add(time);
                }
            } catch (JSONException e) {
                Timber.e(e, "Cannot get item in array");
            }
        }
        return times;
    }

    public String getDomain() {
        return getString(DOMAIN).trim();
    }

    public String getDescription() {
        return getString(ParseConstant.DESCRIPTION);
    }

    public ParseFile getPicture() {
        return getParseFile(ParseConstant.PICTURE);
    }

    public String getName() {
        return getString(ParseConstant.NAME);
    }
}
