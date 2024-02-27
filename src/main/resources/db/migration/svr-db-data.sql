insert into fuel_type (id, description, cost) values
    (1, 'A-95+', '54.49'),
    (2, 'A-95', '52.24'),
    (3, 'A-92', '49.10'),
    (4, 'ДП', '51.74'),
    (5, 'ГАЗ', '26.42');

insert into vehicle (id, fuel_type_id, description, carrying_capacity, volume, fuel_consumption) values
    (1, 4, 'MAN/TGX 33.540', 44000, 93, 30),
    (2, 3, 'MAN/TGL 8.180', 30000, 63, 32),
    (3, 2, 'FAW 1041', 4500, 13.68, 10),
    (4, 4, 'HYUNDAI HD78', 4000, 5.145, 8),
    (5, 5, 'DEO LANOS', 800, 1.2, 8),
    (6, 2, 'DEO LANOS', 800, 1.2, 6);