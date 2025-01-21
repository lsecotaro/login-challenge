# Login-challenge
SignUp and Login api.

Documentation: [LoginChallenge_Documentation.pdf](doc%2FLoginChallenge_Documentation.pdf)


## Compile and run the project

### Using Docker:
```bash
$ docker build -t login-challenge .
$ docker run -p 8080:8080 login-challenge

```

### Local if you have Gradle 7.4 and Java 11:
```bash
$ gradle clean build
$ java -jar build/libs/login-challenge.jar

```

## Test
You can find the JaCoCo coverage report at:
```/build/jacocoHtml/index.html```

## DB: H2

Console: http://localhost:8080/api/h2-console

Set jdbc url with ```jdbc:h2:mem:testdb```

user: sa;

Let password empty

## Configurations:
In application.properties you can configure:
* Port
* Secret key for jwt
* Token expiration time in minutes

