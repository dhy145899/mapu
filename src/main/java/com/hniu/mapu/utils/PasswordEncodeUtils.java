package com.hniu.mapu.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密工具类
 * @author jiujiu
 */
public class PasswordEncodeUtils {
    private final static PasswordEncoder passwordEncoder;
    static  {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * 加密密码
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encoder(String password) {
        return passwordEncoder.encode(password);
    }
    
    /**
     * 验证密码
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
