CREATE TABLE IF NOT EXISTS Currencies (
                                          ID       INTEGER PRIMARY KEY AUTOINCREMENT,
                                          Code     TEXT    UNIQUE,
                                          FullName TEXT,
                                          Sign     TEXT
);

CREATE TABLE IF NOT EXISTS ExchangeRates (
                                             ID               INTEGER     PRIMARY KEY AUTOINCREMENT,
                                             BaseCurrencyId   INTEGER     REFERENCES Currencies (ID),
                                             TargetCurrencyId INTEGER     REFERENCES Currencies (ID),
                                             Rate             NUMERIC (6)
);

CREATE UNIQUE INDEX Uniq
    ON ExchangeRates (BaseCurrencyId, TargetCurrencyId);