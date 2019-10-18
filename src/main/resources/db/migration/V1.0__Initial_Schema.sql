begin try drop table IMF_WEODATA end try begin catch end catch --TODO: REMOVE WHEN GOING TO PROD
IF OBJECT_ID('dbo.IMF_WEODATA', 'U') IS NULL
begin
    CREATE TABLE IMF_WEODATA (
        [WEO Country Group Code] VARCHAR(3),
        [WEO Subject Code] VARCHAR(255),
        [Country Group Name] VARCHAR(255),
        [Subject Descriptor] VARCHAR(255),
        [Subject Notes] VARCHAR(1500),
        [Units] VARCHAR(255),
        [Scale] VARCHAR(255),
        [Series-specific Notes] VARCHAR(1500),
        [1980] VARCHAR(25),
        [1981] VARCHAR(25),
        [1982] VARCHAR(25),
        [1983] VARCHAR(25),
        [1984] VARCHAR(25),
        [1985] VARCHAR(25),
        [1986] VARCHAR(25),
        [1987] VARCHAR(25),
        [1988] VARCHAR(25),
        [1989] VARCHAR(25),
        [1990] VARCHAR(25),
        [1991] VARCHAR(25),
        [1992] VARCHAR(25),
        [1993] VARCHAR(25),
        [1994] VARCHAR(25),
        [1995] VARCHAR(25),
        [1996] VARCHAR(25),
        [1997] VARCHAR(25),
        [1998] VARCHAR(25),
        [1999] VARCHAR(25),
        [2000] VARCHAR(25),
        [2001] VARCHAR(25),
        [2002] VARCHAR(25),
        [2003] VARCHAR(25),
        [2004] VARCHAR(25),
        [2005] VARCHAR(25),
        [2006] VARCHAR(25),
        [2007] VARCHAR(25),
        [2008] VARCHAR(25),
        [2009] VARCHAR(25),
        [2010] VARCHAR(25),
        [2011] VARCHAR(25),
        [2012] VARCHAR(25),
        [2013] VARCHAR(25),
        [2014] VARCHAR(25),
        [2015] VARCHAR(25),
        [2016] VARCHAR(25),
        [2017] VARCHAR(25),
        [2018] VARCHAR(25),
        [2019] VARCHAR(25),
        [2020] VARCHAR(25),
        [2021] VARCHAR(25),
        [2022] VARCHAR(25),
        [2023] VARCHAR(25),
        [2024] VARCHAR(25),
        [Estimates Start After] VARCHAR(255)
    )
end
------------------------------------------------
begin try drop table WORLDBANK_EASE_COUNTRY_METADATA end try begin catch end catch --TODO: REMOVE WHEN GOING TO PROD
IF OBJECT_ID('dbo.WORLDBANK_EASE_COUNTRY_METADATA', 'U') IS NULL
begin
    CREATE TABLE WORLDBANK_EASE_COUNTRY_METADATA (
        COUNTRY_CODE VARCHAR(50),
        REGION VARCHAR(255),
        INCOME_GROUP VARCHAR(255),
        SPECIAL_NOTES VARCHAR(1500),
        TABLE_NAME VARCHAR(100)
    )
