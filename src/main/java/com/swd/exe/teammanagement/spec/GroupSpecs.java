package com.swd.exe.teammanagement.spec;

import com.swd.exe.teammanagement.entity.Group;
import com.swd.exe.teammanagement.entity.User;
import com.swd.exe.teammanagement.enums.group.GroupStatus;
import com.swd.exe.teammanagement.enums.group.GroupType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public final class GroupSpecs {
    private GroupSpecs() {}

    public static Specification<Group> keyword(String q) {
        if (q == null || q.isBlank()) return null;
        String like = "%" + q.toLowerCase().trim() + "%";

        return (root, cq, cb) -> {
            var predicates = new ArrayList<Predicate>();

            // title
            predicates.add(cb.like(cb.lower(root.get("title")), like));

            // leader name
            Join<Group, User> leader = root.join("leader", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(leader.get("fullName")), like));

            // lecturer name
            Join<Group, User> lecturer = root.join("checkpointLecture", JoinType.LEFT);
            predicates.add(cb.like(cb.lower(lecturer.get("fullName")), like));

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Group> status(GroupStatus status) {
        return status == null ? null : (root, cq, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Group> type(GroupType type) {
        return type == null ? null : (root, cq, cb) -> cb.equal(root.get("type"), type);
    }
}
