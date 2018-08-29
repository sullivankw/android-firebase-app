package com.apps.ksullivan.firstfirebaseapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.ksullivan.firstfirebaseapp.model.ProfileAction;
import com.apps.ksullivan.firstfirebaseapp.model.Gender;
import com.apps.ksullivan.firstfirebaseapp.model.Profile;
import com.apps.ksullivan.firstfirebaseapp.model.Sort;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.apps.ksullivan.firstfirebaseapp.LoginActivity.LOGOUT_ID;
import static com.apps.ksullivan.firstfirebaseapp.LoginActivity.USER_NAME;
import static com.apps.ksullivan.firstfirebaseapp.LoginActivity.USER_ID;
import static com.apps.ksullivan.firstfirebaseapp.ProfileDetailActivity.PROFILE_ACTION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ProfileRecyclerViewAdaptor.ProfileOnItemClickListener {

    private Button addProfileBtn;
    private ProfileViewModel viewModel;
    public static final String PROFILE = "clickedProfile";
    public static final String PROFILE_TO_RETURN = "returnMeProfile";
    public static final int IMAGE_ID_RESULT = 2232;
    public static final int PROFILE_TO_RETURN_ID_RESULT= 3567;
    private RecyclerView recyclerView;
    private ProfileRecyclerViewAdaptor adaptor;
    private List<Profile> profiles;
    private Spinner spinnerSort;

    private Spinner spinnerFiler;
    private Gender genderFilter;
    private Sort sortBy;
    private Query currentQuery;
    private boolean shouldReverseQuery;
    private TextView userTV;
    private Button signoOutBtn;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linear_main);
        setupViews();
        setupRecyclerView();
        addProfileBtn.setOnClickListener(this);
        signoOutBtn.setOnClickListener(this);
        setupSpinnerFilterAndSortingListeners();
        populateSpinners();
        configureViewModel();

        userTV.setText(getIntent().getStringExtra(USER_NAME));
        userId = getIntent().getStringExtra(USER_ID);
        if (userId == null) {
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupQuery(Query query) {
        if (query != null) {
            currentQuery = query;
        } else {
            currentQuery = viewModel.getAllProfiles();
        }

        currentQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profiles = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Profile profile = snapshot.getValue(Profile.class);
                    profiles.add(profile);
                }
                if (shouldReverseQuery) {
                    adaptor.setProfilesForRecView(reverseQueryOrder(profiles));
                } else {
                    adaptor.setProfilesForRecView(profiles);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("data-load", "failed getting data snapshot from db: ", databaseError.toException());
            }
        });
    }

    @Override
    public void onProfileItemClick(Profile profile) {
        Intent i = new Intent(this, ProfileDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(PROFILE, profile);
        i.putExtras(extras);
        startActivityForResult(i, PROFILE_TO_RETURN_ID_RESULT);
    }

    private void setupRecyclerView() {
        adaptor = new ProfileRecyclerViewAdaptor(this);
        recyclerView.setAdapter(adaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void setupViews() {
        addProfileBtn = (Button) findViewById(R.id.addProfileBtn);
        recyclerView = (RecyclerView) findViewById(R.id.recView);
        spinnerSort = (Spinner) findViewById(R.id.sortSpinner);
        spinnerFiler = (Spinner) findViewById(R.id.filterSpinner);
        userTV = (TextView) findViewById(R.id.user);
        signoOutBtn = (Button) findViewById(R.id.logoutBtn);
    }

    private void setupSpinnerFilterAndSortingListeners() {
        spinnerFiler.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String filter = adapterView.getItemAtPosition(i).toString();
                genderFilter = Gender.fromItem(filter);
                if (genderFilter != null) {
                    //means we have a male or female to filter by
                    if (sortBy == null) {
                        shouldReverseQuery = false;
                        viewModel.setCurrentQuery(viewModel.getProfilesByGender(genderFilter));
                    } else {
                        if (sortBy.equals(Sort.Alphabetical)) {
                            shouldReverseQuery = false;
                            viewModel.setCurrentQuery(viewModel.getProfilesOrderedByGenderAndAscendingName(genderFilter));
                        } else if (sortBy.equals(Sort.ReverseAlphabetical)) {
                            shouldReverseQuery = true;
                            viewModel.setCurrentQuery(viewModel.getProfilesOrderedByGenderAndAscendingName(genderFilter));
                        } else if (sortBy.equals(Sort.AgeAscending)) {
                            shouldReverseQuery = false;
                            viewModel.setCurrentQuery(viewModel.getProfilesOrderedByGenderAndAscendingAge(genderFilter));
                        } else if (sortBy.equals(Sort.AgeDescending)) {
                            shouldReverseQuery = true;
                            viewModel.setCurrentQuery(viewModel.getProfilesOrderedByGenderAndAscendingAge(genderFilter));
                        }
                    }
                } else {
                    //still gotta check that sort isn't on
                    if (sortBy == null) {
                        shouldReverseQuery = false;
                        viewModel.setCurrentQuery(viewModel.getAllProfiles());
                    } else {
                        if (sortBy.equals(Sort.AgeDescending) || sortBy.equals(Sort.ReverseAlphabetical)) {
                            shouldReverseQuery = true;
                        } else {
                            shouldReverseQuery = false;
                        }
                        viewModel.setCurrentQuery(viewModel.getSortedProfiles(sortBy));
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sort = adapterView.getItemAtPosition(i).toString();
                sortBy = Sort.fromItem(sort);
                if (sortBy != null) {
                    if (genderFilter == null) {
                        if (sortBy.equals(Sort.AgeDescending) || sortBy.equals(Sort.ReverseAlphabetical)) {
                            shouldReverseQuery = true;
                        } else {
                            shouldReverseQuery = false;
                        }
                        viewModel.setCurrentQuery(viewModel.getSortedProfiles(sortBy));
                    } else {
                        if (sortBy.equals(Sort.Alphabetical)) {
                            shouldReverseQuery = false;
                            viewModel.setCurrentQuery(viewModel.getProfilesOrderedByGenderAndAscendingName(genderFilter));
                        } else if (sortBy.equals(Sort.AgeAscending)) {
                            shouldReverseQuery = false;
                            viewModel.setCurrentQuery(viewModel.getProfilesOrderedByGenderAndAscendingAge(genderFilter));
                        } else if (sortBy.equals(Sort.ReverseAlphabetical)) {
                            shouldReverseQuery = true;
                            viewModel.setCurrentQuery(viewModel.getProfilesOrderedByGenderAndAscendingName(genderFilter));
                        } else if (sortBy.equals(Sort.AgeDescending)) {
                            shouldReverseQuery = true;
                            viewModel.setCurrentQuery(viewModel.getProfilesOrderedByGenderAndAscendingAge(genderFilter));
                        }
                    }
                } else {
                    if (genderFilter == null) {
                        shouldReverseQuery = false;
                        viewModel.setCurrentQuery(viewModel.getAllProfiles());
                    } else {
                        shouldReverseQuery = false;
                        viewModel.setCurrentQuery(viewModel.getProfilesByGender(genderFilter));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void configureViewModel() {
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        viewModel.getProfile().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(@Nullable Profile profile) {
                if (profile != null) {
                    saveNewProfile(profile);
                    viewModel.clearViewModel();
                }
            }
        });
        viewModel.getCurrentQuery().observe(this, new Observer<Query>() {
            @Override
            public void onChanged(@Nullable Query query) {
                if (query != null) {
                    setupQuery(query);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            Log.e("not-found", "no data returned from startResult");
            return;
        }
        if (requestCode == IMAGE_ID_RESULT) {
            if (data.getData() == null) {
                Log.e("not-found", "no image returned to onActivityResult");
                return;
            }
            Uri uri = data.getData();
            final String lastPath = uri.getLastPathSegment();
            viewModel.setImageId(lastPath);
            InputStream imageStream;
            Bitmap selectedImage;
            byte[] dataBytes = null;
            try {
                imageStream = getContentResolver().openInputStream(uri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                dataBytes = baos.toByteArray();
                if (dataBytes == null) {
                    Log.d("not-found", "no image returned to onActivityResult");
                    return;
                }
                viewModel.setBytes(dataBytes);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("invalid-bytes", "cannot convert image for storage", e);
            }
        }

        if (requestCode == PROFILE_TO_RETURN_ID_RESULT) {
            Profile profile = (Profile) data.getSerializableExtra(PROFILE_TO_RETURN);
            String action = data.getStringExtra(PROFILE_ACTION);
            if (action.equals(ProfileAction.DELETE.getItem())) {
                deleteProfile(profile);
            } else if (action.equals(ProfileAction.EDIT.getItem())) {
                updateProfile(profile);
            }
        }
    }

    private void saveNewProfile(Profile profile) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        profile.setCreated(timeStamp);
        String id = viewModel.getDatabaseKey();
        //values needed for ordering in db
        profile.setId(id);
        //values needed for sorting queries
        profile.setGenderAge(profile.getGender().getCode() + "_" + profile.getAge());
        profile.setGenderName(profile.getGender().getCode() + "_" + profile.getName());
        profile.setUserId(userId);
        viewModel.saveProfile(id, profile).addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Profile added", Toast.LENGTH_SHORT).show();

            }
        })
                .addOnFailureListener(MainActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Profile cannot be added", Toast.LENGTH_SHORT).show();
                        removeImageUpload();
                    }
                });
    }

    private void removeImageUpload() {
        if (viewModel.getImageId() == null) {
            Log.d("photo-delete", "no image to delete");
            return;
        }
        viewModel.deleteImageFromStorage(viewModel.getImageId()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("photo-delete", "removed image from failed profile save for id ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("photo-delete", "unable to delete image. Would log to error queue and reprocess in prod app for: " + viewModel.getImageId());

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addProfileBtn:
                CreateProfileDialogFragment fragment = new CreateProfileDialogFragment();
                fragment.show(getSupportFragmentManager(), "dialog");
                break;
            case R.id.logoutBtn:
                Intent i = new Intent();
                setResult(LOGOUT_ID,i);
                finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case 678:
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            Toast.makeText(this, "Profile cannot be created with access to photos", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProfile(Profile profile) {
        if (profile.getImageId() == null) {
            Log.d("photo-delete", "no image to delete");
            return;
        }
        viewModel.deleteImageFromStorage(profile.getImageId()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("photo-delete", "removed image from failed profile save for id ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("photo-delete", "unable to delete image. Would log to error queue and reprocess in prod app for");

            }
        });
        //now delete from db
        viewModel.deleteProfile(profile.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("profile-delete", "unable to delete profile");
            }
        });
    }

    private void updateProfile(Profile profile) {
        viewModel.updateProfile(profile).addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();

            }
        })
                .addOnFailureListener(MainActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Profile cannot be added", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /****
     *
     * Method needed to sort order in reverse. Firebase doesn't allow that order to be returned from database
     */
    private List<Profile> reverseQueryOrder(List<Profile> profiles) {
        List<Profile> reverseProfiles = new ArrayList<>();

        for (int i = profiles.size() - 1; i >= 0; i -- ) {
            reverseProfiles.add(profiles.get(i));
        }
        return reverseProfiles;
    }

    private void populateSpinners() {
        List<String> filters = new ArrayList<String>();
        filters.add("All");
        filters.add("Female");
        filters.add("Male");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filters);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiler.setAdapter(dataAdapter);

        List<String> sorts = new ArrayList<String>();
        sorts.add("Created");
        sorts.add("Alphabetical");
        sorts.add("Reverse Alphabetical");
        sorts.add("Age Ascending");
        sorts.add("Age Descending");

        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sorts);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(dataAdapter);
    }

}
