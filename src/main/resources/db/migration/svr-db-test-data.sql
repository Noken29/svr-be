/*INSERT INTO routing_session (id, last_saved, description) VALUES
    (100, '2024-01-01', 'Тест');

SET @depot_latitude = 50.51950435;
SET @depot_longitude = 30.43564783;

INSERT INTO depot (routing_session_id, address_lines, latitude, longitude) VALUES
    (100, 'вулиця Василя Мови, 3, Київ, Україна, 02000', @depot_latitude, @depot_longitude);

INSERT INTO customer (routing_session_id, name, phone_number, address_lines, special_requirements, latitude, longitude) VALUES
    (100, 'Клієнт №1', 'Не Вказано', 'Не Вказано', 'Не Вказано', @depot_longitude + (RAND() * 10) - (RAND() * 5), @depot_longitude + (RAND() * 10) - (RAND() * 5)),
    (100, 'Клієнт №2', 'Не Вказано', 'Не Вказано', 'Не Вказано', @depot_longitude + (RAND() * 10) - (RAND() * 5), @depot_longitude + (RAND() * 10) - (RAND() * 5)),
    (100, 'Клієнт №3', 'Не Вказано', 'Не Вказано', 'Не Вказано', @depot_longitude + (RAND() * 10) - (RAND() * 5), @depot_longitude + (RAND() * 10) - (RAND() * 5)),
    (100, 'Клієнт №4', 'Не Вказано', 'Не Вказано', 'Не Вказано', @depot_longitude + (RAND() * 10) - (RAND() * 5), @depot_longitude + (RAND() * 10) - (RAND() * 5)),
    (100, 'Клієнт №5', 'Не Вказано', 'Не Вказано', 'Не Вказано', @depot_longitude + (RAND() * 10) - (RAND() * 5), @depot_longitude + (RAND() * 10) - (RAND() * 5)),
    (100, 'Клієнт №6', 'Не Вказано', 'Не Вказано', 'Не Вказано', @depot_longitude + (RAND() * 10) - (RAND() * 5), @depot_longitude + (RAND() * 10) - (RAND() * 5)),
    (100, 'Клієнт №7', 'Не Вказано', 'Не Вказано', 'Не Вказано', @depot_longitude + (RAND() * 10) - (RAND() * 5), @depot_longitude + (RAND() * 10) - (RAND() * 5)),
    (100, 'Клієнт №8', 'Не Вказано', 'Не Вказано', 'Не Вказано', @depot_longitude + (RAND() * 10) - (RAND() * 5), @depot_longitude + (RAND() * 10) - (RAND() * 5)),
    (100, 'Клієнт №9', 'Не Вказано', 'Не Вказано', 'Не Вказано', @depot_longitude + (RAND() * 10) - (RAND() * 5), @depot_longitude + (RAND() * 10) - (RAND() * 5)),
    (100, 'Клієнт №10', 'Не Вказано', 'Не Вказано', 'Не Вказано', @depot_longitude + (RAND() * 10) - (RAND() * 5), @depot_longitude + (RAND() * 10) - (RAND() * 5)),
    (100, 'Клієнт №11', 'Не Вказано', 'Не Вказано', 'Не Вказано', @depot_longitude + (RAND() * 10) - (RAND() * 5), @depot_longitude + (RAND() * 10) - (RAND() * 5)),
    (100, 'Клієнт №12', 'Не Вказано', 'Не Вказано', 'Не Вказано', @depot_longitude + (RAND() * 10) - (RAND() * 5), @depot_longitude + (RAND() * 10) - (RAND() * 5)),
    (100, 'Клієнт №13', 'Не Вказано', 'Не Вказано', 'Не Вказано', @depot_longitude + (RAND() * 10) - (RAND() * 5), @depot_longitude + (RAND() * 10) - (RAND() * 5)),
    (100, 'Клієнт №14', 'Не Вказано', 'Не Вказано', 'Не Вказано', @depot_longitude + (RAND() * 10) - (RAND() * 5), @depot_longitude + (RAND() * 10) - (RAND() * 5)),
    (100, 'Клієнт №15', 'Не Вказано', 'Не Вказано', 'Не Вказано', @depot_longitude + (RAND() * 10) - (RAND() * 5), @depot_longitude + (RAND() * 10) - (RAND() * 5));*/

CREATE TEMPORARY TABLE IF NOT EXISTS customer_ids (id INT);
INSERT INTO customer_ids (SELECT id FROM customer ORDER BY RAND() LIMIT 9);

CREATE TEMPORARY TABLE IF NOT EXISTS package_data (
    cost DECIMAL(10, 2),
    weight DECIMAL(10, 2),
    volume DECIMAL(10, 2)
);

INSERT INTO package_data
SELECT
    ROUND(RAND() * (20000 - 50) + 50, 2) AS cost,
    ROUND(RAND() * (2000 - 0.5) + 0.5, 2) AS weight,
    ROUND(RAND() * (1000 - 0.5) + 0.5, 2) AS volume
FROM information_schema.tables AS t1
JOIN information_schema.tables AS t2
JOIN information_schema.tables AS t3
LIMIT 300;

CREATE TEMPORARY TABLE IF NOT EXISTS package_distribution (
    customer_id INT,
    type ENUM('Small', 'Medium', 'Large'),
    cost DECIMAL(10, 2),
    weight DECIMAL(10, 2),
    volume DECIMAL(10, 2)
);

INSERT INTO package_distribution (customer_id, type, cost, weight, volume)
SELECT
    ci.id,
    CASE
        WHEN pd.weight <= 100 THEN 'Small'
        WHEN pd.weight <= 500 THEN 'Medium'
        ELSE 'Large'
        END AS type,
    pd.cost,
    pd.weight,
    pd.volume
FROM customer_ids ci
         JOIN (
    SELECT
        cost,
        weight,
        volume
    FROM package_data
    ORDER BY RAND()
    LIMIT 50
) pd;

INSERT INTO package (customer_id, type, cost, weight, volume)
SELECT customer_id, type, cost, weight, volume FROM package_distribution;

DROP TEMPORARY TABLE IF EXISTS customer_ids;
DROP TEMPORARY TABLE IF EXISTS package_data;
DROP TEMPORARY TABLE IF EXISTS package_distribution;

