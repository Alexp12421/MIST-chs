package com.example.mist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    public TextView main_text;
    public Button buttonus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_text = (TextView) findViewById(R.id.main_text_id);

        buttonus = (Button)findViewById(R.id.Amodo_Button);


        buttonus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main_text.setText("Shmeck");
            }
        });
    }
}