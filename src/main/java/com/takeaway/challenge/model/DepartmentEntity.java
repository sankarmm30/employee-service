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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "department", schema = "emp")
public class DepartmentEntity implements Serializable {

    private static final long serialVersionUID = -7526472295622776147L;

    @Id
    @Column(name = "idepart_id", nullable = false)
    @GeneratedValue(generator = "seq_depart_id", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "seq_depart_id", sequenceName = "seq_department_id", schema = "emp", allocationSize = 1)
    private Long departId;

    @Column(name = "sname", nullable = false, length = 50)
    private String name;

    @Column(name = "tscreated_at")
    private ZonedDateTime createdAt;

    @Column(name = "tsupdated_at")
    private ZonedDateTime updatedAt;

    @OneToMany(
            fetch = FetchType.EAGER, mappedBy = "departmentEntity",
            cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.REMOVE}
    )
    private List<EmployeeEntity> employeeEntities;
}
