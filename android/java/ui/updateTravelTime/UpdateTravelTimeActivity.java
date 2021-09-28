package com.travelfox.ryan.ui.updateTravelTime;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.travelfox.ryan.R;
import com.travelfox.ryan.base.BaseActivity;
import com.travelfox.ryan.utils.TimeUtils;

import java.util.Calendar;

public class UpdateTravelTimeActivity extends BaseActivity {

    Button btnFromTime, btnToTime;
    String fromTimeString, toTimeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.update_travel_time_activity);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        fromTimeString = intent.getStringExtra("fromTime");
        toTimeString = intent.getStringExtra("toTime");

        btnFromTime = findViewById(R.id.btnFromTime);
        btnFromTime.setOnClickListener(view -> showSelectDateDialog(true));
        btnToTime = findViewById(R.id.btnToTime);
        btnToTime.setOnClickListener(view -> showSelectDateDialog(false));
        findViewById(R.id.btnConfirm).setOnClickListener(view -> {
            Intent intentResult = new Intent();
            intentResult.putExtra("fromTime", fromTimeString);
            intentResult.putExtra("toTime", toTimeString);
            setResult(RESULT_OK, intentResult);
            finish();
        });

        btnFromTime.setText(TimeUtils.ISO8601ToDisplayDate(fromTimeString));
        btnToTime.setText(TimeUtils.ISO8601ToDisplayDate(toTimeString));
    }

    private void showSelectDateDialog(boolean isFromTime) {
        hideKeyboard();
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (datePicker, y, m, d) ->
        {
            Calendar createAtCalendar = Calendar.getInstance();
            createAtCalendar.set(Calendar.YEAR, y);
            createAtCalendar.set(Calendar.MONTH, m);
            createAtCalendar.set(Calendar.DATE, d);
            if (isFromTime) {
                fromTimeString = TimeUtils.dateToISO8601(createAtCalendar.getTime());
                btnFromTime.setText(TimeUtils.dateToDisplayDate(createAtCalendar.getTime()));
            } else {
                toTimeString = TimeUtils.dateToISO8601(createAtCalendar.getTime());
                btnToTime.setText(TimeUtils.dateToDisplayDate(createAtCalendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE))
                .show();
    }
}