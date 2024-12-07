package com.example.signinoutlab4.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.signinoutlab4.R;
import com.example.signinoutlab4.adapters.UsersAdapter;
import com.example.signinoutlab4.databinding.ActivityUserBinding;
import com.example.signinoutlab4.models.User;
import com.example.signinoutlab4.utilities.Constants;
import com.example.signinoutlab4.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class userActivity extends AppCompatActivity {

    private ActivityUserBinding binding;

    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        setListeners();
        getUsers();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.Key_COLLECTION_USERS).get()
                .addOnCompleteListener(task -> {
                    loading(false);
                   String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                   if(task.isSuccessful() && task.getResult()!=null){
                       List<User> users = new ArrayList<>();
                       for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                           if(currentUserId.equals(queryDocumentSnapshot.getId())){
                               continue;
                           }
                           User user = new User();
                           user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                           user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                           user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                           user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                           user.id = queryDocumentSnapshot.getId();
                           users.add(user);
                       }
                       if(users.size() > 0){
                           UsersAdapter usersAdapter = new UsersAdapter(users);
                           binding.usersRecyclerView.setAdapter(usersAdapter);
                           binding.usersRecyclerView.setVisibility(View.VISIBLE);
                       } else {
                           showErrorMessage();
                       }
                   }
                });
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.INVISIBLE);
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
        }
    }


}