package com.example.notes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class devContactActivity extends AppCompatActivity {

    ImageView github, linkedin, gmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_dev_contact);

        github = findViewById(R.id.github);
        linkedin = findViewById(R.id.linkedin);
        gmail = findViewById(R.id.gmail);

        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gitHub = new Intent(Intent.ACTION_VIEW);
                gitHub.setData(Uri.parse("https://github.com/prince02765"));
                startActivity(gitHub);
            }
        });

        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LinkedIn = new Intent(Intent.ACTION_VIEW);
                LinkedIn.setData(Uri.parse("https://www.linkedin.com/in/prince-patel-02765"));
                startActivity(LinkedIn);
            }
        });

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Gmail = new Intent(Intent.ACTION_VIEW);
                Gmail.setData(Uri.parse("mailto:20DCE088@charusat.edu.in?subject=Need%20help!&amp;body=Dear%20developer%3C%3E"));
                startActivity(Gmail);
            }
        });

    }
}