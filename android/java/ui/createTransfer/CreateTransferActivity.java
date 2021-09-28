package com.travelfox.ryan.ui.createTransfer;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.travelfox.ryan.R;
import com.travelfox.ryan.base.BaseActivity;
import com.travelfox.ryan.entity.Quote;
import com.travelfox.ryan.utils.StrUtils;
import com.travelfox.ryan.utils.TimeUtils;

import java.util.Calendar;
import java.util.List;

public class CreateTransferActivity extends BaseActivity {

    long planId;
    String market;
    String initCreateAt;

    Button btnQuote;
    EditText etPrice, etSize;
    Button btnCreateAt;

    List<Quote> quoteList;

    // 新增交易Form
    Quote quote;
    String createAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.create_transfer_activity);
        super.onCreate(savedInstanceState);

        planId = getIntent().getLongExtra("planId", -1);
        market = getIntent().getStringExtra("market");
        initCreateAt = getIntent().getStringExtra("createAt");

        if (planId == -1) {
            toast("計畫取得失敗");
            finish();
            return;
        }

        btnQuote = findViewById(R.id.btnQuote);
        etPrice = findViewById(R.id.etPrice);
        etSize = findViewById(R.id.etSize);
        btnCreateAt = findViewById(R.id.btnCreateAt);
        btnQuote.setOnClickListener(view -> selectQuoteDialog());
        btnCreateAt.setOnClickListener(view -> selectDateDialog());

        findViewById(R.id.btnCreate).setOnClickListener(view -> createTransfer(false));
        findViewById(R.id.btnCreateAndNext).setOnClickListener(view -> createTransfer(true));

        // 初始化交易日期
        if (initCreateAt != null) {
            createAt = initCreateAt;
            btnCreateAt.setText(TimeUtils.ISO8601ToDisplayDate(createAt));
            initCreateAt = null;
        }

        getQuoteList();
    }

    private void getQuoteList() {
//        getPlannerService().getPlaceList(null, market, (statusCode, responseBody, throwable) -> {
//            GetPlaceListResponse rsp = (GetPlaceListResponse) handleBasicOnResponse(statusCode, responseBody, throwable, GetPlaceListResponse.class);
//            if (rsp != null) {
//                quoteList = rsp.results;
//                dismissProgressView();
//            }
//        });
    }

    private void createTransfer(boolean createNext) {
//        hideKeyboard();
//        String price = etPrice.getText().toString();
//        String size = etSize.getText().toString();
//        if (quote == null || createAt == null || price.isEmpty() || size.isEmpty()) {
//            toast("請輸入");
//            return;
//        }
//        CreateTransferRequest request = new CreateTransferRequest();
//        request.plan_id = planId;
//        request.quote_id = quote.quote_id;
//        request.price = price;
//        request.size = Integer.parseInt(size);
//        request.fee = "0";
//        request.create_at = createAt;
//        getPlannerService().createTravelPlace(request, (statusCode, responseBody, throwable) -> {
//            CreateTransferResponse rsp = (CreateTransferResponse) handleBasicOnResponse(statusCode, responseBody, throwable, CreateTransferResponse.class);
//            if (rsp != null) {
//                toast("新增交易成功");
//                if (createNext) {
//                    startActivity(
//                            new Intent(CreateTransferActivity.this, CreateTransferActivity.class)
//                                    .putExtra("planId", planId)
//                                    .putExtra("market", market)
//                                    .putExtra("createAt", createAt)
//                    );
//                }
//                finish();
//            }
//        });
    }

    private void selectQuoteDialog() {
        hideKeyboard();
        String[] items = new String[quoteList.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = StrUtils.format("(%s)%s", quoteList.get(i).symbol, quoteList.get(i).name);
        }
        new AlertDialog.Builder(this)
                .setItems(items, (dialogInterface, i) -> {
                    quote = quoteList.get(i);
                    btnQuote.setText(StrUtils.format("(%s)%s", quote.symbol, quote.name));
                })
                .show();
    }

    @SuppressLint("SimpleDateFormat")
    private void selectDateDialog() {
        hideKeyboard();
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (datePicker, y, m, d) ->
        {
            Calendar createAtCalendar = Calendar.getInstance();
            createAtCalendar.set(Calendar.YEAR, y);
            createAtCalendar.set(Calendar.MONTH, m);
            createAtCalendar.set(Calendar.DATE, d);
            createAt = TimeUtils.dateToISO8601(createAtCalendar.getTime());
            btnCreateAt.setText(TimeUtils.dateToDisplayDate(createAtCalendar.getTime()));
        }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE))
                .show();
    }
}