package com.pure.service.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String HEADMASTER = "ROLE_HEADMASTER";
    public static final String SALES = "ROLE_SALES";
    public static final String SALESMANAGER = "ROLE_SALES_MANAGER";
    public static final String RECEPTION = "ROLE_RECEPTION";
    public static final String PWI = "ROLE_PWI";
    public static final String COURSE_CONSULTANT = "ROLE_COURSE_CONSULTANT";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private AuthoritiesConstants() {
    }
}
