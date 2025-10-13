package com.swd.exe.teammanagement.spec;

import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.user.UserRole;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecs {
    private UserSpecs() {}

    public static Specification<User> keyword(String q) {
        if (q == null || q.isBlank()) return null;
        String like = "%" + q.toLowerCase() + "%";
        return (root, cq, cb) -> cb.or(
                cb.like(cb.lower(root.get("fullName")), like),
                cb.like(cb.lower(root.get("email")), like),
                cb.like(cb.lower(root.get("studentCode")), like)
        );
    }

    public static Specification<User> role(UserRole role) {
        return role == null ? null : (root, cq, cb) -> cb.equal(root.get("role"), role);
    }

    public static Specification<User> active(Boolean active) {
        return active == null ? null : (root, cq, cb) -> cb.equal(root.get("isActive"), active);
    }

//    public static Specification<User> majorCode(String code) {
//        if (code == null || code.isBlank()) return null;
//        return (root, cq, cb) -> {
//            var major = root.join("major", JoinType.LEFT);
//            return cb.equal(cb.lower(major.get("code")), code.toLowerCase());
//        };
//    }
}

