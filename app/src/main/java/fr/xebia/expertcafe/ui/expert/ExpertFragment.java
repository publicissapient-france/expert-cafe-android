package fr.xebia.expertcafe.ui.expert;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import fr.xebia.expertcafe.R;
import fr.xebia.expertcafe.common.AppConstant;
import fr.xebia.expertcafe.common.BaseFragment;
import fr.xebia.expertcafe.model.Expert;
import fr.xebia.expertcafe.model.Meeting;
import fr.xebia.expertcafe.transformation.RoundedTransformation;
import timber.log.Timber;

import static fr.xebia.expertcafe.common.ParseConstant.EXPERT;
import static fr.xebia.expertcafe.common.ParseConstant.TIME;

public class ExpertFragment extends BaseFragment {

    public static final String BUNDLE_EXPERT_ID = "BUNDLE_EXPERT_ID";
    public static final RoundedTransformation ROUNDED_TRANSFORMATION = new RoundedTransformation(6, 0);

    @Bind(R.id.descTextView) TextView descTextView;
    @Bind(R.id.picImageView) ImageView picImageView;
    @Bind(R.id.nameEditText) EditText nameEditText;
    @Bind(R.id.emailEditText) EditText emailEditText;
    @Bind(R.id.subjectEditText) EditText subjectEditText;

    @Bind(R.id.ten0Button) Button ten0Button;
    @Bind(R.id.ten1Button) Button ten1Button;
    @Bind(R.id.ten2Button) Button ten2Button;
    @Bind(R.id.ten3Button) Button ten3Button;

    private Expert expert;
    private String expertId;

    private ArrayList<Button> timeButtons = new ArrayList<Button>(4);

    public static Fragment newInstance(String expertId) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_EXPERT_ID, expertId);
        bundle.putInt(BUNDLE_LAYOUT_ID, R.layout.fragment_expert);
        ExpertFragment fragment = new ExpertFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            expertId = bundle.getString(BUNDLE_EXPERT_ID);
        }

        if (expertId != null) {
            ParseQuery<Expert> query = new ParseQuery<>(Expert.class);
            query.fromLocalDatastore();
            query.getInBackground(expertId, new GetCallback<Expert>() {
                @Override
                public void done(Expert localExpert, ParseException e) {
                    if (e == null) {
                        expert = localExpert;
                        bindView();
                    } else {
                        Timber.e(e, "Cannot retrieve expert " + expertId + " from local datastore");
                    }
                }
            });
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ten0Button.setTag(AppConstant.Time.TEN_0);
        timeButtons.add(ten0Button);

        ten1Button.setTag(AppConstant.Time.TEN_1);
        timeButtons.add(ten1Button);

        ten2Button.setTag(AppConstant.Time.TEN_2);
        timeButtons.add(ten2Button);

        ten3Button.setTag(AppConstant.Time.TEN_3);
        timeButtons.add(ten3Button);
    }

    protected void bindView() {
        descTextView.setText(expert.getDescription());
        Picasso.with(getActivity())
                .load(expert.getPicture().getUrl())
                .fit()
                .centerCrop()
                .transform(ROUNDED_TRANSFORMATION)
                .into(picImageView);
        syncTime();
    }

    @SuppressWarnings("unused")
    @OnClick({
            R.id.ten0Button,
            R.id.ten1Button,
            R.id.ten2Button,
            R.id.ten3Button})
    public void onTen0Clicked(Button button) {
        clearTimeSelection();
        button.setSelected(!button.isSelected());
    }

    private void clearTimeSelection() {
        for (Button button : timeButtons) {
            button.setSelected(false);
        }
    }

    private AppConstant.Time getSelectedTime() {
        for (Button button : timeButtons) {
            if (button.isSelected()) {
                return (AppConstant.Time) button.getTag();
            }
        }
        return AppConstant.Time.UNKNOWN;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.submit)
    public void onSubmit() {
        final Meeting meeting = new Meeting();
        meeting.setAttendeeName(nameEditText.getText().toString());
        meeting.setAttendeeEmail(emailEditText.getText().toString());
        meeting.setAttendeeSubject(subjectEditText.getText().toString());
        final AppConstant.Time time = getSelectedTime();
        meeting.setTime(time);
        meeting.setExpert(expert);

        final ParseQuery<Meeting> query = new ParseQuery<>(Meeting.class);
        query.whereEqualTo(EXPERT, expert);
        query.whereEqualTo(TIME, time.name());
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), null, "Checking time availability");
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e == null) {
                    dialog.setMessage("Saving appointment");
                    meeting.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            dialog.dismiss();
                            if (e == null) {
                                Toast.makeText(getActivity(), "Appointment booked, confirmation sent by email", Toast.LENGTH_LONG).show();
                                clearForm();
                                syncTime();
                            } else {
                                Toast.makeText(getActivity(), "Cannot book appointment", Toast.LENGTH_LONG).show();
                                Timber.e(e, "Cannot book appointment");
                            }
                        }
                    });
                } else {
                    dialog.dismiss();
                    Timber.e(e, "Cannot check time availability");
                    Toast.makeText(getActivity(), "Cannot check time availability", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearForm() {
        clearTimeSelection();
        nameEditText.getText().clear();
        emailEditText.getText().clear();
        subjectEditText.getText().clear();
    }

    private void syncTime() {
        ParseQuery<Meeting> query = new ParseQuery<>(Meeting.class);
        query.whereEqualTo(EXPERT, expert);
        query.findInBackground(new FindCallback<Meeting>() {
            @Override
            public void done(List<Meeting> meetings, ParseException e) {
                if (e == null) {
                    for (Button button : timeButtons) {
                        button.setEnabled(!isBookedMeeting(meetings, (AppConstant.Time) button.getTag()));
                    }
                } else {
                    Timber.e(e, "Cannot get booked meeting");
                }
            }
        });
    }

    private boolean isBookedMeeting(List<Meeting> meetings, AppConstant.Time time) {
        for (Meeting meeting : meetings) {
            if (meeting.getTime().equals(time)) {
                return true;
            }
        }
        return false;
    }
}
