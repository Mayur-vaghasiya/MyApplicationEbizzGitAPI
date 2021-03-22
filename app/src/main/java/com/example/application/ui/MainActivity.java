package com.example.application.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.application.R;
import com.example.application.adapter.MyUserListAdapter;
import com.example.application.api.ApiRequestData;
import com.example.application.custom_views.CustomProgressDialog;
import com.example.application.db.AppDatabase;
import com.example.application.model.User;
import com.example.application.retro.RetrofitInstance;
import com.example.application.util.CleanableEditText;
import com.example.application.util.NetworkStatus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Activity activity = null;
    private static final String TAG = "UserListActivity";
    private Toolbar toolbar;
    private LinearLayoutManager layoutManager = null;
    private RecyclerView rvItemList = null;
    private ArrayList<User> userList = null;
    private MyUserListAdapter myUserListAdapter;
    private ApiRequestData service = null;
    private CustomProgressDialog progress = null;
    private CleanableEditText search;

    private AppDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = MainActivity.this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rvItemList = (RecyclerView) findViewById(R.id.recycler_viewUserList);
        search = (CleanableEditText) findViewById(R.id.search);
        setSupportActionBar(toolbar);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_back);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        AppCompatTextView txtHeaderNname = (AppCompatTextView) toolbar.findViewById(R.id.actv_header_name);
        txtHeaderNname.setText(getString(R.string.user_list));

        database = AppDatabase.getDatabaseInstance(this);
        layoutManager = new LinearLayoutManager(activity, layoutManager.VERTICAL, false);
        rvItemList.setLayoutManager(layoutManager);



        rvItemList.addOnItemTouchListener(new MyUserListAdapter.RecyclerTouchListener(getApplicationContext(), rvItemList, new MyUserListAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("LOGIN", userList.get(position).getLogin());
                Intent intent = new Intent(activity, MainActivity2.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
            @Override
            public void onLongClick(View view, int position) {

            }

        }));
    }

    private void getUserList() {
        service = RetrofitInstance.getRetrofitInstance().create(ApiRequestData.class);
        Call<List<User>> call = service.getUserList();
        progress = new CustomProgressDialog(activity).
                setStyle(CustomProgressDialog.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.5f)
                .show();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                Log.e(TAG, response.toString());
                userList =(ArrayList<User>) response.body();
                setRecyclerViewData();
                progress.dismiss();
                Toast.makeText(activity, "Sync Data with Server", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                progress.dismiss();

                Toast.makeText(MainActivity.this, "Sync Fail!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkStatus.getConnectivityStatusString(activity)) {
            getUserList();
        } else {
            Toast.makeText(activity, "No Internet Connection!", Toast.LENGTH_LONG).show();
        }

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                if (null != userList ) {
                    String text = search.getText().toString().toLowerCase(Locale.getDefault());
                    myUserListAdapter.filters(text);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setRecyclerViewData() {
        myUserListAdapter = new MyUserListAdapter(new WeakReference<Context>(activity), userList);
        rvItemList.setAdapter(myUserListAdapter);
    }
}