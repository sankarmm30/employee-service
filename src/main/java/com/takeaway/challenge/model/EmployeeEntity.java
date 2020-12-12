package com.takeaway.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employee", schema = "emp")
public class EmployeeEntity implements Serializable {

    private static final long serialVersionUID = -7526472295622776150L;

    @Id
    @Column(name = "iemployee_id_pk", nullable = false)
    @GeneratedValue(generator = "seq_emp_id", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "seq_emp_id", sequenceName = "seq_employee_id", schema = "emp", allocationSize = 1)
    private Long employeeIdPk;

    @Column(name= "semployee_id", nullable = false)
    private String employeeId;

    @Column(name= "semail", nullable = false)
    private String email;

    @Column(name= "sname", nullable = false)
    private String name;

    @Column(name= "tsdateofbirth")
    private LocalDate dateOfBirth;

    @Column(name = "tscreated_at")
    private ZonedDateTime createdAt;

    @Column(name = "tsupdated_at")
    private ZonedDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinColumn(name = "idepart_id", nullable = false)
    private DepartmentEntity departmentEntity;
}
