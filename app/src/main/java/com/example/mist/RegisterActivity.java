package com.example.mist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

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

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(username,email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this,"User has been registered successfully!",Toast.LENGTH_LONG).show();

                                                //redirect to login!
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            }else{
                                                Toast.makeText(RegisterActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(RegisterActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
}