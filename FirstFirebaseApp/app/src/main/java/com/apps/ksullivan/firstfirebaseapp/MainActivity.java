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
import android.widget.Toast;

import com.apps.ksullivan.firstfirebaseapp.utils.FirebaseUtils;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ProfileRecyclerViewAdaptor.ProfileOnItemClickListener {

    private Button addProfileBtn;
    private ProfileViewModel viewModel;
    public static final String PROFILE = "clickedProfile";
    public static final int IMAGE_ID_RESULT = 2232;
    private RecyclerView recyclerView;
    private ProfileRecyclerViewAdaptor adaptor;
    private List<Profile> profiles;
    private Spinner spinnerSort;
    private Spinner spinnerFiler;
    private Gender genderFilter;
    private Sort sortBy;
    private Query currentQuery;
    private boolean shouldReverseQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linear_main);
        setupViews();
        setupRecyclerView();
        setupListeners();
        setupSpinners();
        configureViewModel();
    }

    private void setupQuery(Query query) {
        if (query != null) {
            currentQuery = query;
        } else {
            currentQuery = (FirebaseUtils.getAllProfiles());
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

    private void setupSpinners() {
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

    @Override
    public void onProfileItemClick(Profile profile) {
        Intent i = new Intent(this, ProfileDetailActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(PROFILE, profile);
        i.putExtras(extras);
        startActivity(i);
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

    }

    private void setupListeners() {
        addProfileBtn.setOnClickListener(this);

        spinnerFiler.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String filter = adapterView.getItemAtPosition(i).toString();
                genderFilter = Gender.fromItem(filter);
                if (genderFilter != null) {
                    //means we have a male or female to filter by
                    if (sortBy == null) {
                        shouldReverseQuery = false;
                        viewModel.setCurrentQuery(FirebaseUtils.getProfilesByGender(genderFilter.getItem()));
                    } else {
                        if (sortBy.equals(Sort.Alphabetical)) {
                            shouldReverseQuery = false;
                            viewModel.setCurrentQuery(FirebaseUtils.getProfilesOrderedByGenderAndAscendingName(genderFilter));
                        } else if (sortBy.equals(Sort.ReverseAlphabetical)) {
                            shouldReverseQuery = true;
                            viewModel.setCurrentQuery(FirebaseUtils.getProfilesOrderedByGenderAndAscendingName(genderFilter));
                        } else if (sortBy.equals(Sort.AgeAscending)) {
                            shouldReverseQuery = false;
                            viewModel.setCurrentQuery(FirebaseUtils.getProfilesOrderedByGenderAndAscendingAge(genderFilter));
                        } else if (sortBy.equals(Sort.AgeDescending)) {
                            shouldReverseQuery = true;
                            viewModel.setCurrentQuery(FirebaseUtils.getProfilesOrderedByGenderAndAscendingAge(genderFilter));
                        }
                    }
                } else {
                    //still gotta check that sort isn't on
                    if (sortBy == null) {
                        shouldReverseQuery = false;
                        viewModel.setCurrentQuery(FirebaseUtils.getAllProfiles());
                    } else {
                        if (sortBy.equals(Sort.AgeDescending) || sortBy.equals(Sort.ReverseAlphabetical)) {
                            shouldReverseQuery = true;
                        }
                        shouldReverseQuery = false;
                        viewModel.setCurrentQuery(FirebaseUtils.getSortedProfiles(sortBy));
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
                        viewModel.setCurrentQuery(FirebaseUtils.getSortedProfiles(sortBy));
                    } else {
                        if (sortBy.equals(Sort.Alphabetical)) {
                            shouldReverseQuery = false;
                            viewModel.setCurrentQuery(FirebaseUtils.getProfilesOrderedByGenderAndAscendingName(genderFilter));
                        } else if (sortBy.equals(Sort.AgeAscending)) {
                            shouldReverseQuery = false;
                            viewModel.setCurrentQuery(FirebaseUtils.getProfilesOrderedByGenderAndAscendingAge(genderFilter));
                        } else if (sortBy.equals(Sort.ReverseAlphabetical)) {
                            shouldReverseQuery = true;
                            viewModel.setCurrentQuery(FirebaseUtils.getProfilesOrderedByGenderAndAscendingName(genderFilter));
                        } else if (sortBy.equals(Sort.AgeDescending)) {
                            shouldReverseQuery = true;
                            viewModel.setCurrentQuery(FirebaseUtils.getProfilesOrderedByGenderAndAscendingAge(genderFilter));
                        }
                    }
                } else {
                    if (genderFilter == null) {
                        shouldReverseQuery = false;
                        viewModel.setCurrentQuery(FirebaseUtils.getAllProfiles());
                    } else {
                        shouldReverseQuery = false;
                        viewModel.setCurrentQuery(FirebaseUtils.getProfilesByGender(genderFilter.getItem()));
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
        if (requestCode == IMAGE_ID_RESULT) {
            if (data == null || data.getData() == null) {
                Log.d("not-found", "no image returned to onActivityResult");
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
    }

    private void saveNewProfile(Profile profile) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        profile.setCreated(timeStamp);
        String id = FirebaseUtils.getDatabaseKey();
        //values needed for ordering in db
        profile.setId(id);
        //values needed for sorting queries
        profile.setGenderAge(profile.getGender().getCode() + "_" + profile.getAge());
        profile.setGenderName(profile.getGender().getCode() + "_" + profile.getName());
        FirebaseUtils.saveProfileToDatabase(id, profile).addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>() {
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
        FirebaseUtils.deleteImageFromStorage(viewModel.getImageId()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

}
