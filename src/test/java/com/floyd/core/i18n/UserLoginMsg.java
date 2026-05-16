package com.floyd.core.i18n;

import ch.jalu.configme.Comment;
import org.springframework.stereotype.Component;

/**
 *
 * @author floyd
 */
@Component
public class UserLoginMsg implements I18nMessageHolder {

    @Comment("USERNAME")
    public static final LocaleMessage USERNAME = LocaleMessage.of("login.username", "Username");

    @Comment("PASSWORD")
    public static final LocaleMessage PASSWORD = LocaleMessage.of("login.password", "Password");

    @Comment("LOGIN")
    public static final LocaleMessage LOGIN = LocaleMessage.of("login.login", "Login");

    @Comment("LOGOUT")
    public static final LocaleMessage LOGOUT = LocaleMessage.of("login.logout", "Logout");

    @Comment("REGISTER")
    public static final LocaleMessage REGISTER = LocaleMessage.of("login.register", "Register");

    @Comment("FORGET_PASSWORD")
    public static final LocaleMessage FORGET_PASSWORD = LocaleMessage.of("login.forget", "Forgot Password");
    @Comment("RESET_PASSWORD")
    public static final LocaleMessage RESET_PASSWORD = LocaleMessage.of("login.reset", "Reset Password");
    @Comment("USERNAME_PLACEHOLDER")
    public static final LocaleMessage USERNAME_PLACEHOLDER = LocaleMessage.of("login.username_placeholder", "Please enter username");
    @Comment("PASSWORD_PLACEHOLDER")
    public static final LocaleMessage PASSWORD_PLACEHOLDER = LocaleMessage.of("login.password_placeholder", "Please enter password");
    @Comment("USERNAME_REQUIRED")
    public static final LocaleMessage USERNAME_REQUIRED = LocaleMessage.of("login.username_required", "Username cannot be empty");
    @Comment("PASSWORD_REQUIRED")
    public static final LocaleMessage PASSWORD_REQUIRED = LocaleMessage.of("login.password_required", "Password cannot be empty");
    @Comment("USERNAME_INVALID")
    public static final LocaleMessage USERNAME_INVALID = LocaleMessage.of("login.username_invalid", "Invalid username format");
    @Comment("PASSWORD_INVALID")
    public static final LocaleMessage PASSWORD_INVALID = LocaleMessage.of("login.password_invalid", "Invalid password format");
    @Comment("USERNAME_EXISTS")
    public static final LocaleMessage USERNAME_EXISTS = LocaleMessage.of("login.username_exists", "Username already exists");
    @Comment("USERNAME_NOT_EXISTS")
    public static final LocaleMessage USERNAME_NOT_EXISTS = LocaleMessage.of("login.username_not_exists", "Username does not exist");

    public static final LocaleMessage TEST_NOT_CONFIGED = LocaleMessage.of("login.test_not_configed", "test_not_configed message: {0}");
}
