package com.codemaster.switchadmin.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component("permissionChecker")
public class PermissionChecker {

    /**
     * Checks if the user has ALL specified permissions OR the "ALL" permission.
     */
    public boolean hasAllPermissions(Authentication authentication, String... permissions) {
        // If user has "ALL" permission, grant access immediately
        boolean hasFullAccess = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ALL"));

        if (hasFullAccess) {
            return true;
        }

        // Otherwise, check for all required permissions
        return Arrays.stream(permissions)
                .allMatch(requiredPerm ->
                        authentication.getAuthorities().stream()
                                .anyMatch(userPerm -> userPerm.getAuthority().equals(requiredPerm))
                );
    }
}