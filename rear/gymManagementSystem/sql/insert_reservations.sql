-- 插入预约数据
INSERT INTO reservations (venue_id, user_id, date, start_time, end_time, number_of_people, status, reservation_type, remarks, cost)
VALUES 
(1, 1, '2025-05-04', '09:00', '10:00', 4, 'CONFIRMED', 'NORMAL', '篮球训练', 100.00),
(1, 2, '2025-05-04', '14:00', '15:00', 6, 'CONFIRMED', 'NORMAL', '篮球比赛', 100.00),
(2, 3, '2025-05-05', '10:00', '11:00', 4, 'CONFIRMED', 'NORMAL', '篮球训练', 100.00),
(3, 4, '2025-05-05', '15:00', '16:00', 2, 'CONFIRMED', 'NORMAL', '羽毛球训练', 50.00),
(4, 5, '2025-05-06', '16:00', '17:00', 2, 'CONFIRMED', 'NORMAL', '羽毛球训练', 50.00),
(5, 6, '2025-05-06', '17:00', '18:00', 2, 'CONFIRMED', 'NORMAL', '网球训练', 80.00),
(6, 7, '2025-05-07', '18:00', '19:00', 2, 'CONFIRMED', 'NORMAL', '网球训练', 80.00),
(7, 8, '2025-05-07', '19:00', '20:00', 10, 'CONFIRMED', 'NORMAL', '游泳训练', 30.00),
(8, 9, '2025-05-08', '20:00', '21:00', 2, 'CONFIRMED', 'NORMAL', '乒乓球训练', 20.00),
(9, 10, '2025-05-08', '21:00', '22:00', 2, 'CONFIRMED', 'NORMAL', '乒乓球训练', 20.00); 