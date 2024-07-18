#------------------------------Postgres-Image----------------------------#

## Use an official PostgreSQL image as a base
#FROM postgres:14-alpine
#
## Copy the SQL dump into the container
#COPY ems_dump.sql /docker-entrypoint-initdb.d/
#
## Environment variables for PostgreSQL configuration
#ENV POSTGRES_PASSWORD=postgres
#ENV POSTGRES_USER=postgres
#ENV POSTGRES_DB=ems
#
## Expose PostgreSQL default port
#EXPOSE 5433

#-------------------------------App-Image------------------------------#

#FROM openjdk:17-jdk-alpine
#WORKDIR /event-management-app
#COPY target/event-management-0.0.1-SNAPSHOT.jar event-management-app.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "event-management-app.jar"]