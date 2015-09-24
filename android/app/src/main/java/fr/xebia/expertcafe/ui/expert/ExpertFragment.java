package fr.xebia.expertcafe.ui.expert;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
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
    @Bind(R.id.timesGroup) ViewGroup timesGroup;
    @Bind(R.id.domainTextView) TextView domainText;

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearTimeSelection();
            v.setSelected(!v.isSelected());
        }
    };

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

        if (expert != null) {
            bindView();
        }
    }

    protected void bindView() {
        domainText.setText(expert.getDomain());
        descTextView.setText(expert.getDescription());
        Picasso.with(getActivity())
                .load(expert.getPicture().getUrl())
                .fit()
                .centerCrop()
                .transform(ROUNDED_TRANSFORMATION)
                .into(picImageView);
        bindTime();
        syncTime();
    }

    private void bindTime() {
        LinearLayout linearLayout = null;
        Button button;
        for (int i = 0; i < expert.getTimes().size(); i++) {
            if (i % 4 == 0) {
                linearLayout = new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                timesGroup.addView(linearLayout);
            }
            button = new Button(new ContextThemeWrapper(getActivity(), R.style.AppTheme_TimeButton), null, 0);
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.MATCH_PARENT, 1);
            button.setLayoutParams(params);
            button.setPadding(0, 15, 0, 15);
            button.setGravity(Gravity.CENTER);
            final AppConstant.Time timeName = expert.getTimes().get(i);
            button.setText(getStringByName(timeName.name()));
            button.setTag(timeName);
            button.setOnClickListener(onClickListener);
            if (linearLayout != null) {
                linearLayout.addView(button);
            }
            timeButtons.add(button);
        }
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
