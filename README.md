# ITA Dataloader
A tool that helps import (stage) data for reporting purposes

## APIs
- /api/configuration: Displays configuration urls
- /api/ingest: Saves data sources to storage
- /api/storage-content-url: Returns a url that returns a list of all files in storage
- /api/storage-content: Returns a list of urls to the files in storage

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
    - AZURE_DB_URL: MS SQL Database jdbc connection url
    - AZURE_DB_USERNAME: MS SQL Database username
    - AZURE_DB_PASSWORD: MS SQL Database password

## General Notes
 - The data-pipeline directory is not utilized in this project, it's a temporary backup of the Azure data-pipeline
