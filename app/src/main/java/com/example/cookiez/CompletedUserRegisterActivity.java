package com.example.cookiez;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.cookiez.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class CompletedUserRegisterActivity extends AppCompatActivity {
    public static final String UID_Status = "UID_Status";
    private TextInputEditText Register_TIET_UserNameInput;
    private MaterialButton Register_MB_Register;
    private String UserNameChosen;
    private FirebaseDatabase db;
    private DatabaseReference UsersRef;
    private DatabaseReference SpecificUserRef;
    //Profile picture
    private StorageReference storageReference;
    private Uri image;
    private Uri firebaseImage;
    private ShapeableImageView Register_SIV_ProfileImage;
    private StorageReference ProfilePictureReference;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    image = result.getData().getData();
                    //Optional - make Matterial Button Register enabled after uploading a photo
                    //Register_MB_Register.setEnabled(true);
                    Glide.with(getApplicationContext()).load(image).into(Register_SIV_ProfileImage);
                }
            } else {
                //toast message upload failed
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_completed_user_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        Intent previousScreen = getIntent();
        String UID = previousScreen.getStringExtra(UID_Status);
        db = FirebaseDatabase.getInstance();
        UsersRef = db.getReference("users");
        SpecificUserRef = UsersRef.child(UID); // Users -> specific user


        findViews();
        initViews(UID);
    }

    private void initViews(String UID) {
        FirebaseApp.initializeApp(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        Register_SIV_ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });

        Register_MB_Register.setOnClickListener(v -> {
            UserNameChosen = Register_TIET_UserNameInput.getText().toString();
            User NewUser = new User();
            NewUser.setUid(UID).setUserName(UserNameChosen);
            SpecificUserRef.setValue(NewUser);
            uploadImage(image);
            ChangeActivityMainActivity();
        });

    }

    private void findViews() {
        Register_TIET_UserNameInput = findViewById(R.id.Register_TIET_UserNameInput);
        Register_MB_Register = findViewById(R.id.Register_MB_Register);
        Register_SIV_ProfileImage = findViewById(R.id.Register_SIV_ProfileImage);
    }

    private void ChangeActivityMainActivity() {
        Intent MainIntent = new Intent(this, MainActivity.class);
        startActivity(MainIntent);
        finish();
    }

    private void uploadImage(Uri image) {
        ProfilePictureReference = storageReference.child("images/" + UUID.randomUUID().toString());
        ProfilePictureReference.putFile(image).continueWithTask(task -> {
            if(!task.isSuccessful())
                throw task.getException();
            return ProfilePictureReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                Log.d("Profile picture upload","succeed");
                Uri downloadUri = task.getResult();
                Log.d("downloadUri",downloadUri.toString());
                if(downloadUri!=null){
                    firebaseImage = downloadUri;
                    Log.d("firebaseImage",firebaseImage.toString());
                    useUri(downloadUri);
                }
            }else{
                Log.d("Profile picture upload","failed");
            }
        });
    }

    private void useUri(Uri uri) {
        Log.d("useUri",uri.toString());
        SpecificUserRef.child("Profile Picture").setValue(uri.toString());
    }
}