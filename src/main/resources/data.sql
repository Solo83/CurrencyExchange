INSERT INTO currencies (code, FullName, sign)
VALUES ('EUR', 'Euro', '€'),
       ('USD', 'United States Dollar', '$'),
       ('RUB', 'Russian Ruble', '₽'),
       ('GBP', 'Pound Sterling', '£');

INSERT INTO exchangerates (BaseCurrencyId, TargetCurrencyId, rate)
VALUES (2, 1, 1.0835),
       (3, 2, 0.01101);