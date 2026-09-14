package com.datum.client.manager.repository.jpa.specification;

import com.datum.client.manager.constants.CustomerStatus;
import com.datum.client.manager.domain.Customer;
import com.datum.client.manager.dto.CustomerFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerSpecificationsTest {

    @Mock
    private Root<Customer> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Path<String> namePath;

    @Mock
    private Path<String> emailPath;

    @Mock
    private Path<String> cpfPath;

    @Mock
    private Path<CustomerStatus> statusPath;

    @Mock
    private Path<Instant> createdAtPath;

    @Mock
    private Path<Instant> updatedAtPath;

    @Mock
    private Expression<String> loweredName;

    @Mock
    private Expression<String> loweredEmail;

    @Mock
    private Predicate conjunction;

    @Mock
    private Predicate namePredicate;

    @Mock
    private Predicate emailPredicate;

    @Mock
    private Predicate cpfPredicate;

    @Mock
    private Predicate statusPredicate;

    @Mock
    private Predicate createdAtStartPredicate;

    @Mock
    private Predicate createdAtEndPredicate;

    @Mock
    private Predicate updatedAtStartPredicate;

    @Mock
    private Predicate updatedAtEndPredicate;

    @Mock
    private Predicate combinedPredicate;

    private Instant createdAtStart;
    private Instant createdAtEnd;
    private Instant updatedAtStart;
    private Instant updatedAtEnd;

    @BeforeEach
    void setUp() {
        createdAtStart = Instant.ofEpochMilli(1_700_000_000_000L);
        createdAtEnd = Instant.ofEpochMilli(1_700_086_400_000L);
        updatedAtStart = Instant.ofEpochMilli(1_700_172_800_000L);
        updatedAtEnd = Instant.ofEpochMilli(1_700_259_200_000L);
    }

    @Test
    void shouldReturnConjunctionWhenFilterIsNull() {
        when(criteriaBuilder.conjunction()).thenReturn(conjunction);

        Specification<Customer> specification =
                CustomerSpecifications.byFilter(null);

        Predicate result =
                specification.toPredicate(root, query, criteriaBuilder);

        assertSame(conjunction, result);

        verify(criteriaBuilder).conjunction();
        verify(criteriaBuilder, never()).and(any(Predicate[].class));
    }

    @Test
    void shouldCreateAllPredicates() {
        CustomerFilter filter = CustomerFilter.builder()
                .name("  Leonardo  ")
                .email("  CUSTOMER@EMAIL.COM  ")
                .cpf(" 12345678901 ")
                .status(CustomerStatus.ACTIVE)
                .createdAtStart(createdAtStart.toEpochMilli())
                .createdAtEnd(createdAtEnd.toEpochMilli())
                .updatedAtStart(updatedAtStart.toEpochMilli())
                .updatedAtEnd(updatedAtEnd.toEpochMilli())
                .page(0)
                .max(20)
                .build();

        configurePaths();
        configurePredicates();

        Specification<Customer> specification =
                CustomerSpecifications.byFilter(filter);

        Predicate result =
                specification.toPredicate(root, query, criteriaBuilder);

        assertSame(combinedPredicate, result);

        verify(criteriaBuilder).like(
                loweredName,
                "%leonardo%",
                '\\'
        );

        verify(criteriaBuilder).like(
                loweredEmail,
                "%customer@email.com%",
                '\\'
        );

        verify(criteriaBuilder).equal(
                cpfPath,
                "12345678901"
        );

        verify(criteriaBuilder).equal(
                statusPath,
                CustomerStatus.ACTIVE
        );

        verify(criteriaBuilder).greaterThanOrEqualTo(
                createdAtPath,
                createdAtStart
        );

        verify(criteriaBuilder).lessThanOrEqualTo(
                createdAtPath,
                createdAtEnd
        );

        verify(criteriaBuilder).greaterThanOrEqualTo(
                updatedAtPath,
                updatedAtStart
        );

        verify(criteriaBuilder).lessThanOrEqualTo(
                updatedAtPath,
                updatedAtEnd
        );

        verify(criteriaBuilder).and(
                namePredicate,
                emailPredicate,
                cpfPredicate,
                statusPredicate,
                createdAtStartPredicate,
                createdAtEndPredicate,
                updatedAtStartPredicate,
                updatedAtEndPredicate
        );
    }

    @Test
    void shouldEscapeLikeSpecialCharacters() {
        CustomerFilter filter = CustomerFilter.builder()
                .name(" JO%_\\ÃO ")
                .email(" EMAIL%_\\@TEST.COM ")
                .cpf("12345678901")
                .status(CustomerStatus.ACTIVE)
                .createdAtStart(createdAtStart.toEpochMilli())
                .createdAtEnd(createdAtEnd.toEpochMilli())
                .updatedAtStart(updatedAtStart.toEpochMilli())
                .updatedAtEnd(updatedAtEnd.toEpochMilli())
                .page(0)
                .max(20)
                .build();

        when(root.<String>get("name")).thenReturn(namePath);
        when(root.<String>get("email")).thenReturn(emailPath);
        when(root.<String>get("cpf")).thenReturn(cpfPath);
        when(root.<CustomerStatus>get("status")).thenReturn(statusPath);
        when(root.<Instant>get("createdAt")).thenReturn(createdAtPath);
        when(root.<Instant>get("updatedAt")).thenReturn(updatedAtPath);

        when(criteriaBuilder.lower(namePath)).thenReturn(loweredName);
        when(criteriaBuilder.lower(emailPath)).thenReturn(loweredEmail);

        when(criteriaBuilder.like(
                loweredName,
                "%jo\\%\\_\\\\ão%",
                '\\'
        )).thenReturn(namePredicate);

        when(criteriaBuilder.like(
                loweredEmail,
                "%email\\%\\_\\\\@test.com%",
                '\\'
        )).thenReturn(emailPredicate);

        when(criteriaBuilder.equal(cpfPath, "12345678901"))
                .thenReturn(cpfPredicate);

        when(criteriaBuilder.equal(statusPath, CustomerStatus.ACTIVE))
                .thenReturn(statusPredicate);

        when(criteriaBuilder.greaterThanOrEqualTo(
                createdAtPath,
                createdAtStart
        )).thenReturn(createdAtStartPredicate);

        when(criteriaBuilder.lessThanOrEqualTo(
                createdAtPath,
                createdAtEnd
        )).thenReturn(createdAtEndPredicate);

        when(criteriaBuilder.greaterThanOrEqualTo(
                updatedAtPath,
                updatedAtStart
        )).thenReturn(updatedAtStartPredicate);

        when(criteriaBuilder.lessThanOrEqualTo(
                updatedAtPath,
                updatedAtEnd
        )).thenReturn(updatedAtEndPredicate);

        when(criteriaBuilder.and(any(Predicate[].class)))
                .thenReturn(combinedPredicate);

        Specification<Customer> specification =
                CustomerSpecifications.byFilter(filter);

        Predicate result =
                specification.toPredicate(root, query, criteriaBuilder);

        assertSame(combinedPredicate, result);

        verify(criteriaBuilder).like(
                loweredName,
                "%jo\\%\\_\\\\ão%",
                '\\'
        );

        verify(criteriaBuilder).like(
                loweredEmail,
                "%email\\%\\_\\\\@test.com%",
                '\\'
        );
    }

    private void configurePaths() {
        when(root.<String>get("name")).thenReturn(namePath);
        when(root.<String>get("email")).thenReturn(emailPath);
        when(root.<String>get("cpf")).thenReturn(cpfPath);
        when(root.<CustomerStatus>get("status")).thenReturn(statusPath);
        when(root.<Instant>get("createdAt")).thenReturn(createdAtPath);
        when(root.<Instant>get("updatedAt")).thenReturn(updatedAtPath);

        when(criteriaBuilder.lower(namePath)).thenReturn(loweredName);
        when(criteriaBuilder.lower(emailPath)).thenReturn(loweredEmail);
    }

    private void configurePredicates() {
        when(criteriaBuilder.like(
                loweredName,
                "%leonardo%",
                '\\'
        )).thenReturn(namePredicate);

        when(criteriaBuilder.like(
                loweredEmail,
                "%customer@email.com%",
                '\\'
        )).thenReturn(emailPredicate);

        when(criteriaBuilder.equal(
                cpfPath,
                "12345678901"
        )).thenReturn(cpfPredicate);

        when(criteriaBuilder.equal(
                statusPath,
                CustomerStatus.ACTIVE
        )).thenReturn(statusPredicate);

        when(criteriaBuilder.greaterThanOrEqualTo(
                createdAtPath,
                createdAtStart
        )).thenReturn(createdAtStartPredicate);

        when(criteriaBuilder.lessThanOrEqualTo(
                createdAtPath,
                createdAtEnd
        )).thenReturn(createdAtEndPredicate);

        when(criteriaBuilder.greaterThanOrEqualTo(
                updatedAtPath,
                updatedAtStart
        )).thenReturn(updatedAtStartPredicate);

        when(criteriaBuilder.lessThanOrEqualTo(
                updatedAtPath,
                updatedAtEnd
        )).thenReturn(updatedAtEndPredicate);

        when(criteriaBuilder.and(any(Predicate[].class)))
                .thenReturn(combinedPredicate);
    }
}