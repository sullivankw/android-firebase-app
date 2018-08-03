package com.apps.ksullivan.firstfirebaseapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;

import com.apps.ksullivan.firstfirebaseapp.utils.FirebaseUtils;
import com.apps.ksullivan.firstfirebaseapp.model.Gender;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import static com.apps.ksullivan.firstfirebaseapp.MainActivity.IMAGE_ID_RESULT;

public class CreateProfileDialogFragment extends android.support.v4.app.DialogFragment implements View.OnClickListener {
    private Button uploadProfilePicBtn;
    private Switch switchGender;
    private ProfileViewModel viewModel;
    private TextInputEditText name;
    private TextInputEditText age;
    private CheckBox hobbyCycling;
    private CheckBox hobbyHiking;
    private CheckBox hobbyRunning;
    private CheckBox hobbyReading;
    private CheckBox hobbySwimming;
    private CheckBox hobbyKayaking;
    private TextInputLayout nameTextInputLayout;
    private TextInputLayout ageTextInputLayout;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        viewModel = ViewModelProviders.of(getActivity()).get(ProfileViewModel.class);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(v);
        setupViews(v);
        setupListeners();
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //todo validate age is number
                if (name == null || name.length() > 30
                        || age == null || age.length() > 3
                        || viewModel.getImageId() == null) {
                    Toast.makeText(getActivity(), "Can't save profile. Name, age, and profile pic required.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                UploadTask
                        uploadTask = FirebaseUtils.getImageFromStorageReference(viewModel.getImageId())
                        .putBytes(viewModel.getBytes());

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("upload-task", "Upload failed for imageId " + viewModel.getImageId(), e);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // todo should validate only numbers here
                        int intAge = Integer.parseInt(age.getText().toString());
                        viewModel.setAge(intAge);
                        viewModel.setName(name.getText().toString());
                        if (viewModel.getGender() == null) {
                            //means the selector wasn't moved and intending male
                            viewModel.setGender(Gender.Male);
                        }
                        viewModel.setHobbies();
                        viewModel.setProfile();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        System.out.println("Upload is " + progress + "% done");
                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewModel.clearViewModel();
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    private void setupListeners() {
        uploadProfilePicBtn.setOnClickListener(this);
        switchGender.setOnClickListener(this);
        uploadProfilePicBtn.setOnClickListener(this);
        hobbyCycling.setOnClickListener(this);
        hobbyHiking.setOnClickListener(this);
        hobbyRunning.setOnClickListener(this);
        hobbyReading.setOnClickListener(this);
        hobbySwimming.setOnClickListener(this);
        hobbyKayaking.setOnClickListener(this);

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() < 1 || editable.length() > 30) {
                    nameTextInputLayout.setError("Name must be within 1 and 30 chars");
                } else {
                    ageTextInputLayout.setError(null);
                }
            }
        });

        age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < 1 || editable.length() > 3) {
                    ageTextInputLayout.setError("Age must be within 1 and 3 chars");
                } else {
                    ageTextInputLayout.setError(null);
                }

            }
        });
    }

    private void setupViews(View v) {
        uploadProfilePicBtn = (Button) v.findViewById(R.id.uploadProfilePic);
        name = (TextInputEditText) v.findViewById(R.id.inputName);
        age = (TextInputEditText) v.findViewById(R.id.inputAge);
        switchGender = (Switch) v.findViewById(R.id.switchGender);
        hobbyCycling = (CheckBox) v.findViewById(R.id.hobbyCycling);
        hobbyHiking = (CheckBox) v.findViewById(R.id.hobbyHiking);
        hobbyRunning = (CheckBox) v.findViewById(R.id.hobbyRunning);
        hobbyReading = (CheckBox) v.findViewById(R.id.hobbyReading);
        hobbySwimming = (CheckBox) v.findViewById(R.id.hobbySwimming);
        hobbyKayaking = (CheckBox) v.findViewById(R.id.hobbyKayaking);
        ageTextInputLayout = (TextInputLayout) v.findViewById(R.id.ageLayout);
        nameTextInputLayout = (TextInputLayout) v.findViewById(R.id.nameLayout);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.uploadProfilePic:
                if (getActivity() == null) {
                    break;
                }
                askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,678);
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                getActivity().startActivityForResult(galleryIntent, IMAGE_ID_RESULT);
                break;
            case R.id.switchGender:
                if (switchGender.isChecked()) {
                    viewModel.setGender(Gender.Female);
                }
                else {
                    viewModel.setGender(Gender.Male);
                }
                break;
            case R.id.hobbyCycling:
                if (hobbyCycling.isChecked()) {
                    viewModel.setCycler(true);
                } else {
                    viewModel.setCycler(false);
                }
                break;
            case R.id.hobbyHiking:
                if (hobbyHiking.isChecked()) {
                    viewModel.setHiker(true);
                } else {
                    viewModel.setHiker(false);
                }
                break;
            case R.id.hobbyRunning:
                if (hobbyRunning.isChecked()) {
                    viewModel.setRunner(true);
                } else {
                    viewModel.setRunner(false);
                }
                break;
            case R.id.hobbyReading:
                if (hobbyReading.isChecked()) {
                    viewModel.setReader(true);
                } else {
                    viewModel.setReader(false);
                }
                break;
            case R.id.hobbySwimming:
                if (hobbySwimming.isChecked()) {
                    viewModel.setSwimmer(true);
                } else {
                    viewModel.setSwimmer(false);
                }
                break;
            case R.id.hobbyKayaking:
                if (hobbyKayaking.isChecked()) {
                    viewModel.setKayaker(true);
                } else {
                    viewModel.setKayaker(false);
                }
                break;
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (getActivity() == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
        }
    }
}
