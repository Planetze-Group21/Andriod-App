package com.example.planetzeapp;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LoginPresenterUnitTest {
    private LoginView mockView;
    private LoginModel mockModel;
    private EmailValidator mockEmailValidator;
    private LoginPresenter presenter;

    @Before
    public void setUp() {
        mockView = mock(LoginView.class);
        mockModel = mock(LoginModel.class);
        mockEmailValidator = mock(EmailValidator.class);
        presenter = new LoginPresenter(mockView, mockModel, mockEmailValidator);
    }

    @Test
    public void handleLogin_noEmail_showsError() {
        //no email entered
        presenter.handleLogin("", "password");

        verify(mockView).showError("Email can't be empty.");
    }

    @Test
    public void handleLogin_invalidEmail_showsError() {
        //invalid email because no email organization
        String invalidEmail = "invalid";
        when(mockEmailValidator.isValid(invalidEmail)).thenReturn(false);

        presenter.handleLogin(invalidEmail, "password");

        verify(mockView).showError("Invalid email address.");
    }

    @Test
    public void handleLogin_noPassword_showsError() {
        //no password entered
        String validEmail = "email@gmail.com";
        when(mockEmailValidator.isValid(validEmail)).thenReturn(true);

        presenter.handleLogin(validEmail, "");

        verify(mockView).showError("Password can't be empty.");
    }

    @Test
    public void handleLogin_successfulLogin_setupComplete_navigatesToEcoGauge() {
        //valid account that has finished the set up Qs
        String validEmail = "email@gmail.com";
        String password = "password123";

        when(mockEmailValidator.isValid(validEmail)).thenReturn(true);

        doAnswer(invocation -> {
            LoginModel.LoginCallback callback = invocation.getArgument(2);
            callback.onSetupComplete();
            return null;
        }).when(mockModel).login(eq(validEmail), eq(password), any());

        presenter.handleLogin(validEmail, password);

        verify(mockView).navigateToEcoGauge();
    }

    @Test
    public void handleLogin_successfulLogin_setupIncomplete_navigatesToSetupQs() {
        //valid account that has not finished the set up questions
        String validEmail = "email@gmail.com";
        String password = "password123";

        when(mockEmailValidator.isValid(validEmail)).thenReturn(true);

        doAnswer(invocation -> {
            LoginModel.LoginCallback callback = invocation.getArgument(2);
            callback.onSetupIncomplete();
            return null;
        }).when(mockModel).login(eq(validEmail), eq(password), any());

        presenter.handleLogin(validEmail, password);

        verify(mockView).navigateToIntroduction();
    }

    @Test
    public void handleLogin_unverifiedEmail_showsPrompt() {
        //valid account, has not verified their email
        String validEmail = "email@gmail.com";
        String password = "password123";

        when(mockEmailValidator.isValid(validEmail)).thenReturn(true);

        doAnswer(invocation -> {
            LoginModel.LoginCallback callback = invocation.getArgument(2);
            callback.onEmailNotVerified();
            return null;
        }).when(mockModel).login(eq(validEmail), eq(password), any());

        presenter.handleLogin(validEmail, password);

        verify(mockView).promptEmailVerification();
    }

    @Test
    public void handleLogin_failure_showsErrorMessage() {
        //user attempts to sign up without the correct username/password
        String validEmail = "email@gmail.com";
        String password = "password123";
        String errorMessage = "Invalid credentials";

        when(mockEmailValidator.isValid(validEmail)).thenReturn(true);

        doAnswer(invocation -> {
            LoginModel.LoginCallback callback = invocation.getArgument(2);
            callback.onFailure(errorMessage);
            return null;
        }).when(mockModel).login(eq(validEmail), eq(password), any());

        presenter.handleLogin(validEmail, password);

        verify(mockView).showError("Login failed: " + errorMessage);
    }

    @Test
    public void onSignUpClick_navigatesToSignUp() {
        //user clicks the sign up link
        presenter.onSignUpClick();

        verify(mockView).navigateToSignUp();
    }

    @Test
    public void onForgotPasswordClick_navigatesToResetPassword() {
        //user forgets their password ("send" auth password reset request)
        presenter.onForgotPasswordClick();

        verify(mockView).navigateToResetPassword();
    }




}
