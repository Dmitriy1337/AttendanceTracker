package com.ui.attracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.ui.attracker.model.EventsList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class NewEventActivity extends AppCompatActivity {

    private static final String TAG = "NewEventActivity";
    private static final String INVALID_EVENT_NAME_MESSAGE = "Custom event name is invalid";
    public static final int QR_SIZE = 1000;
    public static final int MAX_EVENT_NAME_SIZE = 30;
    private static final String COURSE_SPINNER_DEFAULT_ELEMENT = "Choose a course name";
    private static final String SPINNER_NONE_CHOICE = "None";
    private static final String SPINNER_CUSTOM_CHOICE = "Custom";
    private static final String TYPE_SPINNER_DEFAULT_ELEMENT = "Choose an event type";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);


        final Intent viewEvent = new Intent(this, ViewEventActivity.class);
        final EditText customEventNameEditText = findViewById(R.id.customEventNameEditText);
        customEventNameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        final EditText editTextEventNumber = findViewById(R.id.editTextEventNumber);
        final EditText editTextEventDate = findViewById(R.id.editTextEventDate);
        final Button addEventBtn = findViewById(R.id.addEventBtn);


        final Spinner courseNameSpinner = findViewById(R.id.courseNameSpinner);
        final ArrayAdapter<String> courseNameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<String>(Arrays.asList(COURSE_SPINNER_DEFAULT_ELEMENT, SPINNER_CUSTOM_CHOICE)));
        courseNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseNameSpinner.setAdapter(courseNameAdapter);
        APIRequests.getCourses(courseNameAdapter);
        courseNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (courseNameAdapter.getItem(i).equals(SPINNER_CUSTOM_CHOICE)) {
                    customEventNameEditText.setVisibility(View.VISIBLE);
                    customEventNameEditText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
                else {
                    customEventNameEditText.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        final Spinner eventTypeSpinner = findViewById(R.id.eventTypeSpinner);
        final ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<String>(Arrays.asList(TYPE_SPINNER_DEFAULT_ELEMENT, SPINNER_NONE_CHOICE)));
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(eventTypeAdapter);
        APIRequests.getEventTypes(eventTypeAdapter);


        new SetDate(editTextEventDate, this);


        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (courseNameSpinner.getSelectedItem().toString().equals(COURSE_SPINNER_DEFAULT_ELEMENT)) {
                    Snackbar.make(view, COURSE_SPINNER_DEFAULT_ELEMENT, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (eventTypeSpinner.getSelectedItem().toString().equals(TYPE_SPINNER_DEFAULT_ELEMENT)) {
                    Snackbar.make(view, TYPE_SPINNER_DEFAULT_ELEMENT, Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (courseNameSpinner.getSelectedItem().toString().equals(SPINNER_CUSTOM_CHOICE) && !checkCustomNameValidity(customEventNameEditText.getText().toString())) {
                    Snackbar.make(view, INVALID_EVENT_NAME_MESSAGE, Snackbar.LENGTH_LONG).show();
                    return;
                }


                StringBuilder eventName = new StringBuilder();

                if (courseNameSpinner.getSelectedItem().toString().equals(SPINNER_CUSTOM_CHOICE))
                    eventName.append(customEventNameEditText.getText().toString());
                else
                    eventName.append(courseNameSpinner.getSelectedItem().toString());

                if (!eventTypeSpinner.getSelectedItem().toString().equals(SPINNER_NONE_CHOICE))
                    eventName.append(' ').append(eventTypeSpinner.getSelectedItem().toString());

                if (!editTextEventNumber.getText().toString().equals(""))
                    eventName.append(' ').append(editTextEventNumber.getText().toString());

                if (!editTextEventDate.getText().toString().equals(""))
                    eventName.append(' ').append(editTextEventDate.getText().toString());


                if (APIRequests.user.getEvents().contains(eventName.toString())) {
                    Snackbar.make(view, "Event already exists", Snackbar.LENGTH_LONG).show();
                    return;
                }

                APIRequests.addEvent(eventName.toString(), getApplicationContext());
                EventListActivity.eventAdapter.notifyDataSetChanged();

                viewEvent.putExtra("EVENT_NUMBER", EventsList.getEventsList().size()-1);
                startActivity(viewEvent);
                finish();
            }
        });

    }

    private boolean checkCustomNameValidity(String name) {
        return name.matches("^[a-zA-Z0-9\\s]+$") && name.length() < MAX_EVENT_NAME_SIZE;
    }
}



class SetDate implements View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener {

    private EditText editText;
    private Calendar myCalendar;
    private Context context;

    public SetDate(EditText editText, Context ctx){
        this.editText = editText;
        this.editText.setOnFocusChangeListener(this);
        this.context = ctx;
        myCalendar = Calendar.getInstance();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)     {

        String myFormat = "dd.MM.yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        editText.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
            v.clearFocus();
            new DatePickerDialog(this.context, this, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

}