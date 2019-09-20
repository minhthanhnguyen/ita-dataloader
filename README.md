# SUSA Stats Dataloader
A tool that helps import (stage) data for reporting purposes

## APIs
- /api/config: Displays configuration urls
- /api/save-datasets: Saves datasets to storage
- /api/storage-content-url: Returns a url that returns a list of all files in storage
- /api/storage-content: Returns a list of urls to the files in storage

## Deployment
- A database stub named select_usa must exist prior to deploying this project. 
  - For an Azure deployment, run the following query against the master database: ```CREATE DATABASE select_usa (EDITION = 'datawarehouse', SERVICE_OBJECTIVE='DW100c');```
- Flyway doesn't currently work with Azure SQL Data Warehouse, so the scripts in the /src/main/resources/db/migrations directory will need to be run manually against the database.
- Connection parameters for the database must be stored in the following environment variables:
        - FLYWAY_URL: The jdbc url to use to connect to the database
        - FLYWAY_USER: The user to use to connect to the database
        - FLYWAY_PASSWORD: The password to use to connect to the database
