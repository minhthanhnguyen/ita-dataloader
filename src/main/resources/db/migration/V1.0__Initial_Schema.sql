create TABLE IMF_WEODATA (
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

create TABLE WORLDBANK_EASE_COUNTRY_METADATA (
    COUNTRY_CODE VARCHAR(50),
    REGION VARCHAR(255),
    INCOME_GROUP VARCHAR(255),
    SPECIAL_NOTES VARCHAR(1500),
    TABLE_NAME VARCHAR(100)
)

create TABLE WORLDBANK_EASE_COUNTRY_INDEX (
    [Country Name] VARCHAR(255),
    [Country Code] VARCHAR(255),
    [Indicator Name] VARCHAR(255),
    [Indicator Code] VARCHAR(255),
    [1960] VARCHAR(25),
    [1961] VARCHAR(25),
    [1962] VARCHAR(25),
    [1963] VARCHAR(25),
    [1964] VARCHAR(25),
    [1965] VARCHAR(25),
    [1966] VARCHAR(25),
    [1967] VARCHAR(25),
    [1968] VARCHAR(25),
    [1969] VARCHAR(25),
    [1970] VARCHAR(25),
    [1971] VARCHAR(25),
    [1972] VARCHAR(25),
    [1973] VARCHAR(25),
    [1974] VARCHAR(25),
    [1975] VARCHAR(25),
    [1976] VARCHAR(25),
    [1977] VARCHAR(25),
    [1978] VARCHAR(25),
    [1979] VARCHAR(25),
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
    [2018] VARCHAR(25)
)

create TABLE BEA_NIPA (
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

create TABLE BEA_MNE_COUNTRY (
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

create TABLE BEA_MNE_COUNTRY_BY_INDUSTRY (
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

create TABLE BEA_MNE_COUNTRY_UBO_BY_INDUSTRY (
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

create TABLE BEA_MNE_INDUSTRY (
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

create TABLE BEA_ITA (
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

create TABLE OTEXA_DATA_SET_CAT (
   CTRY_ID INTEGER
  , CAT_ID INTEGER
  , SYEF DECIMAL(5,2)
  , HEADER_ID VARCHAR(25)
  , VAL DECIMAL(30, 10)
)

create TABLE OTEXA_CATEGORY_REF (
   CAT_ID  INTEGER
  , CAT_DESCRIPTION VARCHAR(255)
)

create TABLE OTEXA_COUNTRY_REF (
   CTRY_ID  INTEGER
  , CTRY_DESCRIPTION VARCHAR(255)
)

create TABLE OTEXA_HEADER_REF (
   HEADER_ID  VARCHAR(15)
  , YR INTEGER
  , HEADER_DESCRIPTION VARCHAR(255)
  , HEADER_TYPE VARCHAR(255)
)
go

create VIEW OTEXA_DATA_SET_CAT_VW
as
select details.CTRY_ID
    , details.CAT_ID
    , details.HEADER_ID
    , case when hdr.HEADER_TYPE = 'UNITS' then details.VAL / details.SYEF else details.VAL end as ADJ_VAL
from OTEXA_DATA_SET_CAT details
inner join OTEXA_HEADER_REF hdr
on details.HEADER_ID = hdr.HEADER_ID;
go