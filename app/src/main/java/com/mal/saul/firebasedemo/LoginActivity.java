package com.mal.saul.firebasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "loginactivity";
    private EditText etEmail;
    private EditText etPassword;
    private Button btnSignInUp;
    private TextView tvSignInUp;
    private FirebaseAuth mAuth;
    private boolean createNewAccount = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setListners();
        mAuth = FirebaseAuth.getInstance();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignInUp = findViewById(R.id.btnSignInUp);
        tvSignInUp = findViewById(R.id.tvSignInUp);
    }

    private boolean checkCredentials(String email, String password) {
        if (!email.contains("@") || email.length() < 6) {
            onInvalidEmail(R.string.error_invalid_email);
            return false;
        } else if (password.length() < 6) {
            onInvalidPassword(R.string.error_invalid_password);
            return false;
        }
        return true;
    }

    private void onInvalidEmail(int idError) {
        etEmail.setError(getString(idError));
        etEmail.requestFocus();
    }

    private void onInvalidPassword(int idError) {
        etPassword.setError(getString(idError));
        etPassword.requestFocus();
    }

    private void registerNewUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) { // Sign in success,
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        sendEmailVerification(user);
                    } else { // If sign in fails
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Error while registering new user", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendEmailVerification(FirebaseUser user) {
        Log.d(TAG, "started Verification");
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Verification email sent to " + user.getEmail());
                        showConfirmationDialog(R.string.confirm_email,
                                getString(R.string.please_confirm_email, user.getEmail()));
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.getException());
                    }
                });
    }

    private void showConfirmationDialog(int title, String msg){
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setMessage(msg);
        dlg.setTitle(title);
        dlg.setPositiveButton(R.string.ok, null);
        dlg.show();
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {  // Sign in success,
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (!user.isEmailVerified()) {
                            showConfirmationDialog(R.string.confirm_email,
                                    getString(R.string.please_confirm_email, user.getEmail()));
                        } else {
                            updateUI(user);
                        }
                    } else { // If sign in fails
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Error while login", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) { //send current user to next activity
        if (currentUser == null) return;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setListners() {
        tvSignInUp.setOnClickListener(view -> {
            createNewAccount = !createNewAccount;
            if (createNewAccount) { // if the user wants to create an account
                onChangeContent(R.string.create_account, R.string.sign_in_free);
            } else { // if the user wants to login
                onChangeContent(R.string.action_sign_in, R.string.create_account);
            }
        });
        btnSignInUp.setOnClickListener(view -> {
            if (checkCredentials(etEmail.getText().toString(), etPassword.getText().toString())) {
                if (createNewAccount) { // user wants to create a new account
                    registerNewUser(etEmail.getText().toString(), etPassword.getText().toString());
                } else { // User wants to login in with an existing account
                    loginUser(etEmail.getText().toString(), etPassword.getText().toString());
                }
            }
        });
    }

    private void onChangeContent(int btnTextId, int textViewTextId) {
        tvSignInUp.setText(textViewTextId);
        btnSignInUp.setText(btnTextId);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
}