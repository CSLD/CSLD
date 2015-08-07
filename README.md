ČSLD
====

Československá larpová databáze je portál pro larpy české a slovenské larpové komunity.

== Set up for local Development==
Set up Java at least 1.7
Set up PostgreSql database csld with user csld and password csld.
Run mvn flyway:migrate -Dflyway.locations=filesystem:sql/migration -Dflyway.url=jdbc:postgresql://localhost:5432/csld -Dflyway.user=csld -Dflyway.password=csld
Run the Project in Wicket development mode. 
Initialize the database by going to the page /testDatabase

Don't forget inside of the tomcat, which the application is run on insert jars for mail.