# Purchase Transaction

## Summary

This assessments aims to provide a purchase and exchange transactions platform utilizing
Treasury Reporting Rates of Exchange API. The application returns, after consuming Rates of Exchange API,
the amount of Dollar in the transaction converted to a desired currency.

### API documentation

```
   https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange
```

## How to run the project

- Clone the repository
- Move into repository folder
- Build and start the application using Docker Compose
```
docker compose up
```

This will build and up two containers one containing Postgres and another containing the application artifact.
- The application will be exposed at the following address
```
http://localhost:8080/transaction
```
## Endpoints
- Create a purchase:
    ``` 
      http://localhost:8080/transaction/purchase
   ```
- Body:
  ```json
  {"amount":100.00, "description":"desc up to 50 char","transactionDate": "2024-12-19"}
  ```
- Retrieve a purchase:
  ``` 
   http://localhost:8080/transaction/retrieve/{{purchase uuid}}
  ``` 
- Retrieve a purchase in a desired currency
  ```
    http://localhost:8080/transaction/retrieve?id={{purchase uuid}}&currency={{desired currency (i.e. Real}}
  ```
    - You can check the currencies supported in [this link](https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange).

## Testing

The application contains integration and unit tests using H2 Database, MockMvc and Mockito.
You can run all the tests using your favorite IDE (i.e. IntelliJ) or run

- Windows
```
.\gradlew.bat clean test --info
```

- Linux or Mac
```
./gradlew clean test --info
```

## Tech Stack

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)
![Postgres](https://img.shields.io/badge/Postgres-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![H2 Database](https://img.shields.io/badge/H2%20Database-07405E?style=for-the-badge&logo=Databricks&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200.svg?style=for-the-badge&logo=Flyway&logoColor=white)
![Jacoco](https://img.shields.io/badge/Jacoco-F01F7A.svg?style=for-the-badge&logo=Codecov&logoColor=white)