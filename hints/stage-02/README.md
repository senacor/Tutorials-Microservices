# Hints for Tutorial stage 02

## MySQL configuration and database creation

### Installation (should already be done in VM)

```
sudo apt-get update
sudo apt-get install mysql-server
```

### Configuration

Run the following commands to configure the mysql server installation:

```
sudo mysql_secure_installation
```


For details on the ```sudo mysql_secure_installation``` command setup steps you can check the subsection "MySQL installation dump" below.

Once mysql server was installed you can access the mysql command line like this:
```
mysql -h localhost -u root -p
```

You will have to enter the root password; for the tutorial we use ```mysql``` as root password.

#### MySQL installation dump:
```
vagrant@ethdev:~$ sudo mysql_secure_installation

Securing the MySQL server deployment.

Enter password for user root: mysql

VALIDATE PASSWORD PLUGIN can be used to test passwords
and improve security. It checks the strength of password
and allows the users to set only those passwords which are
secure enough. Would you like to setup VALIDATE PASSWORD plugin?

Press y|Y for Yes, any other key for No: n
Using existing password for root.
Change the password for root ? ((Press y|Y for Yes, any other key for No) : n

 ... skipping.
By default, a MySQL installation has an anonymous user,
allowing anyone to log into MySQL without having to have
a user account created for them. This is intended only for
testing, and to make the installation go a bit smoother.
You should remove them before moving into a production
environment.

Remove anonymous users? (Press y|Y for Yes, any other key for No) : n

 ... skipping.


Normally, root should only be allowed to connect from
'localhost'. This ensures that someone cannot guess at
the root password from the network.

Disallow root login remotely? (Press y|Y for Yes, any other key for No) : y
Success.

By default, MySQL comes with a database named 'test' that
anyone can access. This is also intended only for testing,
and should be removed before moving into a production
environment.


Remove test database and access to it? (Press y|Y for Yes, any other key for No) : n

 ... skipping.
Reloading the privilege tables will ensure that all changes
made so far will take effect immediately.

Reload privilege tables now? (Press y|Y for Yes, any other key for No) : y
Success.

All done!
```

### Create the demodb database

In the mysql console run:
```SQL
CREATE SCHEMA demodb;
```

## Flyway configuration and scripting

### Configure the demo project for Flyway

Add the following dependencies to your gradle build file in the demo project:

1. dependency for mysql
2. dependencies for flyway
3. dependency for data-jpa  

