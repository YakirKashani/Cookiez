package com.example.cookiez;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.cookiez.Adapters.IngredientSetAdapter;
import com.example.cookiez.Adapters.StepsSetAdapter;
import com.example.cookiez.Model.Ingredient;
import com.example.cookiez.Model.Recipe;
import com.example.cookiez.Model.User;
import com.example.cookiez.Utilities.SignalManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewRecipeActivity extends AppCompatActivity {
    private RecyclerView NewRecipe_RV_ingredients;
    private TextInputEditText NewRecipe_TIET_name;
    private ArrayList<Ingredient> ingredientList;
    private ImageButton NewRecipe_IB_addIngredient;
    private IngredientSetAdapter ingredientSetAdapter;
    private ArrayList <String> stepsList;
    private RelativeLayout NewRecipe_RL_main;
    private RecyclerView NewRecipe_RV_steps;
    private ImageButton NewRecipe_IB_addStep;
    private StepsSetAdapter stepsSetAdapter;
    private MaterialButton NewRecipe_MB_upload;
    private MaterialButton NewRecipe_MB_cancel;
    private ShapeableImageView Register_SIV_AddRecipeImage;
    private FirebaseDatabase db;
    private DatabaseReference UsersRef;
    private DatabaseReference SpecificUserRef;
    private FirebaseAuth auth;
    private FirebaseUser user;
    public static final String UserNameStatus = "User_Name_Status";
    private String CurrentUserName;
    private String simpleDate;
    private String simpleTime;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat simpleTimeFormat;
    private Uri image;
    private Uri firebaseImage;
    private StorageReference storageReference;
    private StorageReference RecipePictureReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_recipe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.NewRecipe_RL_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent previousScreen = getIntent();
        CurrentUserName = previousScreen.getStringExtra(UserNameStatus);
        findViews();
        initViews();
    }

    private void findViews() {
        NewRecipe_RL_main = findViewById(R.id.NewRecipe_RL_main);


        NewRecipe_TIET_name = findViewById(R.id.NewRecipe_TIET_name);
        NewRecipe_MB_upload = findViewById(R.id.NewRecipe_MB_upload);
        NewRecipe_MB_cancel=findViewById(R.id.NewRecipe_MB_cancel);
        Register_SIV_AddRecipeImage = findViewById(R.id.Register_SIV_AddRecipeImage);


        //Ingredients
        NewRecipe_RV_ingredients = findViewById(R.id.NewRecipe_RV_ingredients);
        NewRecipe_IB_addIngredient = findViewById(R.id.NewRecipe_IB_addIngredient);

        //Steps
        NewRecipe_RV_steps = findViewById(R.id.NewRecipe_RV_steps);
        NewRecipe_IB_addStep = findViewById(R.id.NewRecipe_IB_addStep);


    }
    private void initViews() {

        db = FirebaseDatabase.getInstance();
        UsersRef = db.getReference("users");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        SpecificUserRef = UsersRef.child(user.getUid()); // Users -> specific user

        storageReference = FirebaseStorage.getInstance().getReference();


        //Ingredients

        ingredientList = new ArrayList<>();
        ingredientSetAdapter = new IngredientSetAdapter(getApplicationContext(), ingredientList);
        LinearLayoutManager linearLayoutManagerIngredients = new LinearLayoutManager(this);
        linearLayoutManagerIngredients.setOrientation(RecyclerView.VERTICAL);
        NewRecipe_RV_ingredients.setLayoutManager(linearLayoutManagerIngredients);
        NewRecipe_RV_ingredients.setAdapter(ingredientSetAdapter);
        NewRecipe_IB_addIngredient.setOnClickListener(v -> {
            ingredientList.add(new Ingredient("", "", ""));
            ingredientSetAdapter.notifyItemInserted(ingredientList.size() - 1);
        });

        //Steps
        stepsList = new ArrayList<>();
        stepsSetAdapter = new StepsSetAdapter(getApplicationContext(), stepsList);
        LinearLayoutManager linearLayoutManagerSteps = new LinearLayoutManager(this);
        linearLayoutManagerSteps.setOrientation(RecyclerView.VERTICAL);
        NewRecipe_RV_steps.setLayoutManager(linearLayoutManagerSteps);
        NewRecipe_RV_steps.setAdapter(stepsSetAdapter);
        NewRecipe_IB_addStep.setOnClickListener(v -> {
            stepsList.add("");
            stepsSetAdapter.notifyItemInserted(stepsList.size() - 1);
        });
        NewRecipe_MB_upload.setOnClickListener(v -> { // Save button
            Recipe recipe = new Recipe();
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
            simpleDate = simpleDateFormat.format(calendar.getTime()).toString();
            simpleTime = simpleTimeFormat.format(calendar.getTime()).toString();
            String name = NewRecipe_TIET_name.getText().toString();

            recipe.setName(name).setIngredients(ingredientSetAdapter.getIngredientList()).setSteps(stepsSetAdapter.getStepsList()).setAuthor(CurrentUserName).setDate(simpleDate).setTime(simpleTime);

            db.getReference("users").child(user.getUid()).child("Recipes").child(recipe.getName()).setValue(recipe);
            uploadImage(recipe,image);
            ChangeActivityMainActivity();
            SignalManager.getInstance().toast("Recipe Uploaded Successfully");
        });

        NewRecipe_MB_cancel.setOnClickListener(v -> {
            finish();
        });

        Register_SIV_AddRecipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    image = result.getData().getData();
                    Glide.with(getApplicationContext()).load(image).into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            NewRecipe_RL_main.setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
                }
            } else {
            }
        }
    });

    private void uploadImage(Recipe recipe,Uri image) {
        RecipePictureReference = storageReference.child("images/" + UUID.randomUUID().toString());
        RecipePictureReference.putFile(image).continueWithTask(task -> {
            if(!task.isSuccessful())
                throw task.getException();
            return RecipePictureReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                Log.d("Recipe picture upload","succeed");
                Uri downloadUri = task.getResult();
                Log.d("downloadUri",downloadUri.toString());
                if(downloadUri!=null){
                    firebaseImage = downloadUri;
                    Log.d("firebaseImage",firebaseImage.toString());
                    useUri(recipe,downloadUri);
                }
            }else{
                Log.d("Recipe picture upload","failed");
            }
        });
    }

    private void useUri(Recipe recipe,Uri uri) {
        Log.d("useUri",uri.toString());
        db.getReference("users").child(user.getUid()).child("Recipes").child(recipe.getName()).child("Recipe Picture").setValue(uri.toString());
    }

    private void ChangeActivityMainActivity() {
        Intent MainIntent = new Intent(this, MainActivity.class);
        startActivity(MainIntent);
        finish();
    }
}