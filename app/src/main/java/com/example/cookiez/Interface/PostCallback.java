package com.example.cookiez.Interface;

import com.example.cookiez.Model.Recipe;

public interface PostCallback {
    void likeButtonClicked(Recipe recipe, int position);
}
