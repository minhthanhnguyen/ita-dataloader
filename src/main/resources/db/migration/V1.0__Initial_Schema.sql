begin try drop table IMF_WEODATA end try begin catch end catch --REMOVE WHEN GOING TO PROD
IF OBJECT_ID('dbo.IMF_WEODATA', 'U') IS NULL
begin
    CREATE TABLE IMF_WEODATA (
        [WEO Country Group Code] varchar(3),
        [WEO Subject Code] varchar(255),
        [Country Group Name] varchar(255),
        [Subject Descriptor] varchar(255),
        [Subject Notes] varchar(1500),
        [Units] varchar(255),
        [Scale] varchar(255),
        [Series-specific Notes] varchar(1500),
        [1980] varchar(25),
        [1981] varchar(25),
        [1982] varchar(25),
        [1983] varchar(25),
        [1984] varchar(25),
        [1985] varchar(25),
        [1986] varchar(25),
        [1987] varchar(25),
        [1988] varchar(25),
        [1989] varchar(25),
        [1990] varchar(25),
        [1991] varchar(25),
        [1992] varchar(25),
        [1993] varchar(25),
        [1994] varchar(25),
        [1995] varchar(25),
        [1996] varchar(25),
        [1997] varchar(25),
        [1998] varchar(25),
        [1999] varchar(25),
        [2000] varchar(25),
        [2001] varchar(25),
        [2002] varchar(25),
        [2003] varchar(25),
        [2004] varchar(25),
        [2005] varchar(25),
        [2006] varchar(25),
        [2007] varchar(25),
        [2008] varchar(25),
        [2009] varchar(25),
        [2010] varchar(25),
        [2011] varchar(25),
        [2012] varchar(25),
        [2013] varchar(25),
        [2014] varchar(25),
        [2015] varchar(25),
        [2016] varchar(25),
        [2017] varchar(25),
        [2018] varchar(25),
        [2019] varchar(25),
        [2020] varchar(25),
        [2021] varchar(25),
        [2022] varchar(25),
        [2023] varchar(25),
        [2024] varchar(25),
        [Estimates Start After] varchar(255)
    )
end

------------------------------------------------

begin try drop table WORLDBANK_EASE_COUNTRY_METADATA end try begin catch end catch --REMOVE WHEN GOING TO PROD
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

begin try drop table WORLDBANK_EASE_COUNTRY_INDEX end try begin catch end catch --REMOVE WHEN GOING TO PROD
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