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
```./build.sh```

## Deploy
```./deploy.sh```

## Production Deployment Notes
 - An Azure Blob Storage account is required
 - An Azure AD App Registration is required procure an OAuth Client ID and Client Secret
 - A MS SQL Server Database needs to exist with a stub named ita-data
 - The following environment variables need to exist:
    - AZURE_OAUTH_CLIENT_ID: Active Directory Client ID
    - AZURE_OAUTH_CLIENT_SECRET: Active Directory Client Secret
    - AZURE_STORAGE_ACCOUNT: Data Lake Storage Account
    - AZURE_STORAGE_ACCOUNT_KEY: Data Lake Storage Account Key

## General Notes
 - The data-pipeline directory is not utilized in this project, it's a temporary backup of the Azure data-pipeline
