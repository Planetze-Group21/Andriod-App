package com.example.planetzeapp;


public interface LoginView {
    void showError(String message);

    void navigateToEcoGauge();

    void navigateToSignUp();

    //Changed from SetupQs to Introduction because
    // that is what the starting activity for setup Qs is.
  
    void navigateToIntroduction();

    void navigateToResetPassword();

    void promptEmailVerification();

}

