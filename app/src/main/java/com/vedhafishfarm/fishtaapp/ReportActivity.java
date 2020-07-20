package com.vedhafishfarm.fishtaapp;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.vedhafishfarm.fishtaapp.Fragments.HomeFragment;
import com.vedhafishfarm.fishtaapp.Fragments.ProfileFragment;

public class ReportActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;

    private Button report_Button;
    String mEmail = "fishtahelpdesk@gmail.com";

    private TextView username, description;
    private EditText report_description;
    public ImageView image_profile, post_image;

    private String SpinnerSubject;

    //String mMessage = " Publisher Name: " + mPublisherName + "Post Id: " + mPostId + "Post Description: " + mPostDescription + mImageUrl ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        setStatusBarColor(findViewById(R.id.statusBarBackground),getResources().getColor(android.R.color.white));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final Spinner s = findViewById(R.id.report_subject);

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerSubject = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        report_Button = findViewById(R.id.report_Button);

        report_description = findViewById(R.id.report_description);



        username = findViewById(R.id.username);
        post_image = findViewById(R.id.post_image);
        image_profile = findViewById(R.id.image_profile);
        description = findViewById(R.id.description);

        Intent intent = getIntent();
        //Setting Publisher Name
        username.setText(intent.getStringExtra("publisherName"));

        //Setting Publisher Logo
        Glide.with(this).load(intent.getStringExtra("imageURL"))
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(post_image);

        //Setting Post Image
        Glide.with(this).load(intent.getStringExtra("userProfileImage"))
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(image_profile);

        //Setting Description
        description.setText(intent.getStringExtra("description"));



        //Adding click listener
        report_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

    }

    private void sendEmail() {


        Intent intent = getIntent();
        String ReportedBy = intent.getStringExtra("ReportedBy");

        String mPublisherName = intent.getStringExtra("publisherName");
        String mPublisherLogo = intent.getStringExtra("userProfileImage");
        String mPostId = intent.getStringExtra("id");
        String mPostDescription = intent.getStringExtra("description");
        String mPostImage = intent.getStringExtra("imageURL");
        String mReportExlanation = report_description.getText().toString();

        String mMessage = "Report Description: " + mReportExlanation +"\n\n"+ "ReportedBy: "+ ReportedBy + "\n\n" + "Publisher Name: " + mPublisherName +"\n\n" + "Publisher Logo Url: "+ mPublisherLogo + "\n\n" + "Post Id: " + mPostId +"\n\n" + "Post Description: " + mPostDescription + "\n\n" +mPostImage ;
        //Getting content for email
        String email = mEmail.trim();
        String subject = SpinnerSubject.trim();
        String message = mMessage.trim();

        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message);

        //Executing sendmail to send email
        sm.execute();


    }

    public void setStatusBarColor(View statusBar, int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            statusBar.setBackgroundColor(color);

        }
    }


}