package com.example.third;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Normal_Login_Activity extends Activity {

    private Uri fp_pass;
    private ImageButton ib;
    private Button rb;
    private Button cb;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText id;
        final EditText pw;

        id = (EditText) findViewById(R.id.userId);
        pw = (EditText) findViewById(R.id.passWord);
        ib = (ImageButton) findViewById(R.id.user_image);
        rb = (Button) findViewById(R.id.register_button);
        cb = (Button) findViewById(R.id.cancel_button);

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });

        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id_pass = id.getText().toString().trim();
                String pw_pass = pw.getText().toString().trim();

                if(id_pass.isEmpty() || pw_pass.isEmpty()){
                    Intent intent = new Intent();
                    setResult(2, intent);
                    finish();
                }

                Intent intent = new Intent();
                intent.putExtra("id", id_pass);
                intent.putExtra("pw", pw_pass);
                intent.putExtra("fp", fp_pass);

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == 1)
            if (resultCode == RESULT_OK) {
                Uri selectedImage = imageReturnedIntent.getData();
                fp_pass = selectedImage;
                ib.setImageURI(selectedImage);
            }
    }
}
