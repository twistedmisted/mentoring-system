package ua.kpi.mishchenko.mentoringsystem.domain.entity.specification;

import org.springframework.data.jpa.domain.Specification;
import ua.kpi.mishchenko.mentoringsystem.domain.entity.MentoringRequestEntity;
import ua.kpi.mishchenko.mentoringsystem.domain.util.MentoringRequestStatus;

import static java.util.Objects.isNull;

public class MentoringRequestSpecification {

    public static Specification<MentoringRequestEntity> matchStatus(MentoringRequestStatus status) {
        if (isNull(status)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<MentoringRequestEntity> matchFromEmail(String fromEmail) {
        if (isNull(fromEmail) || fromEmail.isBlank()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("from").get("email"), fromEmail);
    }

    public static Specification<MentoringRequestEntity> matchToEmail(String toEmail) {
        if (isNull(toEmail) || toEmail.isBlank()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("to").get("email"), toEmail);
    }
}
