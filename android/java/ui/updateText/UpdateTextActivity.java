package com.travelfox.ryan.ui.updateText;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import com.travelfox.ryan.R;
import com.travelfox.ryan.base.BaseActivity;

public class UpdateTextActivity extends BaseActivity {

    TextView tvTitle;
    EditText etText;

    String updateTarget;
    String title;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.update_text_activity);
        super.onCreate(savedInstanceState);

        tvTitle = findViewById(R.id.tvTitle);
        etText = findViewById(R.id.etText);
        findViewById(R.id.btnSave).setOnClickListener(view -> {
            String text = etText.getText().toString();
            Intent intent = new Intent();
            intent.putExtra("updateTarget", updateTarget);
            intent.putExtra("text", text);
            setResult(RESULT_OK, intent);
            finish();
        });

        updateTarget = getIntent().getStringExtra("updateTarget");
        title = getIntent().getStringExtra("title");
        text = getIntent().getStringExtra("text");

        if (updateTarget.equals("expense")){
            etText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        tvTitle.setText(title);
        etText.setText(text);
        etText.setHint(text);
    }
}