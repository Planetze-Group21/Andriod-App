package com.example.planetzeapp;


public interface LoginView {
    void showError(String message);

    void navigateToEcoTracker();

    void navigateToSignUp();

    //Changed from SetupQs to Introduction because
    // that is what the starting activity for setup Qs is.
  
    void navigateToSetUpQs();

    void navigateToResetPassword();

    void promptEmailVerification();

}

