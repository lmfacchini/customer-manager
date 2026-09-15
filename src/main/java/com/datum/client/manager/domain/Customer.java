package com.datum.client.manager.domain;

import com.datum.client.manager.constants.CustomerStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


@Entity
@Table(name = "cstmr")
@Data
@EqualsAndHashCode(of = {"id", "cpf", "email"}, callSuper = false)
@DynamicInsert
public class Customer extends AuditableEntity{

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cstmr_seq_generator"
    )
    @SequenceGenerator(
            name = "cstmr_seq_generator",
            sequenceName = "cstmr_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "nme")
    private String name;

    @Column(name = "cpf")
    private String cpf;

    @Column(name = "eml")
    private String email;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "stts", columnDefinition = "customerstatus")
    private CustomerStatus status;

}
