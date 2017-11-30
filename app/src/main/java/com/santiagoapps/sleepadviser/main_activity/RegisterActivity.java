package com.santiagoapps.sleepadviser.main_activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santiagoapps.sleepadviser.R;
import com.santiagoapps.sleepadviser.class_library.DatabaseHelper;
import com.santiagoapps.sleepadviser.class_library.User;

public class RegisterActivity extends AppCompatActivity {

    private Button btnEnter;
    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPassword,txtPassword2;
    private TextView txtView_Signin;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference database;
    private DatabaseHelper myDb;
    private final static String TAG = "SleepAdviser";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();

    }

    public void init(){
        btnEnter = (Button)findViewById(R.id.btnEnter);
        txtName = (EditText)findViewById(R.id.txtName);
        txtEmail = (EditText)findViewById(R.id.txtEmail);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        txtPassword2 = (EditText)findViewById(R.id.txtPassword2);
        txtView_Signin = (TextView) findViewById(R.id.txtSignin);

        myDb = new DatabaseHelper(this);
        database = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    startActivity(new Intent(RegisterActivity.this, NavigationMain.class));
                }
            }
        };

    }

    protected void onStart(){
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void registerUser(){
        //User Inputs
        final String name = txtName.getText().toString().trim();
        final String email = txtEmail.getText().toString().trim();
        final String password = txtPassword.getText().toString().trim();
        final String password2 = txtPassword2.getText().toString().trim();

        //ProgressBar
        final ProgressDialog mDialog = new ProgressDialog(RegisterActivity.this);
        mDialog.setMessage("Please wait...");
        Log.d(TAG, "Checking log-in credentials");

        //Error handling
        if (TextUtils.isEmpty(name)){
            txtName.setError("This field is required.");
            txtName.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(email)){
            txtEmail.setError("This field is required.");
            txtEmail.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(password)){
            txtPassword.setError("Please enter password");
            txtPassword.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(password2)){
            txtPassword2.setError("Please enter confirm password");
            txtPassword2.requestFocus();
            return;
        }

        if(!password.equalsIgnoreCase(password2)){
            txtPassword.setError("Password doesn't match!");
            txtPassword.requestFocus();
            return;
        }
        //Start progress dialog
        mDialog.show();

        //Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mDialog.dismiss();

                            //save to Database
                            saveToDatabase(name,email,password);

                        }else{
                            Log.e(TAG,"REGISTRATION FAILED");
                            mDialog.dismiss();
                        }
                    }
                });
    }

    public void saveToDatabase(String name, String email, String password){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        User _user;

        //firebase is connected
        if (user != null) {
            _user = new User(user.getUid(), email, password, name);
            Log.d(TAG, "Current user is: " + user.getUid());
        }

        else{
            _user = new User(email, password, name);
            Log.d(TAG, "User is null");
        }

        database.child(user.getUid()).setValue(_user);

        //save to local database
        long id = myDb.registerUser(_user);

        if(id!=-1){
            Log.d(TAG,"Data inserted: " + _user);
            startActivity(new Intent(RegisterActivity.this, NavigationMain.class));
        } else {
            Log.e(TAG,"Error inserting data");
        }


        //TODO: Sleep data table
        //id(parent) -> sleep_id(parent) -> details(child)
    }

    public void onClick_signin(View v){
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void onClick_btnEnter(View v){
        registerUser();
    }


}
