# ITA Dataloader
A tool that helps import (stage) data for reporting purposes

## Local Development
Steps to run this application on you local for development purposes

**Prerequisites** 
 - Java 8
 - Gradle
 - NPM (`cd client` then `npm install`)

**Backend** 
 - `gradle bootRun `, http://localhost:8080

**Frontend** `cd client && npm start`, http://localhost:8081

## Running Tests

**Backend** `./gradlew test`

**Frontend**  `cd client && npm test`

## Build
 - CI/CD build script ```./build.sh```
 - Local build script ```./build-local.sh```

## Deployment Configuration Notes
1. Create a Blob Storage Account (Don't enable Hierarchical Namespace)
1. An Azure AD App Registration is required procure an OAuth Client ID and Client Secret
1. Create a SQL Database
1. Create a Data Factory and deploy the configuration in the `pipelines` directory
1. Create an App Registration for the Data Factory with the Contributor Role
1. Deploy https://github.com/InternationalTradeAdministration/ita-datafactory-log-extractor
1. Create a Container Registry
1. The following environment variables need to exist:
    - AZURE_OAUTH_CLIENT_ID: Active Directory Client ID
    - AZURE_OAUTH_CLIENT_SECRET: Active Directory Client Secret
    - AZURE_STORAGE_ACCOUNT: Data Lake Storage Account
    - AZURE_STORAGE_ACCOUNT_KEY: Data Lake Storage Account Key
    - DATAFACTORY_STATUS_URL: The URL to this ITA Data Factory Log Extractor - App Function
    - FLYWAY_URL: The jdbc connection to a AZURE SQL Database
    - FLYWAY_USER: Username to the AZURE SQL Database
    - FLYWAY_PASSWORD: Password to the AZURE SQL Database

## Deploy
 - K8 deploy script ```./deploy-aks.sh```
 - Container Instance deploy script ```./deploy.sh```

## Data Factory Log Extractor
This App Function allows us to retrieve the last known status of a given pipeline within a given data factory:
    https://github.com/InternationalTradeAdministration/ita-datafactory-log-extractor

## Database Notes
 - Flyway is used to manage the state of the database, that's all. This application does not otherwise interact with the database
 - SQL Scripts for updating the Database can be found here:  /src/main/resources/db/migration
 