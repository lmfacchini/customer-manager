package com.datum.client.manager.domain;

import com.datum.client.manager.constants.CustomerStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@Table(name = "cstmr")
@Data
@EqualsAndHashCode(of = {"id", "cpf", "email"}, callSuper = false)
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
    @Column(name = "stts")
    private CustomerStatus status;

}
