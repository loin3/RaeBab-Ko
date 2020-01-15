package com.example.third;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StartActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = null;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int GOOGLE_SIGN = 9001;
    private static final int NORMAL_SIGN = 9002;

    private EditText email;
    private EditText password;
    private Button signInButton;
    private Button normalSignUpButton;
    private Button googleSignUpButton;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        sharedPreferences = getSharedPreferences("member", MODE_PRIVATE);

        email = findViewById(R.id.login_page_email);
        password = findViewById(R.id.login_page_password);
        signInButton = findViewById(R.id.login_page_login_button);
        googleSignUpButton = (Button) findViewById(R.id.googleSignUpButton);
        normalSignUpButton = findViewById(R.id.normalSignUpButton);

        // mAuth = FirebaseAuth.getInstance();

        Log.e("INFORMATION", String.valueOf(FirebaseAuth.getInstance().getCurrentUser()));

        /*
        //  This code is used for auto-login
        if (mAuth.getCurrentUser() != null){
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            finish();
        }
         */

        // Configure Google Sign up
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignUp();
            }
        });

        normalSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalSignUp();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        Button nonMemberButton = (Button) findViewById(R.id.nonMemberButton);
        nonMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("member", false);
                editor.commit();
                finish();
            }
        });
    }

    private void signIn(){
        if (email.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty()) {
            if (email.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty())
                Toast.makeText(getApplicationContext(), "Empty email!", Toast.LENGTH_LONG).show();
            else if (!email.getText().toString().trim().isEmpty() && password.getText().toString().trim().isEmpty())
                Toast.makeText(getApplicationContext(), "Empty password!", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "Empty email and password!", Toast.LENGTH_LONG).show();
        }
        else {
            FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString())
                    .addOnCompleteListener(StartActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "signInWithEmail:success");
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                updateUI(user);
                            } else {
                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Please check your email and password again", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
    }

    private void normalSignUp() {
        Intent intent = new Intent(getApplicationContext(), Normal_Login_Activity.class);
        startActivityForResult(intent, NORMAL_SIGN);
    }

    // For Google Login with Firebase
    private void googleSignUp() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        final DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference();
        final StorageReference reference =  FirebaseStorage.getInstance().getReferenceFromUrl("gs://thirdweek-247d7.appspot.com/");

        // Result of "Google" sign up
        if(requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sing In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }

        // Result of normal sign up
        if (requestCode == NORMAL_SIGN) {
            if (resultCode == RESULT_OK) {
                // id = email, pw = password, fp = filepath of user profile image
                final String id = data.getStringExtra("id");
                final String pw = data.getStringExtra("pw");
                final Uri fp = data.getParcelableExtra("fp");

                // Store image in the firestore as image file
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MMDD_HH:mm:ss");
                Date now = new Date();
                final String profile_image_name = formatter.format(now);

                // Profile image is set
                if (fp != null) {
                    reference.child("profile_images").child(profile_image_name).putFile(fp)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    reference.child("profile_images").child(profile_image_name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            mDataBase.child("User").child(id.substring(0, id.indexOf("@"))).child("profile").setValue(uri.toString());
                                            SharedPreferences sharedPreferences = getSharedPreferences("member", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("image", String.valueOf(uri));
                                            Log.d("zxcv", String.valueOf(uri));
                                            editor.putString("userid",id.substring(0, id.indexOf("@")));
                                            editor.commit();
                                        }
                                    });
                                }
                            });
                }

                // Profile image is not set
                else {
                    Log.e("Null image", "NULL!");
                }

                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(id, pw)
                        .addOnCompleteListener(StartActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Registration success.", Toast.LENGTH_SHORT).show();
                                    // Get login user information
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    // Take device token and store it in the realtime database.
                                    user.getIdToken(true)
                                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                    if (task.isSuccessful()){
                                                        // Make and store user profile info in the realtime database
                                                        User_Infomation user_infomation = new User_Infomation(getSharedPreferences("member", MODE_PRIVATE).getString("token", null), "Initialization");
                                                        mDataBase.child("User").child(id.substring(0, id.indexOf("@"))).setValue(user_infomation);
                                                    }
                                                    else {
                                                        Log.e("REGISTRATION_DATABASE", "FAIL");
                                                    }
                                                }
                                            });
                                    updateUI(user);

                                } else {
                                    Log.w("TAG", "signUpWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Registration failed.", Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        });
            }
            else if (resultCode == RESULT_CANCELED){
                // Action when we push "cancel" button in the registration page
            }
            else if (resultCode == 2)
                // Action when we put null text on email or password
                Toast.makeText(StartActivity.this, "NULL INPUT!!", Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            updateUI(user);
                        }
                        else {
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        //Update UI code here
        if (user != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("member", true);
            editor.commit();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
