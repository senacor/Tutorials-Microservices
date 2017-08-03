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

The account service and controller are to be implemented like the account controller and service in stage 03. 

The account controller should offer the following request mappings:

 | HTTP Verb | Request Mapping | Does what? |
 | --------- | :-------------- | :--------- |
 | GET       | /account/{accountId} | returns account with given account ID |
 | GET       | /account?customerId={customerId} | returns the accounts assigned to the customer with the given ID |
 | POST      | /account | creates a new account, the request body should contain the account formatted as JSON |

The account service should offer at least the following functionality:

```Java
public interface AccountService {

    Account loadAccountById(Integer accountId);

    Account saveAccount(Account account);

    List<Account> findAccountsByCustomerId(Integer customerId);
}
```

For the saving an account the customer ID is provided. Note that you should not verify if the customer exists (using the customer endpoint of the demo project) yet. This will be done in the next stage. In the reference solution we defined a TODO for this:

```Java
@Override
public Account saveAccount(Account account) {

    // TODO: Call the customer service and check it the customer with the given ID exists!

    return accountRepository.save(account);
}
```




