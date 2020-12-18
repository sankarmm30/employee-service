CREATE SCHEMA emp AUTHORIZATION postgres01;

-- SEQUENCE: emp.seq_department_id

-- DROP SEQUENCE emp.seq_department_id;

CREATE SEQUENCE emp.seq_department_id
    INCREMENT 1
    START 1
    MINVALUE 0
    MAXVALUE 99999999
    CACHE 1;

ALTER SEQUENCE emp.seq_department_id OWNER to postgres01;

-- SEQUENCE: emp.seq_employee_id

-- DROP SEQUENCE emp.seq_employee_id;

CREATE SEQUENCE emp.seq_employee_id
    INCREMENT 1
    START 1
    MINVALUE 0
    MAXVALUE 99999999999999
    CACHE 1;

ALTER SEQUENCE emp.seq_employee_id OWNER to postgres01;

-- Table: emp.department

-- DROP TABLE emp.department;

CREATE TABLE emp.department
(
    idepart_id integer NOT NULL,
    sname character varying(50) NOT NULL,
    tscreated_at timestamp with time zone,
    tsupdated_at timestamp with time zone,
    CONSTRAINT department_pkey PRIMARY KEY (idepart_id)
);

ALTER TABLE emp.department OWNER to postgres01;

-- Table: emp.employee

-- DROP TABLE emp.employee;

CREATE TABLE emp.employee
(
    iemployee_id_pk bigint NOT NULL,
    semployee_id character varying(50) NOT NULL,
    semail character varying(50) NOT NULL,
    sname character varying(120) NOT NULL,
    tsdateofbirth date,
    idepart_id integer NOT NULL,
    tscreated_at timestamp with time zone,
    tsupdated_at timestamp with time zone,
    CONSTRAINT pk_employee_id PRIMARY KEY (iemployee_id_pk),
    CONSTRAINT unique_email UNIQUE (semail),
    CONSTRAINT unique_employee_id UNIQUE (semployee_id),
    CONSTRAINT fk_depart_id FOREIGN KEY (idepart_id)
        REFERENCES emp.department (idepart_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

ALTER TABLE emp.employee OWNER to postgres01;