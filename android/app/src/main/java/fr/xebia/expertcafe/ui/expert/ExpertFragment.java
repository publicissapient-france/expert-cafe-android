package fr.xebia.expertcafe.ui.expert;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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

    private static final String BUNDLE_EXPERT_ID = "BUNDLE_EXPERT_ID";
    private static final RoundedTransformation ROUNDED_TRANSFORMATION = new RoundedTransformation(6, 0);
    private static final int DELAY_SYNC_TIME = 5_000;
    private static final Handler HANDLER = new Handler();

    @Bind(R.id.descTextView) TextView descTextView;
    @Bind(R.id.picImageView) ImageView picImageView;
    @Bind(R.id.nameEditText) EditText nameEditText;
    @Bind(R.id.emailEditText) EditText emailEditText;
    @Bind(R.id.subjectEditText) EditText subjectEditText;

    @Bind(R.id.ten0Button) Button ten0Button;
    @Bind(R.id.ten1Button) Button ten1Button;
    @Bind(R.id.ten2Button) Button ten2Button;
    @Bind(R.id.ten3Button) Button ten3Button;

    @Bind(R.id.eleven0Button) Button eleven0Button;
    @Bind(R.id.eleven1Button) Button eleven1Button;
    @Bind(R.id.eleven2Button) Button eleven2Button;
    @Bind(R.id.eleven3Button) Button eleven3Button;

    @Bind(R.id.twelve0Button) Button twelve0Button;
    @Bind(R.id.twelve1Button) Button twelve1Button;
    @Bind(R.id.twelve2Button) Button twelve2Button;
    @Bind(R.id.twelve3Button) Button twelve3Button;

    @Bind(R.id.one0Button) Button one0Button;
    @Bind(R.id.one1Button) Button one1Button;

    @Bind(R.id.two0Button) Button two0Button;
    @Bind(R.id.two1Button) Button two1Button;
    @Bind(R.id.two2Button) Button two2Button;
    @Bind(R.id.two3Button) Button two3Button;

    @Bind(R.id.three0Button) Button three0Button;
    @Bind(R.id.three1Button) Button three1Button;
    @Bind(R.id.three2Button) Button three2Button;
    @Bind(R.id.three3Button) Button three3Button;

    @Bind(R.id.four0Button) Button four0Button;
    @Bind(R.id.four1Button) Button four1Button;
    @Bind(R.id.four2Button) Button four2Button;
    @Bind(R.id.four3Button) Button four3Button;

    @Bind(R.id.five0Button) Button five0Button;
    @Bind(R.id.five1Button) Button five1Button;
    @Bind(R.id.five2Button) Button five2Button;
    @Bind(R.id.five3Button) Button five3Button;

    @Bind(R.id.six0Button) Button six0Button;
    @Bind(R.id.six1Button) Button six1Button;

    private Expert expert;
    private String expertId;
    private boolean syncTimeActive = false;
    private final Runnable syncTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (syncTimeActive) {
                syncTime();
                scheduleSyncTime();
            }
        }
    };

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
        ten1Button.setTag(AppConstant.Time.TEN_1);
        ten2Button.setTag(AppConstant.Time.TEN_2);
        ten3Button.setTag(AppConstant.Time.TEN_3);

        eleven0Button.setTag(AppConstant.Time.ELEVEN_0);
        eleven1Button.setTag(AppConstant.Time.ELEVEN_1);
        eleven2Button.setTag(AppConstant.Time.ELEVEN_2);
        eleven3Button.setTag(AppConstant.Time.ELEVEN_3);

        twelve0Button.setTag(AppConstant.Time.TWELVE_0);
        twelve1Button.setTag(AppConstant.Time.TWELVE_1);
        twelve2Button.setTag(AppConstant.Time.TWELVE_2);
        twelve3Button.setTag(AppConstant.Time.TWELVE_3);

        one0Button.setTag(AppConstant.Time.ONE_0);
        one1Button.setTag(AppConstant.Time.ONE_1);

        two0Button.setTag(AppConstant.Time.TWO_0);
        two1Button.setTag(AppConstant.Time.TWO_1);
        two2Button.setTag(AppConstant.Time.TWO_2);
        two3Button.setTag(AppConstant.Time.TWO_3);

        three0Button.setTag(AppConstant.Time.THREE_0);
        three1Button.setTag(AppConstant.Time.THREE_1);
        three2Button.setTag(AppConstant.Time.THREE_2);
        three3Button.setTag(AppConstant.Time.THREE_3);

        four0Button.setTag(AppConstant.Time.FOUR_0);
        four1Button.setTag(AppConstant.Time.FOUR_1);
        four2Button.setTag(AppConstant.Time.FOUR_2);
        four3Button.setTag(AppConstant.Time.FOUR_3);

        five0Button.setTag(AppConstant.Time.FIVE_0);
        five1Button.setTag(AppConstant.Time.FIVE_1);
        five2Button.setTag(AppConstant.Time.FIVE_2);
        five3Button.setTag(AppConstant.Time.FIVE_3);

        six0Button.setTag(AppConstant.Time.SIX_0);
        six1Button.setTag(AppConstant.Time.SIX_1);

        timeButtons.add(ten0Button);
        timeButtons.add(ten1Button);
        timeButtons.add(ten2Button);
        timeButtons.add(ten3Button);

        timeButtons.add(eleven0Button);
        timeButtons.add(eleven1Button);
        timeButtons.add(eleven2Button);
        timeButtons.add(eleven3Button);

        timeButtons.add(twelve0Button);
        timeButtons.add(twelve1Button);
        timeButtons.add(twelve2Button);
        timeButtons.add(twelve3Button);

        timeButtons.add(one0Button);
        timeButtons.add(one1Button);

        timeButtons.add(two0Button);
        timeButtons.add(two1Button);
        timeButtons.add(two2Button);
        timeButtons.add(two3Button);

        timeButtons.add(three0Button);
        timeButtons.add(three1Button);
        timeButtons.add(three2Button);
        timeButtons.add(three3Button);

        timeButtons.add(four0Button);
        timeButtons.add(four1Button);
        timeButtons.add(four2Button);
        timeButtons.add(four3Button);

        timeButtons.add(five0Button);
        timeButtons.add(five1Button);
        timeButtons.add(five2Button);
        timeButtons.add(five3Button);

        timeButtons.add(six0Button);
        timeButtons.add(six1Button);
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
            R.id.ten3Button,
            R.id.eleven0Button,
            R.id.eleven1Button,
            R.id.eleven2Button,
            R.id.eleven3Button,
            R.id.twelve0Button,
            R.id.twelve1Button,
            R.id.twelve2Button,
            R.id.twelve3Button,
            R.id.one0Button,
            R.id.one1Button,
            R.id.two0Button,
            R.id.two1Button,
            R.id.two2Button,
            R.id.two3Button,
            R.id.three0Button,
            R.id.three1Button,
            R.id.three2Button,
            R.id.three3Button,
            R.id.four0Button,
            R.id.four1Button,
            R.id.four2Button,
            R.id.four3Button,
            R.id.five0Button,
            R.id.five1Button,
            R.id.five2Button,
            R.id.five3Button,
            R.id.six0Button,
            R.id.six1Button})
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
        if (!formValid()) {
            alertFormInvalid();
        } else {
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
                        if (count > 0) {
                            dialog.dismiss();
                            syncTime();
                            alertTimeBooked();
                        } else {
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
                        }
                    } else {
                        dialog.dismiss();
                        Timber.e(e, "Cannot check time availability");
                        Toast.makeText(getActivity(), "Cannot check time availability", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void alertFormInvalid() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setMessage("All fields are mandatory, please edit form");
        dialog.setTitle("Alert");
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void alertTimeBooked() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setMessage("Chosen time already booked, please choose another time");
        dialog.setTitle("Alert");
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void clearForm() {
        clearTimeSelection();
        subjectEditText.getText().clear();
        emailEditText.getText().clear();
        nameEditText.getText().clear();
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

    @Override
    public void onPause() {
        super.onPause();
        syncTimeActive = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        syncTimeActive = true;
        scheduleSyncTime();
    }

    public boolean formValid() {
        return !(getSelectedTime() == AppConstant.Time.UNKNOWN
                || TextUtils.isEmpty(nameEditText.getText())
                || TextUtils.isEmpty(emailEditText.getText())
                || TextUtils.isEmpty(subjectEditText.getText()));
    }

    private void scheduleSyncTime() {
        HANDLER.postDelayed(syncTimeRunnable, DELAY_SYNC_TIME);
    }
}