Note that the dependency for data-jpa can be omitted if you configure gradle to use the flyway-plugin and you configure the flyway database in the dependencies instead of the application.yml file. If you want to configure flyway with the plugin you can take a look at the [flyway documentation for the setup in gradle](https://flywaydb.org/getstarted/firststeps/gradle). 

The complete ```build.gradle``` file for stage 02:
```
buildscript {
	ext {
		springBootVersion = '1.5.4.RELEASE'
	}
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
		classpath('io.spring.gradle:dependency-management-plugin:0.5.4.RELEASE')
	}
}

apply plugin: 'java'
apply plugin: 'idea'
apply plugin: 'org.springframework.boot'

version = '0.0.1-SNAPSHOT'
sourceCompatibility = 1.8

repositories {
	mavenCentral()
}


dependencies {
	compile('org.springframework.boot:spring-boot-starter-web')
	compile('org.springframework.cloud:spring-cloud-starter-config')
	compile('org.springframework.boot:spring-boot-starter-actuator')
	compile('org.yaml:snakeyaml')
	compile('org.flywaydb:flyway-core')
	compile("mysql:mysql-connector-java:5.1.13")
	compile('org.springframework.boot:spring-boot-starter-data-jpa')
	testCompile('org.springframework.boot:spring-boot-starter-test')
}


dependencyManagement {
	imports {
		mavenBom "org.springframework.cloud:spring-cloud-dependencies:Camden.SR5"
	}
}

```

Note: Be careful with the recent mysql-jdbc drivers, some have a problems with the server timezone so the migration fails (version ```8.0.7-dmr``` has that problem). With the above version (```5.1.13```) it should work.

Don't forget to run a "refresh" in your gradle view (in IntelliJ IDEA).

### Configure the database

For stage 02 we add an ```application.yml``` file to the ```resources``` folder of the demo project, to define the connection to the database: 

```YAML
spring:
  datasource:
    url: 'jdbc:mysql://localhost:3306/demodb'
    username: 'root'
    password: 'mysql'
    driver-class-name: 'com.mysql.jdbc.Driver'
```

Note that you could also put this configuration into the service's ```demo-dev.yml``` configuration file on the config server. For this tutorial we put it in the project for now, but it is of course valid to let the config server hold this information.

### Create your first migration

Once flyway is configured in your project you can create your first migration. Migrations are SQL files that change something in the database (e.g. create a table, insert data into a table, ...).

For more information on migrations refer to the [Flyway migration documentation](https://flywaydb.org/documentation/migration/sql).

The first migration should create a customer table. The migration file is to be created like this:

```
[demo-project-root]/src/main/resources/db/migration/V1__Create_customer_table.sql
```

SQL statement:
```SQL
CREATE TABLE demodb.customer (
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL
);
```

### Run the first migration

To run the migration you can just startup the project. Flyway will automatically check if there are migrations that have to be applied to the database. If so it will run those migrations.

#### Running a migration with the flyway plugin (optional)

If you configure flyway using the flyway-plugin you can run migrations through gradle tasks without starting the project itself. Navigate to the demo project's base folder and run:
```
./gradlew flywayMigrate -i
```

You should retrieve a ```BUILD SUCCESSFULL``` message at the end of the migration.

Note: The mysql schema "demodb" (as specified in the flyway configuration in the build.gradle) can be automatically created for you upon the first migration. Usually you would use an already existing database/schema that you create *before* you run the first migration though. If you let flyway create the schema for you the first migration entry in the ```schema_version``` table that flyway automatically creates in the database for you will be a migration without a version number.

#### If something goes wrong...

If something goes wrong with executing your script you might have to take a look into the database and remove the migration entry in the ```schema_version``` table that flyway creates automatically after running a migration. Once a migration was recorded there you cannot apply the same migration to the database again. Unfortunately it is always recorded, even if an error occurs.  Take a look for the next section for accessing the database and checkout for migration entries.

You can also just drop the complete database. Connect to your mysql server through the command line like this:
```
mysql -h localhost -u root -p
```

You will have to enter the root password.

Statement to drop the database:
```SQL
DROP DATABASE demodb;
```

More information on mysql (SQL) commands can be found in the [mysql documentation](https://dev.mysql.com/doc/refman/5.7/en/drop-database.html).

### Take a look at what happened in the database

1. install the "DB Navigator" IDEA plugin (alternatively you can also connect to the database using e.g. [dBeaver]())
2. Create a connection to your mysql database
3. Show the database in the DB Browser
4. Open an SQL console on the database

The table ```schema_versions``` is create by flyway in the database. It contains information on the migrations that were already executed. 
```SQL
select * from schema_version;
```

### Create more migrations

The second migration should add some data to the customer table. Add the migration file:

```
[demo-project-root]/src/main/resources/db/migration/V2__InsertInto_customer_table_BudAndTerence.sql
```

SQL statements:

```SQL
INSERT INTO demodb.customer
  (id, first_name, last_name, birth_date)
  VALUES (1, "Bud", "Spencer", STR_TO_DATE('31-10-1929', '%d-%m-%Y')
);

INSERT INTO demodb.customer
  (id, first_name, last_name, birth_date)
  VALUES (2, "Terence", "Hill", STR_TO_DATE('29-03-1939', '%d-%m-%Y')
);
```

For every migration you add you will have to start the project for the changes to take effect. Note that you can also add several migration files; flyway will apply all of them upon startup of the application. 

If you run the migrations as gradle task you will have to run ```./gradlew flywayMigrate -i``` again for *every* migration to take effect.

Feel free to add more migrations. In the reference solution we added a third migration that adds another column to the customer table. 