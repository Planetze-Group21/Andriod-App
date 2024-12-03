package com.example.planetzeapp;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;


public class LoginActivity extends AppCompatActivity implements LoginView {


    private LoginPresenter presenter;
    private EditText loginEmail, loginPassword;
    private TextView signupRedirectText, forgotPasswordText;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        presenter = new LoginPresenter(this, new LoginModel(), new AndroidEmailValidator());


        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        loginButton.setOnClickListener(v -> presenter.handleLogin(
                loginEmail.getText().toString(),
                loginPassword.getText().toString()
        ));


        signupRedirectText.setOnClickListener(v -> presenter.onSignUpClick());


        forgotPasswordText.setOnClickListener(v -> presenter.onForgotPasswordClick());
    }


    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void navigateToEcoGauge() {
        startActivity(new Intent(this, EcoGaugeActivity.class)); //CHANGE TO ECOGAUGE
        finish();
    }


    @Override
    public void navigateToIntroduction(){
        startActivity(new Intent(this, SignupQuestionsActivity.class));
        finish();
    }


    @Override
    public void navigateToSignUp() {
        startActivity(new Intent(this, SignUpActivity.class));
        finish();
    }


    @Override
    public void navigateToResetPassword() {
        startActivity(new Intent(this, ResetPassActivity.class));
        finish();
    }


    @Override
    public void promptEmailVerification() {
        Toast.makeText(this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
    }
}