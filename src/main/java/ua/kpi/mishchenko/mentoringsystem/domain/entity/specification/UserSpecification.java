package ua.kpi.mishchenko.mentoringsystem.domain.entity.specification;

import org.springframework.data.jpa.domain.Specification;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.UserEntity;
import ua.kpi.mishchenko.mentoringsystem.domain.util.UserStatus;

import java.util.List;

public class UserSpecification {

    public static Specification<UserEntity> matchSpecialization(List<String> specializations) {
        if (specializations == null || specializations.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> root.get("questionnaire").get("specialization").get("name").in(specializations);
    }

    public static Specification<UserEntity> matchRank(String rank) {
        if (rank == null || rank.isBlank()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("questionnaire").get("rank").get("name"), rank);
    }

    public static Specification<UserEntity> matchStatus(UserStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<UserEntity> matchHoursPerWeek(Double hoursPerWeek) {
        if (hoursPerWeek == null || hoursPerWeek < 1) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("questionnaire").get("hoursPerWeek"), hoursPerWeek);
    }
}
