package com.datum.client.manager.repository.jpa.specification;

import com.datum.client.manager.domain.Customer;
import com.datum.client.manager.dto.CustomerFilter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class CustomerSpecifications {

    private CustomerSpecifications() {
    }

    public static Specification<Customer> byFilter(
            CustomerFilter filter
    ) {
        return (root, query, criteriaBuilder) -> {
            if (filter == null) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.name())) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                contains(filter.name()),
                                '\\'
                        )
                );
            }

            if (StringUtils.hasText(filter.email())) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("email")),
                                contains(filter.email()),
                                '\\'
                        )
                );
            }

            if (StringUtils.hasText(filter.cpf())) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("cpf"),
                                filter.cpf().trim()
                        )
                );
            }

            if (filter.status() != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("status"),
                                filter.status()
                        )
                );
            }

            if (filter.createdAtStart() != null) {
                Instant createdAtStart = Instant.ofEpochMilli(
                        filter.createdAtStart()
                );

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.<Instant>get("createdAt"),
                                createdAtStart
                        )
                );
            }

            if (filter.createdAtEnd() != null) {
                Instant createdAtEnd = Instant.ofEpochMilli(
                        filter.createdAtEnd()
                );

                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.<Instant>get("createdAt"),
                                createdAtEnd
                        )
                );
            }

            if (filter.updatedAtStart() != null) {
                Instant updatedAtStart = Instant.ofEpochMilli(
                        filter.updatedAtStart()
                );

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.<Instant>get("updatedAt"),
                                updatedAtStart
                        )
                );
            }

            if (filter.updatedAtEnd() != null) {
                Instant updatedAtEnd = Instant.ofEpochMilli(
                        filter.updatedAtEnd()
                );

                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.<Instant>get("updatedAt"),
                                updatedAtEnd
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(Predicate[]::new)
            );
        };
    }

    private static String contains(String value) {
        String escapedValue = value
                .trim()
                .toLowerCase(Locale.ROOT)
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");

        return "%" + escapedValue + "%";
    }
}
