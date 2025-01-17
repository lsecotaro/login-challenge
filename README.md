# Login-challenge
SignUp and Login api.

To meet the requirement of using Gradle 7.4 and Java 11, I decided to utilize Docker.

## Compile and run the project

```bash
$ docker build -t login-challenge .
$ docker run -p 8080:8080 login-challenge

```

## DB: H2

Console: http://localhost:8080/api/h2-console

set jdbc url with jdbc:h2:mem:testdb
