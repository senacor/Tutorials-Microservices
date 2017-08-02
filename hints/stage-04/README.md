# Hints for Tutorial stage 04

## Overview

Basically you just do all stage 00 till stage 03 again. In the end you should have another project that looks something like this:

```
com.senacor.bitc
             +- demo
                 +- AccountingApplication.java
                 |
                 +- domain
                 |   +- Account.java
                 |   +- AccountRepository.java
                 |
                 +- rest
                 |   +- AccountController.java
                 |
                 +- service
                     +- AccountService.java
```

Note: If you want to be lazy you can create a copy of the demo project and apply refactoring to create the accounting project:

1. Create a copy of the demo project folder, rename the base-folder to ```accounting```
2. Import the "accounting" project into IntelliJ IDEA through ```File >> New >> Module from Existing Sources...```, select the accounting folder
3. Refactoring... 

## Account database table and entity

The accounting service will use a separate database:

1. Create another database through the mysql console (see stage 02).
3. Configure the database connection for flyway in the accounting project.
2. Provide migration scripts that create the account table.

The database table should have at least these attributes:

```SQL
CREATE TABLE accountingdb.account (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    account_type VARCHAR(100) NOT NULL,
    customer_id INT NOT NULL,
);
```

Accordingly your account entity will look something like this:

```Java
@Data
@Builder
@NoArgsConstructor // needed for JPA
@AllArgsConstructor // needed for builder (because of NoArgsConstructor)
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    @Column(name = "account_type", nullable = false)
    public AccountType accountType;
    @Column(name = "customer_id", nullable = false)
    public Integer customerId;

}
```

## Account controller and service