end
------------------------------------------------
begin try drop table WORLDBANK_EASE_COUNTRY_INDEX end try begin catch end catch --TODO: REMOVE WHEN GOING TO PROD
IF OBJECT_ID('dbo.WORLDBANK_EASE_COUNTRY_INDEX', 'U') IS NULL
begin
    CREATE TABLE WORLDBANK_EASE_COUNTRY_INDEX (
        [Country Name] VARCHAR(255),
        [Country Code] VARCHAR(255),
        [Indicator Name] VARCHAR(255),
        [Indicator Code] VARCHAR(255),
        [1960] INT,
        [1961] INT,
        [1962] INT,
        [1963] INT,
        [1964] INT,
        [1965] INT,
        [1966] INT,
        [1967] INT,
        [1968] INT,
        [1969] INT,
        [1970] INT,
        [1971] INT,
        [1972] INT,
        [1973] INT,
        [1974] INT,
        [1975] INT,
        [1976] INT,
        [1977] INT,
        [1978] INT,
        [1979] INT,
        [1980] INT,
        [1981] INT,
        [1982] INT,
        [1983] INT,
        [1984] INT,
        [1985] INT,
        [1986] INT,
        [1987] INT,
        [1988] INT,
        [1989] INT,
        [1990] INT,
        [1991] INT,
        [1992] INT,
        [1993] INT,
        [1994] INT,
        [1995] INT,
        [1996] INT,
        [1997] INT,
        [1998] INT,
        [1999] INT,
        [2000] INT,
        [2001] INT,
        [2002] INT,
        [2003] INT,
        [2004] INT,
        [2005] INT,
        [2006] INT,
        [2007] INT,
        [2008] INT,
        [2009] INT,
        [2010] INT,
        [2011] INT,
        [2012] INT,
        [2013] INT,
        [2014] INT,
        [2015] INT,
        [2016] INT,
        [2017] INT,
        [2018] INT
    )
end
------------------------------------------------
begin try drop table BEA_NIPA end try begin catch end catch --TODO: REMOVE WHEN GOING TO PROD
IF OBJECT_ID('dbo.BEA_NIPA', 'U') IS NULL
begin
    CREATE TABLE BEA_NIPA (
        [TableName] VARCHAR(255),
        [SeriesCode] VARCHAR(255),
        [LineNumber] INT,
        [LineDescription] VARCHAR(255),
        [TimePeriod] INT,
        [METRIC_NAME] VARCHAR(255),
        [CL_UNIT] VARCHAR(255),
        [UNIT_MULT] INT,
        [DataValue] INT,
        [NoteRef] INT
    )
end
------------------------------------------------
begin try drop table BEA_MNE_COUNTRY end try begin catch end catch --TODO: REMOVE WHEN GOING TO PROD
IF OBJECT_ID('dbo.BEA_MNE_COUNTRY', 'U') IS NULL
begin
    CREATE TABLE BEA_MNE_COUNTRY (
        [DIRECTIONOFINVESTMENT] VARCHAR(255),
        [OwnershipLevel] INT,
        [Year] INT,
        [SeriesID] INT,
        [SeriesName] VARCHAR(255),
        [Row] VARCHAR(255),
        [Column] VARCHAR(255),
        [RowCode] VARCHAR(255),
        [ColumnCode] VARCHAR(255),
        [TableScale] VARCHAR(255),
        [DataValueUnformatted] VARCHAR(255),
        [DataValue] VARCHAR(255)
    )
end
------------------------------------------------
begin try drop table BEA_MNE_COUNTRY_BY_INDUSTRY end try begin catch end catch --TODO: REMOVE WHEN GOING TO PROD
IF OBJECT_ID('dbo.BEA_MNE_COUNTRY_BY_INDUSTRY', 'U') IS NULL
begin
    CREATE TABLE BEA_MNE_COUNTRY_BY_INDUSTRY (
        [DIRECTIONOFINVESTMENT] VARCHAR(255),
        [OwnershipLevel] INT,
        [Year] INT,
        [SeriesID] INT,
        [SeriesName] VARCHAR(255),
        [Row] VARCHAR(255),
        [Column] VARCHAR(255),
        [RowCode] VARCHAR(255),
        [ColumnCode] VARCHAR(255),
        [TableScale] VARCHAR(255),
        [DataValueUnformatted] VARCHAR(255),
        [DataValue] VARCHAR(255)
    )
