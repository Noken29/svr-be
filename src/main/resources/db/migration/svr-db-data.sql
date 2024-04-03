insert into fuel_type (id, description, cost) values
    (1, 'A-95+', '54.49'),
    (2, 'A-95', '52.24'),
    (3, 'A-92', '49.10'),
    (4, 'ДП', '51.74'),
    (5, 'ГАЗ', '26.42');

insert into vehicle (id, fuel_type_id, description, carrying_capacity, volume, fuel_consumption) values
    (1, 4, 'MAN/TGX 33.540', 44000, 85, 30),
    (2, 3, 'MAN/TGL 8.180', 30000, 51, 32),
    (3, 2, 'FAW 1041', 4500, 13.68, 10),
    (4, 4, 'HYUNDAI HD78', 4000, 5.145, 8),
    (5, 5, 'DEO LANOS', 800, 1.2, 8),
    (6, 2, 'Ford Transit', 1800, 8.2, 11),
    (7, 1, 'Toyota Hilux', 1000, 2.5, 12),
    (8, 3, 'Ford F-150', 1500, 2.3, 14),
    (9, 5, 'Volkswagen Transporter', 2000, 4.3, 9),
    (10, 1, 'Nissan Navara', 1200, 2.8, 11),
    (11, 2, 'Mercedes-Benz Sprinter', 2000, 10.5, 9),
    (12, 1, 'Volvo FH16', 45000, 110, 28),
    (13, 3, 'Isuzu NPR-HD', 3500, 14.2, 11),
    (14, 4, 'Scania R730', 40000, 95, 32),
    (15, 5, 'Chevrolet Silverado', 1800, 3.6, 15),
    (16, 2, 'Renault Master', 1600, 9.8, 10),
    (17, 3, 'Mitsubishi Fuso Canter', 2500, 12.5, 13),
    (18, 1, 'Kenworth W900', 48000, 105, 30),
    (19, 4, 'Iveco Stralis', 42000, 100, 29),
    (20, 5, 'Dodge Ram', 2200, 4.2, 17);