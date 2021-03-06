package humber.college.homies;
//Team Name: Homies
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Signup_page extends AppCompatActivity {

    EditText mUsername, mEmail, mPassword, mConfirmPassword;
    Button  button;
    boolean validation;
    public RelativeLayout layout1;
    SharedPreferences USR;

    public ArrayList<House> userHouses = new ArrayList<>();
    public ArrayList<House> userBookmarkedHouses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signup_layout);

        layout1 = findViewById(R.id.signupLayout);

        mUsername = findViewById(R.id.signupUserName);
        mEmail = findViewById(R.id.signupEmailAddress);
        mPassword = findViewById(R.id.signupPassword);
        mConfirmPassword = findViewById(R.id.signupConfirmPassword);
        button = findViewById(R.id.signupButton);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        //create profile submit button and validation
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = mUsername.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();
                validation = true;

                if(username.length() == 0){
                    mUsername.requestFocus();
                    mUsername.setError(getString(R.string.Error1));
                    validation = false;
                }
                else if(username.length() < 2){
                    mUsername.requestFocus();
                    mUsername.setError(getString(R.string.Error2));
                    validation = false;
                }

                if(email.length() == 0){
                    mEmail.requestFocus();
                    mEmail.setError(getString(R.string.Error1));
                    validation = false;
                }

                if(password.length() == 0){
                    mPassword.requestFocus();
                    mPassword.setError(getString(R.string.Error1));
                    validation = false;
                }
                else if(password.length() < 6){
                    mPassword.requestFocus();
                    mPassword.setError(getString(R.string.Error4));
                    validation = false;
                }

                if(confirmPassword.length() == 0){
                    mConfirmPassword.requestFocus();
                    mConfirmPassword.setError(getString(R.string.Error1));
                    validation = false;
                }
                else if(!confirmPassword.equals(password)){
                    mConfirmPassword.requestFocus();
                    mConfirmPassword.setError(getString(R.string.Error3));
                    validation = false;
                }

                if(validation){
                   final DatabaseReference myRef = database.getReference("USER/"+username);
                   final SignupData data = new SignupData(username, password, email, userHouses, userBookmarkedHouses);
                   final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("USER");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child(username).exists()){
                                mUsername.requestFocus();
                                mUsername.setError(getString(R.string.Error5));
                            }else{
                                myRef.setValue(data);
                                USR = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = USR.edit();
                                editor.putString("usernameStorage", username);
                                editor.apply();
                                Intent intent = new Intent(getApplicationContext(), Edit_profile_page.class);
                                startActivityForResult(intent, 0);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    } //end of oncreate

    @Override
    public void onBackPressed() {
        final String username = mUsername.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String phone = mPassword.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();
        String confirmPassword = mConfirmPassword.getText().toString().trim();
        if((username.length() > 0) || (email.length() > 0) || (phone.length() > 0) || (password.length() > 0) || (confirmPassword.length() > 0)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.signup_exit_confirmation);
            builder.setCancelable(true);

            builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });
            builder.setNegativeButton(getString(R.string.back_pres_neg), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do nothing
                    return;
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else{
            Intent intent = new Intent(this, Login_page.class);
            startActivity(intent);
        }
    }

    public void Go_To_Login(View view){
        Intent intent = new Intent(this, Login_page.class);
        startActivity(intent);
    }
}
