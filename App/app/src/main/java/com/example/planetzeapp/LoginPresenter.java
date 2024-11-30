package com.example.planetzeapp;

import android.util.Patterns;

public class LoginPresenter {
    private final LoginView view;
    private final LoginModel model;

    public LoginPresenter(LoginView view, LoginModel model) {
        this.view = view;
        this.model = model;
    }

    public void handleLogin(String email, String password) {
        if (email.isEmpty()) {
            view.showError("Email can't be empty.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.showError("Invalid email address.");
            return;
        }

        if (password.isEmpty()) {
            view.showError("Password can't be empty.");
            return;
        }

        model.login(email, password, new LoginModel.LoginCallback() {
            @Override
            public void onSetupComplete() {
                view.navigateToEcoGauge();
            }

            @Override
            public void onSetupIncomplete() {
                view.navigateToSetupQs();
            }

            @Override
            public void onEmailNotVerified() {
                view.promptEmailVerification();
            }

            @Override
            public void onFailure(String errorMessage) {
                view.showError("Login failed: " + errorMessage);
            }
        });
    }

    public void onSignUpClick() {
        view.navigateToSignUp();
    }

    public void onForgotPasswordClick() {
        view.navigateToResetPassword();
    }
}
