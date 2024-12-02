package com.example.planetzeapp;

public class LoginPresenter {
    private final LoginView view;
    private final LoginModel model;
    private final EmailValidator emailValidator;

    public LoginPresenter(LoginView view, LoginModel model, EmailValidator emailValidator) {
        this.view = view;
        this.model = model;
        this.emailValidator = emailValidator;
    }

    public void handleLogin(String email, String password) {
        if (email.isEmpty()) {
            view.showError("Email can't be empty.");
            return;
        }

        if (!emailValidator.isValid(email)) {
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