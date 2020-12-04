package com.example.caridesain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class JoinActivity extends AppCompatActivity
{

    private EditText InputPassword, InputUsername,InputPhone;
    private ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        Button buatAkun = findViewById(R.id.btn_join);
        InputPassword = findViewById(R.id.password);
        InputUsername = findViewById(R.id.username);
        InputPhone = findViewById(R.id.phone);
        loadingbar = new ProgressDialog(this);

        buatAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateAccount();
            }
        });
    }


        private void CreateAccount ()
        {
            String username = InputUsername.getText().toString();
            String password = InputPassword.getText().toString();
            String phone = InputPhone.getText().toString();
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(this, "Please input your Username", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please input your Password", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Please input your Phone Number", Toast.LENGTH_SHORT).show();
            } else {
                loadingbar.setTitle("Creating Account");
                loadingbar.setMessage("Please wait a second. Checking the Requirement");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
                ValidatephoneNumber(username, password, phone);
            }
        }

        private void ValidatephoneNumber ( final String username, final String password, final String phone){
            final DatabaseReference RootRef;
            RootRef = FirebaseDatabase.getInstance().getReference();

            RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!(dataSnapshot.child("Users").child(phone).exists())) {

                        HashMap<String, Object> userdatamap = new HashMap<>();
                        userdatamap.put("Username", username);
                        userdatamap.put("Password", password);
                        userdatamap.put("Phone", phone);

                        RootRef.child("Users").child(phone).updateChildren(userdatamap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(JoinActivity.this, "Congratulation, your account have been created.", Toast.LENGTH_LONG).show();
                                            loadingbar.dismiss();
                                            startActivity(new Intent(JoinActivity.this, PageActivity.class));
                                        } else {
                                            loadingbar.dismiss();
                                            Toast.makeText(JoinActivity.this, "Network Error: Please try again...", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(JoinActivity.this, "This" + phone + "already exist", Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                        Toast.makeText(JoinActivity.this, "Please try again using another phone number", Toast.LENGTH_SHORT).show();

                      //  Intent intent1 = new Intent(JoinActivity.this, SignActivity.class);
                      //  startActivity(intent1);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

