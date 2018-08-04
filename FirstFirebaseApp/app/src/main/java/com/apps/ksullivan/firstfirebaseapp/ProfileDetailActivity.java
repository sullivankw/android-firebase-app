package com.apps.ksullivan.firstfirebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.ksullivan.firstfirebaseapp.model.ProfileAction;
import com.apps.ksullivan.firstfirebaseapp.utils.FirebaseDao;
import com.apps.ksullivan.firstfirebaseapp.model.Hobby;
import com.apps.ksullivan.firstfirebaseapp.model.Profile;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.apps.ksullivan.firstfirebaseapp.MainActivity.PROFILE;
import static com.apps.ksullivan.firstfirebaseapp.MainActivity.PROFILE_TO_RETURN;
import static com.apps.ksullivan.firstfirebaseapp.MainActivity.PROFILE_TO_RETURN_ID_RESULT;

public class ProfileDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String PROFILE_SAVED_INSTANCE = "profileSelected";
    public static final String PROFILE_ACTION = "action";
    private TextView nameDetail;
    private TextView ageDetail;
    private ImageView imageView;
    private TextView genderDetail;
    private CheckBox hobbyCycling;
    private CheckBox hobbyHiking;
    private CheckBox hobbyRunning;
    private CheckBox hobbyReading;
    private CheckBox hobbySwimming;
    private CheckBox hobbyKayaking;
    private Button deleteBtn;
    private Button saveEditBtn;
    private Profile profile;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        if (savedInstanceState != null) {
            profile = (Profile) savedInstanceState.getSerializable(PROFILE_SAVED_INSTANCE);
        } else {
            Intent intent = getIntent();
            profile = (Profile) intent.getSerializableExtra(PROFILE);
        }

        setupViews();
        setupListeners();

    }

    private void setupListeners() {
        deleteBtn.setOnClickListener(this);
        saveEditBtn.setOnClickListener(this);
    }

    private void setupViews() {
        deleteBtn = (Button) findViewById(R.id.deletePrflBtn);
        saveEditBtn = (Button) findViewById(R.id.editPrflBtn);

        nameDetail = (TextView) findViewById(R.id.displayNameDetail);
        nameDetail.setText(profile.getName());

        ageDetail = (TextView) findViewById(R.id.displayAgeDetail);
        ageDetail.setText(String.valueOf(profile.getAge()));

        genderDetail = (TextView) findViewById(R.id.displayGenderDetail);
        genderDetail.setText(profile.getGender().getItem());

        imageView = (ImageView) findViewById(R.id.imageProfileDetail);
        Glide.with(imageView.getContext())
                .using(new FirebaseImageLoader())
                //overkill to create a view model only to handle this static ref call
                .load(FirebaseDao.getImageFromStorageReference(profile.getImageId()))
                .into(imageView);

        hobbyCycling = (CheckBox) findViewById(R.id.hobbyCyclingDetail);
        List<String> hobbies = profile.getHobbies();
        if (hobbies.contains(Hobby.Cycling.getItem())) {
        hobbyCycling.setChecked(true);
        }

        hobbyHiking = (CheckBox) findViewById(R.id.hobbyHikingDetail);
        if (hobbies.contains(Hobby.Hiking.getItem())) {
            hobbyHiking.setChecked(true);
        }

        hobbyKayaking = (CheckBox) findViewById(R.id.hobbyKayakingDetail);
        if (hobbies.contains(Hobby.Kayaking.getItem())) {
            hobbyKayaking.setChecked(true);
        }

        hobbyReading = (CheckBox) findViewById(R.id.hobbyReadingDetail);
        if (hobbies.contains(Hobby.Reading.getItem())) {
            hobbyReading.setChecked(true);
        }

        hobbyRunning = (CheckBox) findViewById(R.id.hobbyRunningDetail);
        if (hobbies.contains(Hobby.Running.getItem())) {
            hobbyRunning.setChecked(true);
        }

        hobbySwimming = (CheckBox) findViewById(R.id.hobbySwimmingDetail);
        if (hobbies.contains(Hobby.Swimming.getItem())) {
            hobbySwimming.setChecked(true);
        }

    }

    public void passProfileToMainActivityForOperation(ProfileAction action) {
        Intent i = new Intent(this, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(PROFILE_TO_RETURN, profile);
        extras.putString(PROFILE_ACTION, action.getItem());
        i.putExtras(extras);
        setResult(PROFILE_TO_RETURN_ID_RESULT,i);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.deletePrflBtn:
                passProfileToMainActivityForOperation(ProfileAction.DELETE);
                break;
            case R.id.editPrflBtn:
                updateHobbies();
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
                profile.setUpdated(timeStamp);
                passProfileToMainActivityForOperation(ProfileAction.EDIT);
                break;
        }
    }

    private void updateHobbies() {
        List<String> hobbies = new ArrayList<>();
        if (hobbyCycling.isChecked()) {
            hobbies.add(Hobby.Cycling.getItem());
        }
        if (hobbyHiking.isChecked()) {
            hobbies.add(Hobby.Hiking.getItem());
        }
        if (hobbyKayaking.isChecked()) {
            hobbies.add(Hobby.Kayaking.getItem());
        }
        if (hobbyReading.isChecked()) {
            hobbies.add(Hobby.Reading.getItem());
        }
        if (hobbyRunning.isChecked()) {
            hobbies.add(Hobby.Running.getItem());
        }
        if (hobbySwimming.isChecked()) {
            hobbies.add(Hobby.Swimming.getItem());
        }
        profile.setHobbies(hobbies);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("profileToUpdate", profile);
    }
}
