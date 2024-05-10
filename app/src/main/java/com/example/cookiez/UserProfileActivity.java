package com.example.cookiez;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookiez.Adapters.RecipeAdapter;
import com.example.cookiez.Interface.RecipeCallback;
import com.example.cookiez.Model.Recipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {
    private ArrayList<Recipe> recipes;
    private FloatingActionButton UserProfile_FAB_Back;
    private MaterialTextView UserProfile_MTV_UserName;
    private MaterialTextView UserProfile_MTV_RecipesCount;
    private ShapeableImageView UserProfile_IB_Follow;
    private ShapeableImageView UserProfile_SIV_ProfilePicture;
    private MaterialTextView UserProfile_MTV_FollowingCount;
    private MaterialTextView UserProfile_MTV_FollowersCount;
    private RecyclerView UserProfile_RV_MyRecipes;
    private RecipeAdapter recipeAdapter;
    public static final String UserIdStatus = "USER_ID_STATUS";
    private String userID;
    private FirebaseDatabase db;
    private DatabaseReference UsersRef;
    private FirebaseAuth auth;
    private FirebaseUser CurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent PreviousScreen = getIntent();
        userID = PreviousScreen.getStringExtra(UserIdStatus);
        db = FirebaseDatabase.getInstance();
        UsersRef = db.getReference("users");
        recipes = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(this,recipes);
        auth = FirebaseAuth.getInstance();
        CurrentUser = auth.getCurrentUser();
        findViews();
        initViews();
    }
    private void findViews() {
        UserProfile_MTV_UserName = findViewById(R.id.UserProfile_MTV_UserName);
        UserProfile_MTV_RecipesCount = findViewById(R.id.UserProfile_MTV_RecipesCount);
        UserProfile_MTV_FollowingCount = findViewById(R.id.UserProfile_MTV_FollowingCount);
        UserProfile_MTV_FollowersCount = findViewById(R.id.UserProfile_MTV_FollowersCount);
        UserProfile_RV_MyRecipes = findViewById(R.id.UserProfile_RV_MyRecipes);
        UserProfile_FAB_Back = findViewById(R.id.UserProfile_FAB_Back);
        UserProfile_IB_Follow = findViewById(R.id.UserProfile_IB_Follow);
        UserProfile_SIV_ProfilePicture = findViewById(R.id.UserProfile_SIV_ProfilePicture);
    }

    private void initViews() {
        UsersRef.child(CurrentUser.getUid()).child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(userID))
                    UserProfile_IB_Follow.setImageResource(R.drawable.baseline_check_24);
                else
                    UserProfile_IB_Follow.setImageResource(R.drawable.baseline_person_add_24);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        UserProfile_IB_Follow.setOnClickListener(v -> {
            UsersRef.child(CurrentUser.getUid()).child("Following").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChild(userID)) {
                        UsersRef.child(CurrentUser.getUid()).child("Following").child(userID).removeValue();
                        UsersRef.child(userID).child("Followers").child(CurrentUser.getUid()).removeValue();
                    }

                    else {
                        UsersRef.child(CurrentUser.getUid()).child("Following").child(userID).setValue(userID);
                        UsersRef.child(userID).child("Followers").child(CurrentUser.getUid()).setValue(CurrentUser.getUid());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });


        UserProfile_FAB_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        UsersRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile_MTV_UserName.setText(snapshot.child("userName").getValue(String.class));
                UserProfile_MTV_RecipesCount.setText(""+snapshot.child("Recipes").getChildrenCount());
                UserProfile_MTV_FollowingCount.setText("" + snapshot.child("Following").getChildrenCount());
                UserProfile_MTV_FollowersCount.setText("" + snapshot.child("Followers").getChildrenCount());
                PutProfilePicture(snapshot.child("Profile Picture").getValue(String.class));
                if(recipes.size() == 0) {
                    for (DataSnapshot recipeSnapshot : snapshot.child("Recipes").getChildren()) {
                        String name = recipeSnapshot.getKey();
                        String dateUploaded = recipeSnapshot.child("Upload date").getValue(String.class);
                        String timeUploaded = recipeSnapshot.child("Upload time").getValue(String.class);
                        Recipe recipe = new Recipe();
                        recipe.setAuthor(name).setName(name).setIngredients(null).setSteps(null).setDate(dateUploaded).setTime(timeUploaded);
                        recipes.add(recipe);
                    }
                }
                useRecipes(recipes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recipeAdapter.setRecipeCallback(new RecipeCallback() {
            @Override
            public void RecipeClicked(Recipe recipe, int position) {
                ChangeActivityToRecipeActivity(recipe);
            }
        });
    }
    private void useRecipes(ArrayList<Recipe> recipes){
        recipes.sort(new Recipe.RecipeDateComparator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        UserProfile_RV_MyRecipes.setLayoutManager(linearLayoutManager);
        UserProfile_RV_MyRecipes.setAdapter(recipeAdapter);
    }

    private void ChangeActivityToRecipeActivity(Recipe recipe){
        Intent RecipeIntent = new Intent(this, ViewRecipeActivity.class);
        RecipeIntent.putExtra(ViewRecipeActivity.RecipeNameStatus, recipe.getName());
        RecipeIntent.putExtra(ViewRecipeActivity.RecipeAuthorStatus, recipe.getAuthor());
        RecipeIntent.putExtra(ViewRecipeActivity.UUIDstatus, userID);
        startActivity(RecipeIntent);
    }

    private void PutProfilePicture(String ProfilePicture){
        Glide.with(this).load(ProfilePicture).into(UserProfile_SIV_ProfilePicture);
    }
}