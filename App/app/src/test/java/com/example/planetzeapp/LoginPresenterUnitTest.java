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
    public void handleLogin_invalidEmail_showsError() {
        // Arrange
        String invalidEmail = "invalid";
        when(mockEmailValidator.isValid(invalidEmail)).thenReturn(false);

        // Act
        presenter.handleLogin(invalidEmail, "password");

        // Assert
        verify(mockView).showError("Invalid email address.");
    }
}
