package com.example.movie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivityDefense extends AppCompatActivity {

    // For laboratory defense task
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_defense);

        Button button = (Button) findViewById(R.id.buttonBack);
        final EditText editText = findViewById(R.id.textBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                Intent intent = new Intent(MainActivityDefense.this, MainActivity.class);
                intent.putExtra("TEXT", text);

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


}