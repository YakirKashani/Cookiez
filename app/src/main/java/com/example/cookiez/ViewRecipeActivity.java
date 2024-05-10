package com.example.cookiez;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
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
import com.example.cookiez.Adapters.RecipeViewIngredientsAdapter;
import com.example.cookiez.Adapters.RecipeViewStepsAdapter;
import com.example.cookiez.Model.Ingredient;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ViewRecipeActivity extends AppCompatActivity {

    public static final String RecipeNameStatus = "Recipe_Name_Status";
    public static final String RecipeAuthorStatus = "RecipeAuthorStatus";
    public static final String UUIDstatus = "UUID_STATUS";
    private String UUID;
    private String RecipeName;
    private String RecipeAuthor;
    private MaterialTextView RecipeView_MTV_name;
    private MaterialTextView RecipeView_MTV_author;
    private RecyclerView RecipeView_RV_ingredients;
    private RecyclerView RecipeView_RV_steps;
    private FloatingActionButton RecipeView_FAB_Back;
    private MaterialTextView RecipeView_MTV_UploadTime;
    private ShapeableImageView RecipeView_SIV_Like;
    private ShapeableImageView RecipeView_SIV_CookIt;
    private ShapeableImageView RecipeView_SIV_RecipePicture;
    private FirebaseDatabase db;
    private DatabaseReference UsersRef;
    private RecipeViewIngredientsAdapter recipeViewIngredientsAdapter;
    private RecipeViewStepsAdapter recipeViewStepsAdapter;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<String> steps;
    private FirebaseAuth auth;
    private FirebaseUser CurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_recipe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent previousScreen = getIntent();
        RecipeName = previousScreen.getStringExtra(RecipeNameStatus);
        RecipeAuthor = previousScreen.getStringExtra(RecipeAuthorStatus);
        UUID = previousScreen.getStringExtra(UUIDstatus);
        ingredients = new ArrayList<>();
        steps = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        CurrentUser = auth.getCurrentUser();
        findView();
        initViews();
    }
    private void findView() {
        RecipeView_MTV_name = findViewById(R.id.RecipeView_MTV_name);
        RecipeView_RV_ingredients = findViewById(R.id.RecipeView_RV_ingredients);
        RecipeView_RV_steps = findViewById(R.id.RecipeView_RV_steps);
        RecipeView_MTV_author = findViewById(R.id.RecipeView_MTV_author);
        RecipeView_FAB_Back = findViewById(R.id.RecipeView_FAB_Back);
        RecipeView_MTV_UploadTime= findViewById(R.id.RecipeView_MTV_UploadTime);
        RecipeView_SIV_Like = findViewById(R.id.RecipeView_SIV_Like);
        RecipeView_SIV_CookIt = findViewById(R.id.RecipeView_SIV_CookIt);
        RecipeView_SIV_RecipePicture = findViewById(R.id.RecipeView_SIV_RecipePicture);
    }

    private void initViews() {

        db = FirebaseDatabase.getInstance();
        UsersRef = db.getReference("users");

        RecipeView_FAB_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        RecipeView_SIV_Like.setOnClickListener(v -> {
            UsersRef.child(UUID).child("Recipes").child(RecipeName).child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(CurrentUser.getUid()))
                        UsersRef.child(UUID).child("Recipes").child(RecipeName).child("Likes").child(CurrentUser.getUid()).removeValue();
                    else
                        UsersRef.child(UUID).child("Recipes").child(RecipeName).child("Likes").child(CurrentUser.getUid()).setValue(CurrentUser.getUid());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });


        UsersRef.child(UUID).child("Recipes").child(RecipeName).child("Likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(CurrentUser.getUid()))
                    RecipeView_SIV_Like.setImageResource(R.drawable.full_heart);
                else
                    RecipeView_SIV_Like.setImageResource(R.drawable.empty_heart);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        UsersRef.child(UUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Recipe recipe = new Recipe();
                String date = snapshot.child("Recipes").child(RecipeName).child("Upload date").getValue(String.class);
                String time = snapshot.child("Recipes").child(RecipeName).child("Upload time").getValue(String.class);
                String RecipePicture = snapshot.child("Recipes").child(RecipeName).child("Recipe Picture").getValue(String.class);

     //           ArrayList<Ingredient> ingredients = new ArrayList<>();
      //          ArrayList<String> steps = new ArrayList<>();
                for(DataSnapshot ingredientSnapshot : snapshot.child("Recipes").child(RecipeName).child("Ingredients").getChildren()) {
                    String name = ingredientSnapshot.child("name").getValue(String.class);
                    String amount = ingredientSnapshot.child("amount").getValue(String.class);
                    String units = ingredientSnapshot.child("units").getValue(String.class);
                    Ingredient ingredient = new Ingredient();
                    ingredient.setName(name).setAmount(amount).setUnit(units);
                    ingredients.add(ingredient);
                }
                for(DataSnapshot stepSnapshot : snapshot.child("Recipes").child(RecipeName).child("Steps").getChildren()){
                    String step = stepSnapshot.getValue(String.class);
                    steps.add(step);
                }
                recipe.setAuthor(RecipeAuthor).setIngredients(ingredients).setName(RecipeName).setSteps(steps).setDate(date).setTime(time).setRecipePicture(RecipePicture);
                useRecipe(recipe);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        RecipeView_SIV_CookIt.setOnClickListener(v -> {
            changeActivityToCookActivity();
        });
    }

    public void useRecipe(Recipe recipe){
        RecipeView_MTV_UploadTime.setText("Uploaded at " + recipe.getDate() + " " + recipe.getTime());
        Glide.with(this).load(recipe.getRecipePicture()).into(RecipeView_SIV_RecipePicture);
        recipeViewIngredientsAdapter = new RecipeViewIngredientsAdapter(this,recipe.getIngredients());
        recipeViewStepsAdapter = new RecipeViewStepsAdapter(this,recipe.getSteps());
        RecipeView_MTV_name.setText(RecipeName);
        RecipeView_MTV_author.setText(RecipeAuthor);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        RecipeView_RV_ingredients.setLayoutManager(linearLayoutManager1);
        RecipeView_RV_ingredients.setAdapter(recipeViewIngredientsAdapter);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(RecyclerView.VERTICAL);
        RecipeView_RV_steps.setLayoutManager(linearLayoutManager2);
        RecipeView_RV_steps.setAdapter(recipeViewStepsAdapter);
    }

    public void changeActivityToCookActivity(){
        Intent CookingIntent = new Intent(this, CookingRecipeActivity.class);
        CookingIntent.putStringArrayListExtra("StepsListKey",steps);
        startActivity(CookingIntent);
    }
}