end
------------------------------------------------
begin try drop table BEA_MNE_COUNTRY_UBO_BY_INDUSTRY end try begin catch end catch --TODO: REMOVE WHEN GOING TO PROD
IF OBJECT_ID('dbo.BEA_MNE_COUNTRY_UBO_BY_INDUSTRY', 'U') IS NULL
begin
    CREATE TABLE BEA_MNE_COUNTRY_UBO_BY_INDUSTRY (
        [DIRECTIONOFINVESTMENT] VARCHAR(255),
        [OwnershipLevel] INT,
        [Year] INT,
        [SeriesID] INT,
        [SeriesName] VARCHAR(255),
        [Row] VARCHAR(255),
        [Column] VARCHAR(255),
        [RowCode] VARCHAR(255),
        [ColumnCode] VARCHAR(255),
        [TableScale] VARCHAR(255),
        [DataValueUnformatted] VARCHAR(255),
        [DataValue] VARCHAR(255)
    )
end
------------------------------------------------
begin try drop table BEA_MNE_INDUSTRY end try begin catch end catch --TODO: REMOVE WHEN GOING TO PROD
IF OBJECT_ID('dbo.BEA_MNE_INDUSTRY', 'U') IS NULL
begin
    CREATE TABLE BEA_MNE_INDUSTRY (
        [DIRECTIONOFINVESTMENT] VARCHAR(255),
        [OwnershipLevel] INT,
        [Year] INT,
        [SeriesID] INT,
        [SeriesName] VARCHAR(255),
        [Row] VARCHAR(255),
        [Column] VARCHAR(255),
        [RowCode] VARCHAR(255),
        [ColumnCode] VARCHAR(255),
        [TableScale] VARCHAR(255),
        [DataValueUnformatted] VARCHAR(255),
        [DataValue] VARCHAR(255)
    )
end
------------------------------------------------
begin try drop table BEA_ITA end try begin catch end catch --TODO: REMOVE WHEN GOING TO PROD
IF OBJECT_ID('dbo.BEA_ITA', 'U') IS NULL
begin
    CREATE TABLE BEA_ITA (
        [Indicator] VARCHAR(255),
        [AreaOrCountry] VARCHAR(255),
        [Frequency] VARCHAR(255),
        [Year] INT,
        [TimeSeriesId] VARCHAR(255),
        [TimeSeriesDescription] VARCHAR(255),
        [TimePeriod] VARCHAR(255),
        [CL_UNIT] VARCHAR(255),
        [UNIT_MULT] VARCHAR(255),
        [DataValue] INT
    )
