package com.example.third;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class SettingActivity extends AppCompatActivity {

    private EditText editText;
    private LinearLayout linearLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sharedPreferences = getSharedPreferences("member", MODE_PRIVATE);

        final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);

        final CheckBox checkBox4 = (CheckBox)findViewById(R.id.checkBox4);
        if(sharedPreferences.getBoolean("member", false) == false){
            checkBox4.setEnabled(false);
            checkBox4.setChecked(false);
        }else if(sharedPreferences.getBoolean("member", false) == true){
            checkBox4.setEnabled(true);
            checkBox4.setChecked(false);
        }

        linearLayout = (LinearLayout)findViewById(R.id.linearLayout2);
        editText = new EditText(getApplicationContext());
        editText.setHint("Depiction");
        editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        checkBox4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    linearLayout.addView(editText);
                }else{
                    linearLayout.removeView(editText);
                }
            }
        });

        Button button = (Button)findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int radioId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton)findViewById(radioId);
                FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@"))).child("outfit").setValue(editText.getText().toString());

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("description", editText.getText().toString());
                editor.commit();

                Intent intent1 = new Intent(SettingActivity.this, AlarmService.class);
                intent1.putExtra("distance", radioButton.getText());
                intent1.putExtra("bundle", getIntent().getBundleExtra("bundle"));
                startService(intent1);

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("Notification").setMessage("Alarm is set!");
                builder.setIcon(R.mipmap.mobile_round);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
    }
}
