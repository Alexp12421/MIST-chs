package com.example.mist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView banner,registerButton;
    private EditText editUsername, editPassword, editConfirmPassword, editEmail;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(R.id.signup);
        banner.setOnClickListener(this);

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);

        editEmail = (EditText) findViewById(R.id.email);
        editUsername = (EditText) findViewById(R.id.username);
        editPassword = (EditText) findViewById(R.id.password);
        editConfirmPassword = (EditText) findViewById(R.id.confirmPassword);



    }

    @Override
    public void onClick(View view) {
            switch (view.getId()){
                case R.id.signup:
                    startActivity(new Intent(this,MainActivity.class));
                    break;

                case R.id.registerButton:
                    registerUser();
                    break;
            }
    }

    private void registerUser() {
        String email = editEmail.getText().toString().trim();
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        if(username.isEmpty())
        {
            editUsername.setError("An username is required");
            editUsername.requestFocus();
            return;
        }


        if(password.isEmpty() || confirmPassword.isEmpty()){
            if(password.isEmpty()){
                editPassword.setError("Insert a password!");
                editPassword.requestFocus();
                return;
            }else if(confirmPassword.isEmpty()){
                editConfirmPassword.setError("Insert the password again!");
                editConfirmPassword.requestFocus();
                return;
            }
        }else if(password.equals(confirmPassword) == false){
            editConfirmPassword.setError("The two passwords are not matching!");
            editConfirmPassword.requestFocus();
            return;
        }else if(password.length() < 6){
            editPassword.setError("Insert a valid password!");
            editConfirmPassword.setError("Insert a valid password!");
            editPassword.requestFocus();
            editConfirmPassword.requestFocus();
            return;
        }

        if(email.isEmpty()){
            editEmail.setError("Email is required");
            editEmail.requestFocus();
            return;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Insert a valid email!");
            editEmail.requestFocus();
            return;
        }




    }
}