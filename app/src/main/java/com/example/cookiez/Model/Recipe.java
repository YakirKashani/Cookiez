package com.example.cookiez.Model;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class Recipe {
    private String name;
    private String AuthorUid;
    private String RecipePicture;
    private String AuthorPicture;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<String> steps;
    private String Author;
    private String date;
    private String time;
    private long likes;
    private ArrayList<String> UsersLiked;

    public Recipe(){

    }

    public String getName() {
        return name;
    }

    public Recipe setName(String name) {
        this.name = name;
        return this;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public Recipe setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    public ArrayList<String> getSteps() {
        return steps;
    }

    public Recipe setSteps(ArrayList<String> steps) {
        this.steps = steps;
        return this;
    }

    public Recipe setAuthor(String Author)
    {
        this.Author = Author;
        return this;
    }
    public String getAuthor(){
        return Author;
    }

    public String getDate() {
        return date;
    }

    public Recipe setDate(String date) {
        this.date = date;
        return this;
    }

    public String getTime() {
        return time;
    }

    public Recipe setTime(String time) {
        this.time = time;
        return this;
    }

    public long getLikes() {
        return likes;
    }

    public Recipe setLikes(long likes) {
        this.likes = likes;
        return this;
    }

    public String getAuthorUid() {
        return AuthorUid;
    }

    public Recipe setAuthorUid(String authorUid) {
        AuthorUid = authorUid;
        return this;
    }

    public ArrayList<String> getUsersLiked() {
        return UsersLiked;
    }

    public Recipe setUsersLiked(ArrayList<String> usersLiked) {
        UsersLiked = usersLiked;
        return this;
    }

    public String getRecipePicture() {
        return RecipePicture;
    }

    public Recipe setRecipePicture(String recipePicture) {
        RecipePicture = recipePicture;
        return this;
    }

    public String getAuthorPicture() {
        return AuthorPicture;
    }

    public Recipe setAuthorPicture(String authorPicture) {
        AuthorPicture = authorPicture;
        return this;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", AuthorUid='" + AuthorUid + '\'' +
                ", RecipePicture='" + RecipePicture + '\'' +
                ", AuthorPicture='" + AuthorPicture + '\'' +
                ", ingredients=" + ingredients +
                ", steps=" + steps +
                ", Author='" + Author + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", likes=" + likes +
                ", UsersLiked=" + UsersLiked +
                '}';
    }

    public static class RecipeDateComparator implements Comparator<Recipe>{

        @Override
        public int compare(Recipe r1, Recipe r2) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
            try{
                Date date1 = dateFormat.parse(r1.getDate() + " " + r1.getTime());
                Date date2 = dateFormat.parse(r2.getDate() + " " + r2.getTime());
                return date2.compareTo(date1);
            }
            catch (ParseException e){
                Log.d("date compare", r1.date + " compared to " + r2.date);
            }
            return 0;
        }
    }
}
