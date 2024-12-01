package com.example.planetzeapp;


import android.util.Patterns;


import com.google.firebase.auth.FirebaseUser;


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
            public void onSuccess(FirebaseUser user) {
                String uid = user.getUid();
                UserRepository userRepository = new UserRepository();
                userRepository.checkSetupQuestions(uid, new UserRepository.SetupQsCallback() {
                    @Override
                    public void onSetupComplete(boolean isSetupComplete) {
                        if (isSetupComplete) {
                            view.navigateToEcoGauge();
                        } else {
                            view.navigateToIntroduction();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        view.showError("Error checking setup: " + errorMessage);
                    }
                });
            }

            @Override
            public void onEmailNotVerified(FirebaseUser user) {
                if (user != null && !user.isEmailVerified()) {
                    user.sendEmailVerification()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    view.promptEmailVerification();
                                } else {
                                    view.showError("Failed to send verification email. Please try again.");
                                }
                            });
                }
            }


            @Override
            public void onFailure(Exception e) {
                view.showError("Login failed: " + e.getMessage());
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