end
------------------------------------------------
begin try drop table FTA_TARIFF_RATES_ACTIVE end try begin catch end catch --TODO: REMOVE WHEN GOING TO PROD
IF OBJECT_ID('dbo.FTA_TARIFF_RATES_ACTIVE', 'U') IS NULL
begin
    CREATE TABLE FTA_TARIFF_RATES_ACTIVE (
       ID  INTEGER
      ,TL VARCHAR(255)
      ,TL_Desc VARCHAR(MAX)
      ,HS6 VARCHAR(255)
      ,HS6_Desc VARCHAR(MAX)
      ,Sector_Code VARCHAR(255)
      ,Base_Rate VARCHAR(255)
      ,Base_Rate_Alt VARCHAR(255)
      ,Final_Year VARCHAR(255)
      ,TLs_Per_6_Digit INTEGER
      ,Staging_Basket INTEGER
      ,TRQ_Quota VARCHAR(255)
      ,TRQ_Note VARCHAR(MAX)
      ,Tariff_Eliminated BIT
      ,Product_Type INTEGER
      ,PartnerName VARCHAR(255)
      ,ReporterName VARCHAR(255)
      ,StagingBasket VARCHAR(255)
      ,StagingBasketId INTEGER
      ,PartnerStartYear INTEGER
      ,ReporterStartYear  INTEGER
      ,PartnerAgreementName VARCHAR(255)
      ,ReporterAgreementName VARCHAR(255)
      ,PartnerAgreementApproved  VARCHAR(255)
      ,ReporterAgreementApproved VARCHAR(255)
      ,QuotaName VARCHAR(255)
      ,ProductType VARCHAR(255)
      ,Y2004 VARCHAR(255)
      ,Alt_2004 VARCHAR(255)
      ,Y2005 VARCHAR(255)
      ,Alt_2005 VARCHAR(255)
      ,Y2006 VARCHAR(255)
      ,Alt_2006 VARCHAR(255)
      ,Y2007 VARCHAR(255)
      ,Alt_2007 VARCHAR(255)
      ,Y2008 VARCHAR(255)
      ,Alt_2008 VARCHAR(255)
      ,Y2009 VARCHAR(255)
      ,Alt_2009 VARCHAR(255)
      ,Y2010 VARCHAR(255)
      ,Alt_2010 VARCHAR(255)
      ,Y2011 VARCHAR(255)
      ,Alt_2011 VARCHAR(255)
      ,Y2012 VARCHAR(255)
      ,Alt_2012 VARCHAR(255)
      ,Y2013 VARCHAR(255)
      ,Alt_2013 VARCHAR(255)
      ,Y2014 VARCHAR(255)
      ,Alt_2014 VARCHAR(255)
      ,Y2015 VARCHAR(255)
      ,Alt_2015 VARCHAR(255)
      ,Y2016 VARCHAR(255)
      ,Alt_2016 VARCHAR(255)
      ,Y2017 VARCHAR(255)
      ,Alt_2017 VARCHAR(255)
      ,Y2018 VARCHAR(255)
      ,Alt_2018 VARCHAR(255)
      ,Y2019 VARCHAR(255)
      ,Alt_2019 VARCHAR(255)
      ,Y2020 VARCHAR(255)
      ,Alt_2020 VARCHAR(255)
      ,Y2021 VARCHAR(255)
      ,Alt_2021 VARCHAR(255)
      ,Y2022 VARCHAR(255)
      ,Alt_2022 VARCHAR(255)
      ,Y2023 VARCHAR(255)
      ,Alt_2023 VARCHAR(255)
      ,Y2024 VARCHAR(255)
      ,Alt_2024 VARCHAR(255)
      ,Y2025 VARCHAR(255)
      ,Alt_2025 VARCHAR(255)
      ,Y2026 VARCHAR(255)
      ,Alt_2026 VARCHAR(255)
      ,Y2027 VARCHAR(255)
      ,Alt_2027 VARCHAR(255)
      ,Y2028 VARCHAR(255)
      ,Alt_2028 VARCHAR(255)
      ,Y2029 VARCHAR(255)
      ,Alt_2029 VARCHAR(255)
      ,Y2030 VARCHAR(255)
      ,Alt_2030 VARCHAR(255)
      ,Y2031 VARCHAR(255)
      ,Alt_2031 VARCHAR(255)
      ,Y2032 VARCHAR(255)
      ,Alt_2032 VARCHAR(255)
      ,Y2033 VARCHAR(255)
      ,Alt_2033 VARCHAR(255)
      ,Y2034 VARCHAR(255)
      ,Alt_2034 VARCHAR(255)
      ,Y2035 VARCHAR(255)
      ,Alt_2035 VARCHAR(255)
      ,Y2036 VARCHAR(255)
      ,Alt_2036 VARCHAR(255)
      ,Y2037 VARCHAR(255)
      ,Alt_2037 VARCHAR(255)
      ,Y2038 VARCHAR(255)
      ,Alt_2038 VARCHAR(255)
      ,Y2039 VARCHAR(255)
      ,Alt_2039 VARCHAR(255)
      ,Y2040 VARCHAR(255)
      ,Alt_2040 VARCHAR(255)
      ,Y2041 VARCHAR(255)
      ,Alt_2041 VARCHAR(255)
    )
END
------------------------------------------------
begin try drop table OTEXA_DATA_SET_CAT end try begin catch end catch --TODO: REMOVE WHEN GOING TO PROD
IF OBJECT_ID('dbo.OTEXA_DATA_SET_CAT', 'U') IS NULL
begin
    CREATE TABLE OTEXA_DATA_SET_CAT (
       CTRYNUM  INTEGER
      ,CAT INTEGER
      , CNAME VARCHAR(255)
      , SYEF DECIMAL(5,2)
      , YR INTEGER
      , MON INTEGER
      , HEADER VARCHAR(25)
      , VAL VARCHAR(25)
    )
end