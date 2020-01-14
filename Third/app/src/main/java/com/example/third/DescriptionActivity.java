package com.example.third;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        TextView textView = (TextView)findViewById(R.id.textView4);

        Intent intent = getIntent();
        textView.setText(intent.getExtras().getString("description"));

    }
}
