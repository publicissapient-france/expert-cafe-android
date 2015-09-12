package fr.xebia.expertcafe.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import fr.xebia.expertcafe.common.AppConstant;

import static fr.xebia.expertcafe.common.ParseConstant.EMAIL;
import static fr.xebia.expertcafe.common.ParseConstant.EXPERT;
import static fr.xebia.expertcafe.common.ParseConstant.MEETING_TABLE;
import static fr.xebia.expertcafe.common.ParseConstant.NAME;
import static fr.xebia.expertcafe.common.ParseConstant.SUBJECT;
import static fr.xebia.expertcafe.common.ParseConstant.TIME;

@ParseClassName(MEETING_TABLE)
public class Meeting extends ParseObject {

    public void setExpert(Expert expert) {
        put(EXPERT, expert);
    }

    public void setTime(AppConstant.Time time) {
        put(TIME, time.name());
    }

    public AppConstant.Time getTime() {
        return AppConstant.Time.from(getString(TIME));
    }

    public void setAttendeeName(String name) {
        put(NAME, name);
    }

    public void setAttendeeEmail(String email) {
        put(EMAIL, email);
    }

    public void setAttendeeSubject(String subject) {
        put(SUBJECT, subject);
    }

}
