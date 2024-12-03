package com.example.planetzeapp;


public interface LoginView {
    void showError(String message);

    void navigateToEcoGauge();

    void navigateToSignUp();

    void navigateToIntroduction();

    void navigateToResetPassword();


    void promptEmailVerification();

}

