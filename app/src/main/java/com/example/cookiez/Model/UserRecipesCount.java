package com.example.cookiez.Model;

import androidx.annotation.NonNull;

public class UserRecipesCount {
    private String UserId;
    private String UserProfilePicture;
    private String UserName;
    private long RecipesCount;

    public UserRecipesCount(){

    }

    public String getUserName() {
        return UserName;
    }

    public UserRecipesCount setUserName(String userName) {
        UserName = userName;
        return this;
    }

    public long getRecipesCount() {
        return RecipesCount;
    }

    public UserRecipesCount setRecipesCount(long recipesCount) {
        RecipesCount = recipesCount;
        return this;
    }

    public String getUserId() {
        return UserId;
    }

    public UserRecipesCount setUserId(String userId) {
        UserId = userId;
        return this;
    }

    public String getUserProfilePicture() {
        return UserProfilePicture;
    }

    public UserRecipesCount setUserProfilePicture(String userProfilePicture) {
        UserProfilePicture = userProfilePicture;
        return this;
    }

    @Override
    public String toString() {
        return "UserRecipesCount{" +
                "UserId='" + UserId + '\'' +
                ", UserProfilePicture='" + UserProfilePicture + '\'' +
                ", UserName='" + UserName + '\'' +
                ", RecipesCount=" + RecipesCount +
                '}';
    }
}
