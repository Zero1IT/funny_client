package by.funnynose.app.events;

import androidx.appcompat.app.AppCompatActivity;
import io.socket.emitter.Emitter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.funnynose.R;
import by.funnynose.app.Utilities;
import by.funnynose.app.constants.Session;
import by.funnynose.app.events.Support.DatePickerDialog;
import by.funnynose.app.events.Support.onGettingFragmentResult;
import by.funnynose.app.network.SocketAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class CreateEventActivity extends AppCompatActivity implements onGettingFragmentResult {

    public static final int IDENTITY = 228;
    public static final String NAME_KEY = "layout_name";

    private static final String DIALOG_TAG = "date_dialog";
    private static final String DATE_TIME_KEY = "dt_key";
    private static final String DEFINE_EVENT = "on_create_new_event";

    private Date mEventDate;
    private AutoCompleteTextView mAutoHour;
    private AutoCompleteTextView mAutoMin;
    private EditText mTitleField;
    private EditText mDescribeField;
    private ProgressBar mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        if (getIntent() != null) {
            setTitle(getIntent().getStringExtra(NAME_KEY));
        }
        mProgressView = findViewById(R.id.progressBar);
        Button eventDate = findViewById(R.id.select_event_date);
        eventDate.setOnClickListener(showDateTimePicker);
        mAutoHour = findViewById(R.id.hour_duration_event);
        mAutoMin = findViewById(R.id.min_duration_event);
        initAutoCompeteHourMin();
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(saveEvent);
        mTitleField = findViewById(R.id.event_title);
        mDescribeField = findViewById(R.id.event_describe);
    }

    private void initAutoCompeteHourMin() {
        Integer[] minutes = new Integer[60];
        for (int i = 0; i < 60; i++)
            minutes[i] = i;
        Integer[] hours = new Integer[24];
        for (int i = 0; i < 24; i++)
            hours[i] = i;
        ArrayAdapter<Integer> hAdapter = new ArrayAdapter<>(this,
                R.layout.activity_create_event, R.id.hour_duration_event);
        ArrayAdapter<Integer> mAdapter = new ArrayAdapter<>(this,
                R.layout.activity_create_event, R.id.min_duration_event);
        mAutoHour.setAdapter(hAdapter);
        mAutoMin.setAdapter(mAdapter);
        mAdapter.addAll(minutes);
        hAdapter.addAll(hours);
    }

    private final View.OnClickListener showDateTimePicker = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerDialog dialog = DatePickerDialog.newInstance(new Date());
            dialog.show(getSupportFragmentManager(), DIALOG_TAG);
        }
    };

    private final View.OnClickListener saveEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String title = mTitleField.getText().toString().trim();
            if (!checkValidMainFields(title)) {
                return;
            }
            String describe = mDescribeField.getText().toString().trim();
            int hours = 0, minutes = 0;
            try {
                hours = Integer.parseInt(mAutoHour.getText().toString().trim());
                minutes = Integer.parseInt(mAutoMin.getText().toString().trim());
            } catch (NumberFormatException e) {
                Log.d(Session.TAG, "if h and m - none");
            }
            int duration = hours * 60 + minutes;
            createEvent(title, describe, duration);
        }
    };

    private void createEvent(String title, String describe, int duration) {
        Event e = new Event();
        e.setTitle(title);
        e.setDate(mEventDate);
        e.setDescribe(describe);
        e.setDurationEvent(duration);
        e.setFinished(false);
        showProgress(true);
        defineEventFromServer(e);
    }

    private void defineEventFromServer(final Event event) {
        SocketAPI.getSocket().emit(DEFINE_EVENT, packEventToServer(event))
                .on(DEFINE_EVENT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                long id = -1;
                try {
                    id = object.getLong("id");
                } catch (JSONException e) {
                    Log.d(Session.TAG, object.toString());
                    Log.d(Session.TAG, e.getMessage());
                }
                if (id == -1) throw new ExceptionInInitializerError("Fatal event error, check it");
                event.setId(id);
                Intent intent = new Intent();
                intent.putExtra(NAME_KEY, event);
                setResult(RESULT_OK, intent);
                showProgress(false);
                finish();
            }
        });
    }

    private JSONObject packEventToServer(Event e) {
        JSONObject object = new JSONObject();
        try {
            object.put("title", e.getTitle());
            object.put("date", e.getDate().getTime());
            object.put("describe", e.getDescribe());
            object.put("duration", e.getDurationEvent());
            object.put("finished", e.isFinished());
            //TODO: refactoring
            int type = getTitle().equals("Другое") ? 0 : (getTitle().equals("Госпиталь") ? 1 : 2);
            object.put("type", type);
        } catch (JSONException err) {
            Log.e(Session.TAG, err.getMessage());
            return null;
        }
        Log.d(Session.TAG, object.toString());
        return object;
    }

    private boolean checkValidMainFields(String title) {
        if (mEventDate == null) {
            Utilities.showSnackbar(getCurrentFocus(), "Дата обязательна для заполнения");
            return false;
        }

        if (title.length() == 0) {
            Utilities.showSnackbar(getCurrentFocus(), "Название обазательно для заполнения");
            return false;
        }

        return true;
    }

    @Override
    public void fragmentResult(Intent intent) {
        if (intent != null)
           mEventDate = (Date) intent.getSerializableExtra(DATE_TIME_KEY);
    }

    private void showProgress(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
                mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                mProgressView.animate().setDuration(shortAnimTime).
                    setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                        }
                    });
            }
        });
    }
}
