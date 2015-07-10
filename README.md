ČSLD
====

Československá larpová databáze je portál pro larpy české a slovenské larpové komunity.

== Set up for local Development==
Set up Java at least 1.7
Set up PostgreSql database csld with user csld and password csld.
Run mvn flyway:migrate -Dflyway.locations=filesystem:sql/migration -Dflyway.url=jdbc:postgresql://localhost:5432/csld -Dflyway.user=csld -Dflyway.password=csld
Initialize the database with sql/setUp/testValues.sql script

Don't forget inside of the tomcat, which the application is run on insert jars for mail.