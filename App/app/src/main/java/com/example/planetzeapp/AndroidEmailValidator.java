package com.example.planetzeapp;

import android.util.Patterns;

public class AndroidEmailValidator implements EmailValidator {
    @Override
    public boolean isValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

