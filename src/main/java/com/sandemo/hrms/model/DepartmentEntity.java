package com.sandemo.hrms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "department")
public class DepartmentEntity implements Serializable {

    private static final long serialVersionUID = -7526472295622776147L;

    @Id
    @Column(name = "idepart_id", nullable = false)
    @GeneratedValue(generator = "seq_depart_id", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "seq_depart_id", sequenceName = "seq_department_id", allocationSize = 1)
    private Long departId;

    @Column(name = "sname", nullable = false, length = 50)
    private String name;

    @Column(name = "tscreated_at")
    private ZonedDateTime createdAt;

    @Column(name = "tsupdated_at")
    private ZonedDateTime updatedAt;
}
