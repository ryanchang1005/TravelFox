package com.travelfox.ryan.ui.createTravel;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.travelfox.ryan.R;
import com.travelfox.ryan.api.response.CreateTravelResponse;
import com.travelfox.ryan.base.BaseActivity;
import com.travelfox.ryan.api.request.CreateTravelRequest;
import com.travelfox.ryan.utils.TimeUtils;

import java.util.Calendar;

public class CreateTravelActivity extends BaseActivity {

    EditText etName;
    Button btnFromTime, btnToTime;
    String fromTimeString, toTimeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.create_travel_activity);
        super.onCreate(savedInstanceState);

        etName = findViewById(R.id.etName);
        btnFromTime = findViewById(R.id.btnFromTime);
        btnFromTime.setOnClickListener(view -> showSelectDateDialog(true));
        btnToTime = findViewById(R.id.btnToTime);
        btnToTime.setOnClickListener(view -> showSelectDateDialog(false));
        findViewById(R.id.btnCreate).setOnClickListener(view -> createTravel());
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

    private void createTravel() {
        hideKeyboard();
        String name = etName.getText().toString();
        if (name.isEmpty() || fromTimeString == null || toTimeString == null) {
            toast("請輸入");
            return;
        }
        CreateTravelRequest request = new CreateTravelRequest();
        request.name = name;
        request.from_time = fromTimeString;
        request.to_time = toTimeString;
        getPlannerService().createTravel(request, (statusCode, responseBody, throwable) -> {
            CreateTravelResponse rsp = (CreateTravelResponse) handleBasicOnResponse(statusCode, responseBody, throwable, CreateTravelResponse.class);
            if (rsp != null) {
                toast("新增旅程成功");
                finish();
            }
        });
    }
}