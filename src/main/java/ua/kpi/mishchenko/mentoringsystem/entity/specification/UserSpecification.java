package ua.kpi.mishchenko.mentoringsystem.entity.specification;

import org.springframework.data.jpa.domain.Specification;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus;
import ua.kpi.mishchenko.mentoringsystem.entity.UserEntity;

import static java.util.Objects.isNull;

public class UserSpecification {

    public static Specification<UserEntity> matchSpecialization(String specialization) {
        if (isNull(specialization) || specialization.isBlank()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("questionnaire").get("specialization").get("name"), specialization);
    }

    public static Specification<UserEntity> matchRank(String rank) {
        if (isNull(rank) || rank.isBlank()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("questionnaire").get("rank").get("name"), rank);
    }

    public static Specification<UserEntity> matchStatus(UserStatus status) {
        if (isNull(status)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<UserEntity> matchRole(String role) {
        if (isNull(role) || role.isBlank()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("role").get("name"), role);
    }

    public static Specification<UserEntity> hoursLessThanMaxValue(Integer hours) {
        if (isNull(hours) || hours <= 0) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("questionnaire")
                .get("hoursPerWeek"), hours);
    }

    public static Specification<UserEntity> hoursGreaterThanMinValue(Integer hours) {
        if (isNull(hours) || hours <= 0) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("questionnaire")
                .get("hoursPerWeek"), hours);
    }
}
