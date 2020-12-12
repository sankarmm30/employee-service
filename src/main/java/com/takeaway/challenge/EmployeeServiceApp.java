package com.takeaway.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Sankar Manthiram <sankar.mm30@gmail.com>
 * @since 2020-12
 */
@SpringBootApplication
@EnableTransactionManagement
public class EmployeeServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeServiceApp.class, args);
    }
}
