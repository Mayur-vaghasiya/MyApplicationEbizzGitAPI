package com.example.application.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.application.R;
import com.example.application.api.ApiRequestData;
import com.example.application.custom_views.CustomProgressDialog;
import com.example.application.db.AppDatabase;
import com.example.application.model.User;
import com.example.application.retro.RetrofitInstance;
import com.example.application.util.NetworkStatus;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {
    private Activity activity = null;
    private static final String TAG = "UserDetailsActivity";
    private Toolbar toolbar;
    private LinearLayoutManager layoutManager = null;
    private ApiRequestData service = null;
    private CustomProgressDialog progress = null;
    private String loginId = "";
    private User user;
    private AppCompatImageView imageViewUser;
    private AppCompatTextView textviewUserName,textviewUserFolowers,textviewUserFollowing, textviewUserCompany, textviewUserBlog, textviewsave;
    private AppCompatEditText edittextNotes;
    private AppDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            loginId = bundle.getString("LOGIN");
        }

        activity = MainActivity2.this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, MainActivity.class));
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                finish();
            }
        });
        AppCompatTextView txtHeaderNname = (AppCompatTextView) toolbar.findViewById(R.id.actv_header_name);
        txtHeaderNname.setText(getString(R.string.user_details));
        database = AppDatabase.getDatabaseInstance(this);
        imageViewUser = findViewById(R.id.imageViewUser);
        textviewUserFolowers = findViewById(R.id.textviewUserFolowers);
        textviewUserFollowing = findViewById(R.id.textviewUserFollowing);
        textviewUserName = findViewById(R.id.textviewUserName);
        textviewUserCompany = findViewById(R.id.textviewUserCompany);
        textviewUserBlog = findViewById(R.id.textviewUserBlog);
        textviewsave = findViewById(R.id.textviewSave);
        textviewsave.setOnClickListener(this::onClick);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(activity, MainActivity.class));
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        finish();

    }

    private void getStoreList() {
        service = RetrofitInstance.getRetrofitInstance().create(ApiRequestData.class);

        Call<User> call = service.getUser(loginId);
        progress = new CustomProgressDialog(activity).
                setStyle(CustomProgressDialog.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.5f)
                .show();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                Log.e(TAG, response.toString());
                user = response.body();
                Glide.with(activity)
                        .load(user.getAvatarUrl())
                        .circleCrop()
                        .into(imageViewUser);

                textviewUserFollowing.setText("Name: ".concat(String.valueOf(user.getFollowing())));
                textviewUserFolowers.setText("Name: ".concat(String.valueOf(user.getFollowers())));
                textviewUserName.setText("Name: ".concat(user.getName().trim()));
                textviewUserBlog.setText("Blog: ".concat(user.getBlog().trim()));
                textviewUserCompany.setText("Company: ".concat(user.getCompany().trim()));
                progress.dismiss();
                Toast.makeText(activity, "Sync Data with Server", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progress.dismiss();

                Toast.makeText(MainActivity2.this, "Sync Fail!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (NetworkStatus.getConnectivityStatusString(activity)) {
            getStoreList();
        } else {
            Toast.makeText(activity, "No Internet Connection!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textviewSave:

                break;

        }
    }
}