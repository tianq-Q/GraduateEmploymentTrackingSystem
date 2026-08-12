package com.college.employment.common.constant;

/**
 * 系统角色常量
 */
public final class RoleConstants {

    public static final String GRADUATE = "GRADUATE";
    public static final String TEACHER = "TEACHER";
    public static final String COLLEGE_ADMIN = "COLLEGE_ADMIN";
    public static final String SYSTEM_ADMIN = "SYSTEM_ADMIN";

    /** Spring Security hasRole 表达式 */
    public static final String HAS_GRADUATE = "hasRole('GRADUATE')";
    public static final String HAS_TEACHER = "hasRole('TEACHER')";
    public static final String HAS_COLLEGE_ADMIN = "hasRole('COLLEGE_ADMIN')";
    public static final String HAS_SYSTEM_ADMIN = "hasRole('SYSTEM_ADMIN')";

    /** 组合权限表达式 */
    public static final String HAS_TEACHER_OR_COLLEGE = "hasAnyRole('TEACHER','COLLEGE_ADMIN')";
    public static final String HAS_COLLEGE_OR_SYSTEM = "hasAnyRole('COLLEGE_ADMIN','SYSTEM_ADMIN')";
    public static final String HAS_TEACHER_AND_ABOVE = "hasAnyRole('TEACHER','COLLEGE_ADMIN','SYSTEM_ADMIN')";
    public static final String HAS_COLLEGE_AND_ABOVE = "hasAnyRole('COLLEGE_ADMIN','SYSTEM_ADMIN')";
    public static final String HAS_ANY = "hasAnyRole('GRADUATE','TEACHER','COLLEGE_ADMIN','SYSTEM_ADMIN')";

    private RoleConstants() {}
}
