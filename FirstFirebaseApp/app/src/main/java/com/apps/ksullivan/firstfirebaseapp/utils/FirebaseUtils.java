package com.apps.ksullivan.firstfirebaseapp.utils;

import com.apps.ksullivan.firstfirebaseapp.model.Gender;
import com.apps.ksullivan.firstfirebaseapp.model.Profile;
import com.apps.ksullivan.firstfirebaseapp.model.Sort;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtils {
    //Firebase does not sort by desc
    public static final String STORAGE_IMAGE_PATH = "gs://testfirebase-4bf45.appspot.com/images/image-";

    public static DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }
    public Query getSortedProfiles(Sort sort) {
        return FirebaseDatabase.getInstance().getReference(("profiles/")).orderByChild(sort.getColumn());
    }
    public Query getAllProfiles() {
        return FirebaseDatabase.getInstance().getReference(("profiles/")).orderByKey();
    }
    public Query getProfilesByGender(Gender gender) {
        return FirebaseDatabase.getInstance().getReference(("profiles/")).orderByChild("gender").equalTo(gender.getItem());
    }
    public Query getProfilesOrderedByGenderAndAscendingName(Gender gender) {
        return FirebaseDatabase.getInstance().getReference(("profiles/")).orderByChild("genderName")
                .startAt(gender.getCode()).endAt(gender.getCode() + "\uf8ff");
    }
    public Query getProfilesOrderedByGenderAndAscendingAge(Gender gender) {
        return FirebaseDatabase.getInstance().getReference(("profiles/")).orderByChild("genderAge")
                .startAt(gender.getCode()).endAt(gender.getCode() + "\uf8ff");
    }
    public String getDatabaseKey() {
        return FirebaseDatabase.getInstance().getReference().child("profiles").push().getKey();
    }
    public Task<Void> saveProfile(String id, Profile profile) {
        return FirebaseDatabase.getInstance().getReference().child("profiles").child(id).setValue(profile);
    }

    public static StorageReference getStorageReference() {
        return FirebaseStorage.getInstance().getReference();
    }
    public static StorageReference getAllImagesStorageReference() {
        return FirebaseStorage.getInstance().getReference().child(STORAGE_IMAGE_PATH);
    }

    public static StorageReference getImageFromStorageReference(String imageId) {
        return FirebaseStorage.getInstance().getReference().child(STORAGE_IMAGE_PATH + imageId);
    }
    public Task<Void> deleteImageFromStorage(String imageId) {
        return FirebaseStorage.getInstance().getReference().child(STORAGE_IMAGE_PATH + imageId).delete();
    }
    public Task<Void> deleteProfile(String id) {
        return FirebaseDatabase.getInstance().getReference().child("profiles")
                .child(id).removeValue();
    }
    public Task<Void> updateProfile(Profile profile) {
        return FirebaseDatabase.getInstance().getReference().child("profiles").child(profile.getId())
                .setValue(profile);
    }
}
