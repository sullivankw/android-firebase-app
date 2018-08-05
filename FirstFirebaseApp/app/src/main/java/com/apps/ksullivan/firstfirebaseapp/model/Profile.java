package com.apps.ksullivan.firstfirebaseapp.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

@IgnoreExtraProperties
public class Profile implements Serializable {
    private String id;
    private Gender gender;
    private String name;
    private int age;

    private List<String> hobbies;
    private String imageId;
    private String created;
    private String updated;
    private String userId;

    //needed due to firebase query limitations
    private String genderAge;
    private String genderName;

    public Profile() {
    }

    public Profile(String id, Gender gender, String name, int age, List<String> hobbies, String imageId) {
        this.id = id;
        this.gender = gender;
        this.name = name;
        this.age = age;
        this.hobbies = hobbies;
        this.imageId = imageId;
    }

    public Gender getGender() {
        return gender;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public String getImageId() {
        return imageId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public void setGenderAge(String genderAge) {
        this.genderAge = genderAge;
    }

    public void setGenderName(String genderName) {
        this.genderName = genderName;
    }

    public String getUpdated() {
        return updated;
    }

    public String getGenderAge() {
        return genderAge;
    }

    public String getGenderName() {
        return genderName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
