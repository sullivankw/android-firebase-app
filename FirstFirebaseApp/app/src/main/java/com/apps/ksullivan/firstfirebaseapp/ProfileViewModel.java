package com.apps.ksullivan.firstfirebaseapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.apps.ksullivan.firstfirebaseapp.model.Gender;
import com.apps.ksullivan.firstfirebaseapp.model.Hobby;
import com.apps.ksullivan.firstfirebaseapp.model.Profile;
import com.apps.ksullivan.firstfirebaseapp.model.Sort;
import com.apps.ksullivan.firstfirebaseapp.utils.FirebaseUtils;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends ViewModel {
    private String id;
    private String imageId;
    private Gender gender;
    private String name;
    private int age;
    private List<String> hobbies;
    private MutableLiveData<Profile> profile;
    private boolean isHiker;
    private boolean isRunner;
    private boolean isSwimmer;
    private boolean isReader;
    private boolean isKayaker;
    private boolean isCycler;
    private byte[] bytes;
    private MutableLiveData<Query> currentQuery;
    private FirebaseUtils firebaseUtils;
    //live date is just for observers

    public LiveData<Profile> getProfile() {
        if (profile == null) {
            profile = new MutableLiveData<>();
            profile.setValue(null);
        }
        return profile;
    }

    public void setProfile() {
        if (imageId != null && gender != null && name != null && age != 0 && !CollectionUtils.isEmpty(hobbies))  {
            profile.setValue(new Profile(id, gender, name, age, hobbies, imageId));
        }
    }
    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getHobbies() {
        if (CollectionUtils.isEmpty(hobbies)) {
            return new ArrayList<>();
        }
        return hobbies;
    }

    public void setHobbies() {
        List<String> hobbies = new ArrayList<>();
        if (isHiker) {
            hobbies.add(Hobby.Hiking.getItem());
        }
        if (isCycler) {
            hobbies.add(Hobby.Cycling.getItem());
        }
        if (isKayaker) {
            hobbies.add(Hobby.Kayaking.getItem());
        }
        if (isReader) {
            hobbies.add(Hobby.Reading.getItem());
        }
        if (isRunner) {
            hobbies.add(Hobby.Running.getItem());
        }
        if (isSwimmer) {
            hobbies.add(Hobby.Swimming.getItem());
        }

        this.hobbies = hobbies;
    }

    public void clearHobbies() {
        this.hobbies = null;
    }

    public void clearViewModel() {
        clearHobbies();
        setGender(null);
        setName(null);
        setAge(0);
        setImageId(null);
        setBytes(null);
        setId(null);

    }

    public boolean isHiker() {
        return isHiker;
    }

    public void setHiker(boolean hiker) {
        isHiker = hiker;
    }

    public boolean isRunner() {
        return isRunner;
    }

    public void setRunner(boolean runner) {
        isRunner = runner;
    }

    public boolean isSwimmer() {
        return isSwimmer;
    }

    public void setSwimmer(boolean swimmer) {
        isSwimmer = swimmer;
    }

    public boolean isReader() {
        return isReader;
    }

    public void setReader(boolean reader) {
        isReader = reader;
    }

    public boolean isKayaker() {
        return isKayaker;
    }

    public void setKayaker(boolean kayaker) {
        isKayaker = kayaker;
    }

    public boolean isCycler() {
        return isCycler;
    }

    public void setCycler(boolean cycler) {
        isCycler = cycler;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LiveData<Query> getCurrentQuery() {
        if (currentQuery == null) {
            currentQuery = new MutableLiveData<>();
            currentQuery.setValue(null);
        }
        return currentQuery;
    }

    public void setCurrentQuery(Query currentQuery) {
        this.currentQuery.setValue(currentQuery);
    }

    /////////////Firebase DAO Methods//////////////

    public FirebaseUtils getFirebaseUtils() {
        if (firebaseUtils == null) {
            firebaseUtils = new FirebaseUtils();
        }
        return firebaseUtils;
    }

    public Query getAllProfiles() {
        return getFirebaseUtils().getAllProfiles();
    }

    public Query getProfilesByGender(Gender gender) {
        return getFirebaseUtils().getProfilesByGender(gender);
    }

    public Query getProfilesOrderedByGenderAndAscendingName(Gender gender) {
        return getFirebaseUtils().getProfilesOrderedByGenderAndAscendingName(gender);
    }

    public Query getProfilesOrderedByGenderAndAscendingAge(Gender gender) {
        return getFirebaseUtils().getProfilesOrderedByGenderAndAscendingAge(gender);
    }

    public Query getSortedProfiles(Sort sort) {
        return getFirebaseUtils().getSortedProfiles(sort);
    }

    public Task<Void> saveProfile(String id, Profile profile) {
        return getFirebaseUtils().saveProfile(id, profile);
    }

    public Task<Void> deleteImageFromStorage(String imageId) {
        return getFirebaseUtils().deleteImageFromStorage(imageId);
    }
    public Task<Void> updateProfile(Profile profile) {
        return getFirebaseUtils().updateProfile(profile);
    }
    public Task<Void> deleteProfile(String id) {
        return getFirebaseUtils().deleteProfile(id);
    }

    public String getDatabaseKey() {
        return getFirebaseUtils().getDatabaseKey();
    }

}
