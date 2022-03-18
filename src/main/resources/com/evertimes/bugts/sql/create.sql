CREATE DATABASE BugTracker
GO
USE BugTracker
GO
CREATE TABLE Приоритеты
(
    IDПриоритета                INT PRIMARY KEY,
    ИмяПриоритета               NVARCHAR(20),
    ОписаниеПриоритета          NVARCHAR(100),
    ЧисленноеЗначениеПриоритета INT
)
CREATE TABLE Статусы
(
    IDСтатуса       INT PRIMARY KEY,
    ИмяСтатуса      NVARCHAR(20),
    ОписаниеСтатуса NVARCHAR(100)
)
CREATE TABLE Метки
(
    IDМетки       INT PRIMARY KEY,
    ИмяМетки      NVARCHAR(20),
    ОписаниеМетки NVARCHAR(100)
)
CREATE TABLE Пользователи
(
    IDПользователя INT PRIMARY KEY,
    ФИО            NVARCHAR(50),
    Телефон        NVARCHAR(20),
    ДомашнийАдрес  NVARCHAR(100)
)
CREATE TABLE Разработчики
(
    IDПользователя        INT PRIMARY KEY,
    ЧислоРешенныхДефектов int
        CONSTRAINT FK_Разработчики_К_Пользователям
            FOREIGN KEY (IDПользователя) REFERENCES Пользователи (IDПользователя)
)
CREATE TABLE Тестировщики
(
    IDПользователя         INT PRIMARY KEY,
    ЧислоНайденныхДефектов int
        CONSTRAINT FK_Тестировщики_К_Пользователям
            FOREIGN KEY (IDПользователя) REFERENCES Пользователи (IDПользователя)
)
CREATE TABLE Администраторы
(
    IDПользователя INT PRIMARY KEY,
    CONSTRAINT FK_Администраторы_К_Пользователям
        FOREIGN KEY (IDПользователя) REFERENCES Пользователи (IDПользователя)
)
CREATE TABLE Проекты
(
    IDПроекта       INT PRIMARY KEY,
    ИмяПроекта      nvarchar(20),
    ОписаниеПроекта nvarchar(200),
    ЭтапРабот       nvarchar(30),
    ДатаНачала      date
)
CREATE TABLE ПользователиВПроектах
(
    IDПользователя INT,
    IDПроекта      INT,
    ДатаНазначения DATE,
    PRIMARY KEY (IDПользователя, IDПроекта),
    CONSTRAINT FK_ПользователиВПроектах_К_Пользователи
        FOREIGN KEY (IDПользователя) REFERENCES Пользователи (IDПользователя),
    CONSTRAINT FK_ПользователиВПроектах_К_Проекты
        FOREIGN KEY (IDПроекта) REFERENCES Проекты (IDПроекта)
)
CREATE TABLE Дефекты
(
    IDДефекта             INT PRIMARY KEY,
    IDПроекта             INT,
    IDТестировщика        INT,
    IDСтатуса             INT,
    IDПриоритета          INT,
    ДатаИВремяРегистрации DATETIME,
    CONSTRAINT FK_Дефекты_К_Проекты
        FOREIGN KEY (IDПроекта) REFERENCES Проекты (IDПроекта),
    CONSTRAINT FK_Дефекты_К_Тестировщики
        FOREIGN KEY (IDТестировщика) REFERENCES Тестировщики (IDПользователя),
    CONSTRAINT FK_Дефекты_К_Статусы
        FOREIGN KEY (IDСтатуса) REFERENCES Статусы (IDСтатуса),
    CONSTRAINT FK_Дефекты_К_Приоритеты
        FOREIGN KEY (IDПриоритета) REFERENCES Приоритеты (IDПриоритета)
)
CREATE TABLE РазработчикиРешающиеДефекты
(
    IDДефекта            INT PRIMARY KEY,
    IDПользователя       INT,
    ДатаИВремяНазначения DATETIME
        CONSTRAINT FK_РазработчикиРешающиеДефекты_К_Разработчики
            FOREIGN KEY (IDПользователя) REFERENCES Разработчики (IDПользователя),
    CONSTRAINT FK_РазработчикиРешающиеДефекты_К_Дефекты
        FOREIGN KEY (IDДефекта) REFERENCES Дефекты (IDДефекта)
)
CREATE TABLE ДефектыСМетками
(
    IDДефекта INT,
    IDМетки   INT,
    PRIMARY KEY (IDДефекта, IDМетки),
    CONSTRAINT FK_ДефектыСМетками_К_Дефекты
        FOREIGN KEY (IDДефекта) REFERENCES Дефекты (IDДефекта),
    CONSTRAINT FK_ДефектыСМетками_К_Меткам
        FOREIGN KEY (IDМетки) REFERENCES Метки (IDМетки),
)
CREATE TABLE Комментарии
(
    IDКомментария    INT PRIMARY KEY,
    Комментарий      NVARCHAR(200),
    IDДефекта        INT,
    ВремяКомментария DATETIME
        CONSTRAINT FK_Комментарии_К_Дефектам
            FOREIGN KEY (IDДефекта) REFERENCES Дефекты (IDДефекта),
)
CREATE RULE СтадииПроекта AS @x = 'Начало работ'
    OR @x = 'Проектирование'
    OR @x = 'Разработка'
    OR @x = 'Завершение работ'
    OR @x = 'Работа завершена'
    OR @x = 'Сопровождение'
GO
exec sp_bindrule 'СтадииПроекта', 'Проекты.ЭтапРабот'
GO
CREATE RULE NOT_NEGATIVE AS @x >= 0
GO
EXEC sp_bindrule 'NOT_NEGATIVE', 'Разработчики.ЧислоРешенныхДефектов'
GO
EXEC sp_bindrule 'NOT_NEGATIVE', 'Тестировщики.ЧислоНайденныхДефектов'
-- Триггер для вставки в таблицу тестировщиков
CREATE TRIGGER CHECK_USER_ONE_ROLE_TEST
    on Тестировщики
    INSTEAD OF INSERT AS
    Declare
        cur CURSOR LOCAL FORWARD_ONLY FAST_FORWARD FOR (Select IDПользователя
                                                        from inserted);
    Declare
        @IDПользователя varchar(50)
    OPEN cur;
    FETCH NEXT FROM cur INTO @IDПользователя
    WHILE (@@FETCH_STATUS = 0)
        BEGIN
            if (@IDПользователя in (Select IDПользователя from Администраторы))
                print ('Пользователь с ID ' + CONVERT(varchar(6), @IDПользователя) + ' уже является администратором')
            else
                if (@IDПользователя in (Select IDПользователя from Разработчики))
                    print ('Пользователь с ID ' + CONVERT(varchar(6), @IDПользователя) + ' уже является разработчиком')
                else
                    INSERT INTO Тестировщики(IDПользователя) VALUES (@IDПользователя)
            FETCH NEXT FROM cur INTO @IDПользователя
        END
    CLOSE cur;
    DEALLOCATE cur;
GO

-- Триггер для вставки в таблицу администраторов
CREATE TRIGGER CHECK_USER_ONE_ROLE_ADM
    on Администраторы
    INSTEAD OF INSERT AS
    Declare
        cur CURSOR FORWARD_ONLY LOCAL FAST_FORWARD FOR (Select IDПользователя
                                                        from inserted);
    Declare
        @IDПользователя varchar(50)
    OPEN cur;
    FETCH NEXT FROM cur INTO @IDПользователя
    WHILE (@@FETCH_STATUS = 0)
        BEGIN
            if (@IDПользователя in (Select IDПользователя from Разработчики))
                print ('Пользователь с ID ' + CONVERT(varchar(6), @IDПользователя) + ' уже является разработчиком')
            else
                if (@IDПользователя in (Select IDПользователя from Тестировщики))
                    print ('Пользователь с ID ' + CONVERT(varchar(6), @IDПользователя) + ' уже является Тестировщиком')
                else
                    INSERT INTO Администраторы(IDПользователя) VALUES (@IDПользователя)
            FETCH NEXT FROM cur INTO @IDПользователя
        END
    CLOSE cur;
    DEALLOCATE cur;
GO

-- Триггер для вставки в таблицу разработчиков
CREATE TRIGGER CHECK_USER_ONE_ROLE_DEV
    on Разработчики
    INSTEAD OF INSERT AS
    Declare
        cur CURSOR FORWARD_ONLY LOCAL FAST_FORWARD FOR (Select IDПользователя
                                                        from inserted);
    Declare
        @IDПользователя varchar(50)
    OPEN cur;
    FETCH NEXT FROM cur INTO @IDПользователя
    WHILE (@@FETCH_STATUS = 0)
        BEGIN
            if (@IDПользователя in (Select IDПользователя from Администраторы))
                print ('Пользователь с ID ' + CONVERT(varchar(6), @IDПользователя) + ' уже является Администратором')
            else
                if (@IDПользователя in (Select IDПользователя from Тестировщики))
                    print ('Пользователь с ID ' + CONVERT(varchar(6), @IDПользователя) + ' уже является Тестировщиком')
                else
                    INSERT INTO Разработчики(IDПользователя) VALUES (@IDПользователя)
            FETCH NEXT FROM cur INTO @IDПользователя
        END
    CLOSE cur;
    DEALLOCATE cur;
GO
CREATE TRIGGER UPDATE_FINDED_COUNT
    on Дефекты
    AFTER INSERT AS
    DECLARE
        @IDТестировщика int
    DECLARE
        cur CURSOR FORWARD_ONLY LOCAL FAST_FORWARD FOR SELECT IDТестировщика
                                                       from inserted
    OPEN cur
    WHILE (@@FETCH_STATUS = 0)
        BEGIN
            UPDATE Тестировщики
            SET ЧислоНайденныхДефектов = ЧислоНайденныхДефектов + 1
            WHERE Тестировщики.IDПользователя = (@IDТестировщика)
            FETCH NEXT FROM cur INTO @IDТестировщика
        END
    CLOSE cur
    DEALLOCATE cur
GO
CREATE TRIGGER UPDATE_RESOLVED_COUNT
    on Дефекты
    AFTER UPDATE AS
    if ((Select COUNT(*)
         from inserted
         WHERE IDСтатуса = 9) > 0)
        BEGIN
            DECLARE @IDРазработчика int
            DECLARE cur CURSOR LOCAL FORWARD_ONLY FAST_FORWARD FOR
                (Select IDПользователя
                 from (SELECT inserted.IDДефекта
                       from inserted
                                join deleted on inserted.IDДефекта = deleted.IDДефекта
                       WHERE inserted.IDСтатуса = 9
                         AND deleted.IDСтатуса != 9)
                          as t1
                          join РазработчикиРешающиеДефекты
                               on t1.IDДефекта = РазработчикиРешающиеДефекты.IDДефекта)
            OPEN cur
            FETCH NEXT FROM cur INTO @IDРазработчика
            WHILE (@@FETCH_STATUS = 0)
                BEGIN
                    UPDATE Разработчики
                    SET ЧислоРешенныхДефектов = ЧислоРешенныхДефектов + 1
                    WHERE Разработчики.IDПользователя = @IDРазработчика
                    FETCH NEXT FROM cur INTO @IDРазработчика
                END
            CLOSE cur
            DEALLOCATE cur
        END
GO
CREATE TRIGGER ПринадлежитЛиТестировщикКПроекту
    ON Дефекты
    INSTEAD OF INSERT AS
    DECLARE
        cur CURSOR LOCAL FORWARD_ONLY FAST_FORWARD FOR
            SELECT IDДефекта, IDПроекта, IDТестировщика, IDСтатуса, IDПриоритета, ДатаИВремяРегистрации
            from inserted
    Declare
        @IDДефекта int
    Declare
        @IDПроекта int
    Declare
        @IDТестировщика int
    Declare
        @IDСтатуса int
    Declare
        @IDПриоритета int
    Declare
        @ДатаИВремя datetime
    OPEN cur
    FETCH NEXT FROM cur INTO @IDДефекта,@IDПроекта,@IDТестировщика,@IDСтатуса, @IDПриоритета, @ДатаИВремя
    WHILE(@@FETCH_STATUS = 0)
        BEGIN
            IF @IDТестировщика in (Select IDПользователя from ПользователиВПроектах where IDПроекта = @IDПроекта)
                INSERT INTO Дефекты
                VALUES (@IDДефекта, @IDПроекта, @IDТестировщика, @IDСтатуса, @IDПриоритета, @ДатаИВремя)
            ELSE
                print ('Тестировщик c ID ' + CONVERT(varchar(5), @IDТестировщика) + ' не принадлежит проекту с ID ' +
                       CONVERT(varchar(5), @IDПроекта))
            FETCH NEXT FROM cur INTO @IDДефекта,@IDПроекта,@IDТестировщика,@IDСтатуса, @IDПриоритета, @ДатаИВремя
        END
    CLOSE cur
    DEALLOCATE cur
GO
CREATE TRIGGER ПринадлежитЛиРазработчикКПроекту
    ON РазработчикиРешающиеДефекты
    INSTEAD OF INSERT AS
    DECLARE
        cur CURSOR LOCAL FORWARD_ONLY FAST_FORWARD FOR SELECT IDПользователя, IDДефекта, ДатаИВремяНазчнения
                                                       from inserted
    OPEN cur
    DECLARE
        @IDРазработчика int
    DECLARE
        @IDДефекта int
    DECLARE
        @ДатаИВремяНазначения datetime
    FETCH NEXT FROM cur INTO @IDРазработчика,@IDДефекта,@ДатаИВремяНазначения
    WHILE(@@FETCH_STATUS = 0)
        BEGIN
            DECLARE @IDПроекта int = (Select IDПроекта from Дефекты where IDДефекта = @IDДефекта)
            IF @IDРазработчика in (Select IDПользователя from ПользователиВПроектах where IDПроекта = @IDПроекта)
                INSERT INTO РазработчикиРешающиеДефекты VALUES (@IDДефекта, @IDРазработчика, @ДатаИВремяНазначения)
            ELSE
                print ('Разработчик с ID ' + CONVERT(varchar(5), @IDРазработчика)
                    + ' не принадлежит проекту с ID ' + CONVERT(varchar(5), @IDПроекта))
            FETCH NEXT FROM cur INTO @IDРазработчика,@IDДефекта,@ДатаИВремяНазначения
        END
    CLOSE cur
    DEALLOCATE cur
GO
CREATE TRIGGER CHANGE_STATUS_TO_ASSIGNED
    ON РазработчикиРешающиеДефекты
    AFTER INSERT AS
    UPDATE Дефекты
    SET IDСтатуса = 2
    WHERE IDДефекта in (SELECT IDДефекта from inserted)
GO
USE BugTracker
INSERT INTO Пользователи
VALUES (1, 'Соловьёв Август Пантелеймонович', '+7(682) 675-3107', 'Merebeck, Kinniside,CA23 3ES');
INSERT INTO Пользователи
VALUES (2, 'Большаков Касьян Мэлсович', '+7(816) 616-4910', '31 Hall Park View, Workington,CA14 4AG');
INSERT INTO Пользователи
VALUES (3, 'Чернов Степан Эдуардович', '+7(401) 691-3795', '10 Lowry Hill Road, Carlisle,CA3 0DF');
INSERT INTO Пользователи
VALUES (4, 'Беляев Алан Вячеславович', '+7(897) 453-0466', 'Fell View, Warcop,CA16 6PR');
INSERT INTO Пользователи
VALUES (5, 'Орехов Бенедикт Богуславович', '+7(513) 646-5739', 'Owlsadean, Tebay,CA10 3SR');
INSERT INTO Пользователи
VALUES (6, 'Большаков Натан Васильевич', '+7(824) 526-3518', '122 Victoria Road, Whitehaven,CA28 6JJ');
INSERT INTO Пользователи
VALUES (7, 'Нестеров Владислав Глебович', '+7(241) 922-9296', 'Fell View, Warcop,CA16 6PR');
INSERT INTO Пользователи
VALUES (8, 'Осипов Анатолий Геннадьевич', '+7(446) 756-0751', 'Pennybrig, Grange,CA12 5UQ');
INSERT INTO Пользователи
VALUES (9, 'Анисимов Леонид Антонинович', '+7(246) 820-0459', '22 Blencathra Street, Keswick,CA12 4HP');
INSERT INTO Пользователи
VALUES (10, 'Соловьёв Август Пантелеймонович', '+7(605) 328-4684', '10 Lowry Hill Road, Carlisle,CA3 0DF');
INSERT INTO Пользователи
VALUES (11, 'Орехов Моисей Вячеславович', '+7(550) 300-2204', 'Grange House, Townhead, Alston,CA9 3SL');
INSERT INTO Пользователи
VALUES (12, 'Пахомов Олег Германнович', '+7(998) 420-9912', '13 Kentmere Grove, Carlisle,CA2 6JD');
INSERT INTO Пользователи
VALUES (13, 'Блинов Кондратий Митрофанович', '+7(925) 351-2752', '3 Duke Street, Whitehaven,CA28 7EW');
INSERT INTO Пользователи
VALUES (14, 'Данилов Георгий Альбертович', '+7(382) 445-8121', '18 Ashness Close, Whitehaven,CA28 9RR');
INSERT INTO Пользователи
VALUES (15, 'Родионов Август Вадимович', '+7(661) 624-8612', 'Grange House, Townhead, Alston,CA9 3SL');
INSERT INTO Пользователи
VALUES (16, 'Игнатьев Митрофан Валерьевич', '+7(542) 695-5340', 'Fell View, Warcop,CA16 6PR');
INSERT INTO Пользователи
VALUES (17, 'Чернов Степан Эдуардович', '+7(605) 328-4684', 'Owlsadean, Tebay,CA10 3SR');
INSERT INTO Пользователи
VALUES (18, 'Исаков Мечислав Игнатьевич', '+7(401) 691-3795',
        'Unit 1, The Glass House, Auction Mart Lane, Penrith,CA11 7JG');
INSERT INTO Пользователи
VALUES (19, 'Терентьев Аркадий Михаилович', '+7(609) 696-6403', '47 Newtown Road, Carlisle,CA2 7JA');
INSERT INTO Пользователи
VALUES (20, 'Родионов Август Вадимович', '+7(219) 222-3187', 'The Old Byre, Kirkoswald,CA10 1ER');
INSERT INTO Пользователи
VALUES (21, 'Беляев Алан Вячеславович', '+7(290) 318-1861', '57 Sarsfield Road, Workington,CA14 5BZ');
INSERT INTO Пользователи
VALUES (22, 'Буров Клим Юлианович', '+7(241) 922-9296', '6 Hothfield Court, Appleby-In-Westmorland,CA16 6JD');
INSERT INTO Пользователи
VALUES (23, 'Воронцов Иннокентий Федорович', '+7(583) 209-2250', '4 Castle Mount, Armathwaite,CA4 9PE');
INSERT INTO Пользователи
VALUES (24, 'Лаврентьев Герасим Алексеевич', '+7(609) 696-6403', '46A - 48A Queen Street, Aspatria,CA7 3BB');
INSERT INTO Пользователи
VALUES (25, 'Сафонов Тимофей Ярославович', '+7(669) 659-0050', '2 Chaucer Road, Workington,CA14 4HH');
INSERT INTO Пользователи
VALUES (26, 'Дмитриев Эрнест Куприянович', '+7(605) 328-4684', 'Site 6C North Lakes Business Park, Flusco,CA11 0JG');
INSERT INTO Пользователи
VALUES (27, 'Егоров Гордей Георгьевич', '+7(456) 959-7844', '18 Ashness Close, Whitehaven,CA28 9RR');
INSERT INTO Пользователи
VALUES (28, 'Дмитриев Эрнест Куприянович', '+7(248) 968-5701', 'Owlsadean, Tebay,CA10 3SR');
INSERT INTO Пользователи
VALUES (29, 'Нестеров Владислав Глебович', '+7(682) 675-3107', '1 Apple Tree Close, Durdar,CA2 4TQ');
INSERT INTO Пользователи
VALUES (30, 'Терентьев Аркадий Михаилович', '+7(998) 294-2793', '2 Clifford Road, Penrith,CA11 8PW');
INSERT INTO Пользователи
VALUES (31, 'Чернов Степан Эдуардович', '+7(824) 526-3518', '1 Apple Tree Close, Durdar,CA2 4TQ');
INSERT INTO Пользователи
VALUES (32, 'Исаков Мечислав Игнатьевич', '+7(607) 716-2972', '1 Clint View, Croglin,CA4 9RU');
INSERT INTO Пользователи
VALUES (33, 'Кузнецов Кассиан Филатович', '+7(790) 477-2419', '1 Apple Tree Close, Durdar,CA2 4TQ');
INSERT INTO Пользователи
VALUES (34, 'Осипов Анатолий Геннадьевич', '+7(716) 275-9579', '31 Hall Park View, Workington,CA14 4AG');
INSERT INTO Пользователи
VALUES (35, 'Наумов Ираклий Аркадьевич', '+7(682) 675-3107', '13 Kentmere Grove, Carlisle,CA2 6JD');
INSERT INTO Пользователи
VALUES (36, 'Соловьёв Август Пантелеймонович', '+7(816) 616-4910', '9 Staunton Drive, Carlisle,CA1 3GU');
INSERT INTO Пользователи
VALUES (37, 'Лобанов Альфред Авдеевич', '+7(669) 659-0050', 'Owlsadean, Tebay,CA10 3SR');
INSERT INTO Пользователи
VALUES (38, 'Комиссаров Арнольд Глебович', '+7(295) 376-0235', 'Grange House, Townhead, Alston,CA9 3SL');
INSERT INTO Пользователи
VALUES (39, 'Галкин Исаак Иванович', '+7(998) 294-2793', '20 Hawkshead Avenue, Workington,CA14 3HP');
INSERT INTO Пользователи
VALUES (40, 'Фадеев Вениамин Робертович', '+7(577) 413-4386', '4 Castle Mount, Armathwaite,CA4 9PE');
INSERT INTO Пользователи
VALUES (41, 'Анисимов Олег Дамирович', '+7(816) 616-4910', '8 King George Gardens, Warwick Bridge,CA4 8FN');
INSERT INTO Пользователи
VALUES (42, 'Егоров Гордей Георгьевич', '+7(984) 331-9414', 'Fell View, Warcop,CA16 6PR');
INSERT INTO Пользователи
VALUES (43, 'Ширяев Вальтер Евгеньевич', '+7(682) 675-3107', '22 Blencathra Street, Keswick,CA12 4HP');
INSERT INTO Пользователи
VALUES (44, 'Лаврентьев Станислав Созонович', '+7(605) 328-4684', '1 Clint View, Croglin,CA4 9RU');
INSERT INTO Пользователи
VALUES (45, 'Некрасов Назарий Мэлорович', '+7(246) 820-0459', 'Chapel Burn Farm, Low Row,CA8 2LY');
INSERT INTO Пользователи
VALUES (46, 'Комиссаров Арнольд Глебович', '+7(619) 706-5766', '20 Sycamore Drive, Longtown,CA6 5NZ');
INSERT INTO Пользователи
VALUES (47, 'Фадеев Вениамин Робертович', '+7(847) 493-1972', '57 Sarsfield Road, Workington,CA14 5BZ');
INSERT INTO Пользователи
VALUES (48, 'Орехов Бенедикт Богуславович', '+7(401) 691-3795', '31 Hall Park View, Workington,CA14 4AG');
INSERT INTO Пользователи
VALUES (49, 'Данилов Георгий Альбертович', '+7(550) 300-2204', '18 Ashness Close, Whitehaven,CA28 9RR');
INSERT INTO Пользователи
VALUES (50, 'Фадеев Сергей Георгьевич', '+7(580) 361-6908', '1 Clint View, Croglin,CA4 9RU');
INSERT INTO Пользователи
VALUES (51, 'Фёдоров Станислав Владиславович', '+7(383) 408-0344', '20 Hawkshead Avenue, Workington,CA14 3HP');
INSERT INTO Пользователи
VALUES (52, 'Наумов Ираклий Аркадьевич', '+7(925) 351-2752', '2 Chaucer Road, Workington,CA14 4HH');
INSERT INTO Пользователи
VALUES (53, 'Григорьев Фрол Максимович', '+7(219) 222-3187', 'Haddon Cottage, 48 The Green, Dalston,CA5 7QD');
INSERT INTO Пользователи
VALUES (54, 'Наумов Ираклий Аркадьевич', '+7(248) 968-5701', '4 Castle Mount, Armathwaite,CA4 9PE');
INSERT INTO Пользователи
VALUES (55, 'Зыков Донат Лукьевич', '+7(557) 918-6680', '14 Buchanan Road, Carlisle,CA2 4QD');
INSERT INTO Пользователи
VALUES (56, 'Сафонов Тимофей Ярославович', '+7(382) 445-8121', 'Cooper House Stables, North Stainmore,CA17 4DZ');
INSERT INTO Пользователи
VALUES (57, 'Воронцов Иннокентий Федорович', '+7(241) 922-9296', 'Merryhill House, Egremont,CA22 2US');
INSERT INTO Пользователи
VALUES (58, 'Нестеров Владислав Глебович', '+7(610) 995-3741', '6 Hothfield Court, Appleby-In-Westmorland,CA16 6JD');
INSERT INTO Пользователи
VALUES (59, 'Игнатьев Митрофан Валерьевич', '+7(550) 300-2204', '2 Station Road Terrace, Threlkeld,CA12 4AL');
INSERT INTO Пользователи
VALUES (60, 'Терентьев Аркадий Михаилович', '+7(790) 477-2419', '2 Beech Lane, Cockermouth,CA13 9HG');

INSERT INTO Разработчики (IDПользователя)
VALUES (1)
INSERT INTO Разработчики (IDПользователя)
VALUES (2)
INSERT INTO Разработчики (IDПользователя)
VALUES (3)
INSERT INTO Разработчики (IDПользователя)
VALUES (4)
INSERT INTO Разработчики (IDПользователя)
VALUES (5)
INSERT INTO Разработчики (IDПользователя)
VALUES (6)
INSERT INTO Разработчики (IDПользователя)
VALUES (7)
INSERT INTO Разработчики (IDПользователя)
VALUES (8)
INSERT INTO Разработчики (IDПользователя)
VALUES (9)
INSERT INTO Разработчики (IDПользователя)
VALUES (10)
INSERT INTO Разработчики (IDПользователя)
VALUES (11)
INSERT INTO Разработчики (IDПользователя)
VALUES (12)
INSERT INTO Разработчики (IDПользователя)
VALUES (13)
INSERT INTO Разработчики (IDПользователя)
VALUES (14)
INSERT INTO Разработчики (IDПользователя)
VALUES (15)
INSERT INTO Разработчики (IDПользователя)
VALUES (16)
INSERT INTO Разработчики (IDПользователя)
VALUES (17)
INSERT INTO Разработчики (IDПользователя)
VALUES (18)
INSERT INTO Разработчики (IDПользователя)
VALUES (19)
INSERT INTO Разработчики (IDПользователя)
VALUES (20)

INSERT INTO Тестировщики (IDПользователя)
VALUES (21)
INSERT INTO Тестировщики (IDПользователя)
VALUES (22)
INSERT INTO Тестировщики (IDПользователя)
VALUES (23)
INSERT INTO Тестировщики (IDПользователя)
VALUES (24)
INSERT INTO Тестировщики (IDПользователя)
VALUES (25)
INSERT INTO Тестировщики (IDПользователя)
VALUES (26)
INSERT INTO Тестировщики (IDПользователя)
VALUES (27)
INSERT INTO Тестировщики (IDПользователя)
VALUES (28)
INSERT INTO Тестировщики (IDПользователя)
VALUES (29)
INSERT INTO Тестировщики (IDПользователя)
VALUES (30)
INSERT INTO Тестировщики (IDПользователя)
VALUES (31)
INSERT INTO Тестировщики (IDПользователя)
VALUES (32)
INSERT INTO Тестировщики (IDПользователя)
VALUES (33)
INSERT INTO Тестировщики (IDПользователя)
VALUES (34)
INSERT INTO Тестировщики (IDПользователя)
VALUES (35)
INSERT INTO Тестировщики (IDПользователя)
VALUES (36)
INSERT INTO Тестировщики (IDПользователя)
VALUES (37)
INSERT INTO Тестировщики (IDПользователя)
VALUES (38)
INSERT INTO Тестировщики (IDПользователя)
VALUES (39)
INSERT INTO Тестировщики (IDПользователя)
VALUES (40)

INSERT INTO Администраторы (IDПользователя)
VALUES (41)
INSERT INTO Администраторы (IDПользователя)
VALUES (42)
INSERT INTO Администраторы (IDПользователя)
VALUES (43)
INSERT INTO Администраторы (IDПользователя)
VALUES (44)
INSERT INTO Администраторы (IDПользователя)
VALUES (45)
INSERT INTO Администраторы (IDПользователя)
VALUES (46)
INSERT INTO Администраторы (IDПользователя)
VALUES (47)
INSERT INTO Администраторы (IDПользователя)
VALUES (48)
INSERT INTO Администраторы (IDПользователя)
VALUES (49)
INSERT INTO Администраторы (IDПользователя)
VALUES (50)
INSERT INTO Администраторы (IDПользователя)
VALUES (51)
INSERT INTO Администраторы (IDПользователя)
VALUES (52)
INSERT INTO Администраторы (IDПользователя)
VALUES (53)
INSERT INTO Администраторы (IDПользователя)
VALUES (54)
INSERT INTO Администраторы (IDПользователя)
VALUES (55)
INSERT INTO Администраторы (IDПользователя)
VALUES (56)
INSERT INTO Администраторы (IDПользователя)
VALUES (57)
INSERT INTO Администраторы (IDПользователя)
VALUES (58)
INSERT INTO Администраторы (IDПользователя)
VALUES (59)
INSERT INTO Администраторы (IDПользователя)
VALUES (60)

INSERT INTO Статусы
VALUES (1, 'Новый', 'Открыт новый дефект')
INSERT INTO Статусы
VALUES (2, 'Назначен', 'Дефект назначен разработчику')
INSERT INTO Статусы
VALUES (3, 'Исправлен', 'Дефект исправлен')
INSERT INTO Статусы
VALUES (4, 'Не исправлен', 'Дефект не исправлен')
INSERT INTO Статусы
VALUES (5, 'Дубль', 'Аналогичный дефект существует')
INSERT INTO Статусы
VALUES (6, 'Не воспроизводим', 'Разработчик не смог воспроизвести дефект')
INSERT INTO Статусы
VALUES (7, 'Закрыт', 'Дефект закрыт')
INSERT INTO Статусы
VALUES (8, 'Открыт повторно', 'Дефект был снова найден')
INSERT INTO Статусы
VALUES (9, 'Подтвержен', 'Корректность исправления подтверждена')

USE BugTracker
INSERT INTO Приоритеты
VALUES (1, 'Тривалный', 'Не очень важная проблема, не оказываюшая влияния', 1)
INSERT INTO Приоритеты
Values (2, 'Незначительный', 'Проблема не нарушающая бизнес логику', 2)
INSERT INTO Приоритеты
Values (3, 'Средний', 'Проблема незначительно нарушающая бизнес логику', 3)
INSERT INTO Приоритеты
Values (4, 'Значительный', 'Часть бизнес логики работает некорректно', 4)
INSERT INTO Приоритеты
Values (5, 'Критический', 'Неправильно работающая бизнес логика', 5)
INSERT INTO Приоритеты
Values (6, 'Блокирующий', 'Проблема приводящая приложение в нерабочее состояние', 6)
INSERT INTO Приоритеты
Values (7, 'Критический + 1', 'Проблема критически нарушающая бизнес логику', 6)
INSERT INTO Приоритеты
Values (8, 'Критический + 2', 'Проблема нарушающая бизнес логику и имеющая серьезные последствия', 7)

INSERT INTO Метки
Values (1, 'UI', 'Ошибка в пользовательском интерфейсе')
INSERT INTO Метки
Values (2, 'DB', 'Ошибка связанная с базой данных')
INSERT INTO Метки
Values (3, 'BL', 'Ошибка в бизнес логике')
INSERT INTO Метки
Values (4, 'REQ', 'Ошибка в обработке запроса')
INSERT INTO Метки
Values (5, 'FRONT', 'Ошибка в логике на стороне пользователя')
INSERT INTO Метки
Values (6, 'DAO', 'Ошибка слоя доступа к данным')
INSERT INTO Метки
Values (7, 'SERVCOM', 'Ошибка в коммуникации межлу сервисами')
INSERT INTO Метки
Values (8, 'API', 'Неверно задан API')
INSERT INTO Метки
Values (9, 'APIINVOKE', 'Ошибка при обращениии к API')
INSERT INTO Метки
Values (10, 'CORRECT', 'Не ошибка')

INSERT INTO Проекты
Values (1, 'MVIDEO-APP', 'Проект написания приложения для магазина МВИДЕО', 'Начало работ', SYSDATETIME())
INSERT INTO Проекты
Values (2, 'DNS-APP', 'Проект написания приложения для магазина DNS', 'Проектирование', SYSDATETIME())
INSERT INTO Проекты
Values (3, 'ELEX-APP', 'Проект написания приложения для магазина ELEX', 'Разработка', SYSDATETIME())
INSERT INTO Проекты
Values (4, 'RSREU-WEB', 'Проект написания сайта для Рязанского Госурадственного Радиотехнического Университета',
        'Сопровождение', SYSDATETIME())
INSERT INTO Проекты
Values (5, 'RSREU-WEB-EDU', 'Проект написания сайта для мониторинга журнала оценов для РГРТУ', 'Завершение работ',
        SYSDATETIME())
INSERT INTO Проекты
Values (6, 'MAGISTRAL-BACKEND', 'Проект написания бекенда для грузоперевозок Магистраль', 'Проектирование',
        SYSDATETIME())
INSERT INTO Проекты
Values (7, 'GAZCOMP-BACKED', 'Проект написания бекенда для магазина газокомпрессорной службы', 'Начало работ',
        SYSDATETIME())
INSERT INTO Проекты
Values (8, 'ADBLOCK-PLUGIN-EDGE', 'Проект написания плагина для браузера MS EDGE', 'Начало работ', SYSDATETIME())
INSERT INTO Проекты
Values (9, 'CITILINK-BACKED', 'Проект написания бекенда для магазина CITILINK', 'Начало работ', SYSDATETIME())
INSERT INTO Проекты
Values (10, 'APU-RF-WEB', 'Проект написания сайта для админстрации президента РФ', 'Начало работ', SYSDATETIME())

INSERT INTO ПользователиВПроектах
Values (22, 1, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (23, 2, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (24, 1, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (25, 1, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (26, 4, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (27, 5, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (22, 6, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (22, 7, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (22, 8, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (29, 9, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (30, 2, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (34, 3, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (35, 3, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (36, 4, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (23, 5, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (39, 6, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (23, 7, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (32, 2, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (28, 10, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (29, 10, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (21, 10, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (31, 10, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (32, 10, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (33, 10, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (34, 10, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (35, 10, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (37, 10, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (38, 10, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (40, 10, SYSDATETIME())

INSERT INTO ПользователиВПроектах
Values (1, 1, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (2, 4, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (3, 5, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (4, 6, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (5, 7, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (6, 8, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (7, 9, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (8, 10, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (9, 10, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (10, 2, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (11, 3, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (12, 3, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (13, 4, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (14, 5, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (15, 6, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (16, 7, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (17, 2, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (18, 9, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (19, 10, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (20, 10, SYSDATETIME())

INSERT INTO ПользователиВПроектах
Values (41, 1, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (42, 2, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (43, 3, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (44, 4, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (45, 4, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (46, 2, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (47, 4, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (48, 2, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (49, 6, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (50, 7, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (51, 8, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (52, 9, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (53, 9, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (54, 9, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (55, 9, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (56, 8, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (57, 2, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (58, 3, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (59, 4, SYSDATETIME())
INSERT INTO ПользователиВПроектах
Values (60, 5, SYSDATETIME())

INSERT INTO Дефекты
Values (1, 1, 22, 1, 1, SYSDATETIME())
INSERT INTO Дефекты
Values (2, 2, 23, 1, 1, SYSDATETIME())
INSERT INTO Дефекты
Values (3, 1, 24, 1, 1, SYSDATETIME())
INSERT INTO Дефекты
Values (4, 1, 25, 2, 2, SYSDATETIME())
INSERT INTO Дефекты
Values (5, 4, 26, 3, 2, SYSDATETIME())
INSERT INTO Дефекты
Values (6, 5, 27, 4, 2, SYSDATETIME())
INSERT INTO Дефекты
Values (7, 6, 22, 2, 2, SYSDATETIME())
INSERT INTO Дефекты
Values (8, 7, 22, 2, 2, SYSDATETIME())
INSERT INTO Дефекты
Values (9, 8, 22, 2, 3, SYSDATETIME())
INSERT INTO Дефекты
Values (10, 9, 29, 5, 3, SYSDATETIME())
INSERT INTO Дефекты
Values (11, 10, 28, 6, 3, SYSDATETIME())
INSERT INTO Дефекты
Values (12, 10, 29, 5, 3, SYSDATETIME())
INSERT INTO Дефекты
Values (13, 2, 30, 8, 3, SYSDATETIME())
INSERT INTO Дефекты
Values (14, 3, 34, 8, 3, SYSDATETIME())
INSERT INTO Дефекты
Values (15, 3, 35, 7, 4, SYSDATETIME())
INSERT INTO Дефекты
Values (16, 4, 36, 7, 4, SYSDATETIME())
INSERT INTO Дефекты
Values (17, 5, 23, 5, 5, SYSDATETIME())
INSERT INTO Дефекты
Values (18, 6, 39, 4, 6, SYSDATETIME())
INSERT INTO Дефекты
Values (19, 7, 23, 3, 6, SYSDATETIME())
INSERT INTO Дефекты
Values (20, 2, 32, 2, 7, SYSDATETIME())
INSERT INTO Дефекты
Values (21, 9, 29, 3, 8, SYSDATETIME())
INSERT INTO Дефекты
Values (22, 10, 28, 3, 8, SYSDATETIME())
INSERT INTO Дефекты
Values (23, 10, 29, 4, 8, SYSDATETIME())
INSERT INTO Дефекты
Values (24, 5, 23, 5, 5, SYSDATETIME())
INSERT INTO Дефекты
Values (25, 6, 39, 4, 6, SYSDATETIME())
INSERT INTO Дефекты
Values (26, 7, 23, 3, 6, SYSDATETIME())
INSERT INTO Дефекты
Values (27, 2, 32, 2, 7, SYSDATETIME())
INSERT INTO Дефекты
Values (28, 9, 29, 3, 8, SYSDATETIME())
INSERT INTO Дефекты
Values (29, 10, 28, 3, 8, SYSDATETIME())
INSERT INTO Дефекты
Values (30, 10, 29, 4, 8, SYSDATETIME())

INSERT INTO ДефектыСМетками
VALUES (1, 1)
INSERT INTO ДефектыСМетками
VALUES (2, 1)
INSERT INTO ДефектыСМетками
VALUES (3, 1)
INSERT INTO ДефектыСМетками
VALUES (4, 1)
INSERT INTO ДефектыСМетками
VALUES (5, 1)
INSERT INTO ДефектыСМетками
VALUES (6, 2)
INSERT INTO ДефектыСМетками
VALUES (8, 2)
INSERT INTO ДефектыСМетками
VALUES (9, 8)
INSERT INTO ДефектыСМетками
VALUES (10, 1)
INSERT INTO ДефектыСМетками
VALUES (11, 2)
INSERT INTO ДефектыСМетками
VALUES (11, 3)
INSERT INTO ДефектыСМетками
VALUES (13, 5)
INSERT INTO ДефектыСМетками
VALUES (14, 5)
INSERT INTO ДефектыСМетками
VALUES (15, 9)
INSERT INTO ДефектыСМетками
VALUES (16, 5)
INSERT INTO ДефектыСМетками
VALUES (17, 9)
INSERT INTO ДефектыСМетками
VALUES (18, 5)
INSERT INTO ДефектыСМетками
VALUES (19, 5)
INSERT INTO ДефектыСМетками
VALUES (20, 5)

INSERT INTO РазработчикиРешающиеДефекты
VALUES (4, 1, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (5, 2, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (6, 3, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (7, 4, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (8, 5, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (9, 6, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (10, 7, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (11, 8, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (12, 9, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (13, 10, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (14, 11, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (15, 12, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (16, 13, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (17, 14, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (18, 15, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (19, 16, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (20, 17, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (21, 18, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (22, 19, SYSDATETIME())
INSERT INTO РазработчикиРешающиеДефекты
VALUES (23, 20, SYSDATETIME())

INSERT INTO Комментарии
VALUES (1, 'Кнопка авторизации не в том месте', 1, SYSDATETIME())
INSERT INTO Комментарии
VALUES (2, 'Кнопка корзины не того цвета', 2, SYSDATETIME())
INSERT INTO Комментарии
VALUES (3, 'Кнопка корзины не того цвета', 3, SYSDATETIME())
INSERT INTO Комментарии
VALUES (4, 'Верстка съехала', 4, SYSDATETIME())
INSERT INTO Комментарии
VALUES (5, 'Вертска съехала', 5, SYSDATETIME())
INSERT INTO Комментарии
VALUES (6, 'Долго загружаются оценки', 6, SYSDATETIME())
INSERT INTO Комментарии
VALUES (7, 'Нет сортировки по городам', 7, SYSDATETIME())
INSERT INTO Комментарии
VALUES (8, 'Неправильный запрос к БД', 8, SYSDATETIME())
INSERT INTO Комментарии
VALUES (9, 'Неправильно работает блокировка рекламы от yandex', 9, SYSDATETIME())
INSERT INTO Комментарии
VALUES (10, 'Не работает добавление в корзину', 10, SYSDATETIME())
INSERT INTO Комментарии
VALUES (11, 'Не работает колонка приказов', 11, SYSDATETIME())
INSERT INTO Комментарии
VALUES (12, 'Не работает колонка новостей', 12, SYSDATETIME())
INSERT INTO Комментарии
VALUES (13, 'Не работает сравнение', 13, SYSDATETIME())
INSERT INTO Комментарии
VALUES (14, 'Не работает сравнение товаров', 14, SYSDATETIME())
INSERT INTO Комментарии
VALUES (15, 'Не работает оплата через карты МИР', 15, SYSDATETIME())
INSERT INTO Комментарии
VALUES (16, 'При нажатии на ссылку главной страницы открывается неправильный адрес', 16, SYSDATETIME())
INSERT INTO Комментарии
VALUES (17, 'Выводятся оценки не того ученика', 17, SYSDATETIME())
INSERT INTO Комментарии
VALUES (18, 'Бесконечная загрузка', 18, SYSDATETIME())
INSERT INTO Комментарии
VALUES (19, 'Бесконечная загрузка', 19, SYSDATETIME())
INSERT INTO Комментарии
VALUES (20, 'Неправильный расчет стоимости товаров', 20, SYSDATETIME())
INSERT INTO Комментарии
VALUES (21, 'Товары при добавлении больше 10 стоят 0 рублей', 21, SYSDATETIME())
INSERT INTO Комментарии
VALUES (22, 'На сайте находится недостоверная информация', 22, SYSDATETIME())
INSERT INTO Комментарии
VALUES (23, 'При попытке открыть страницу с приказами открывается сайт с вредоносным ПО', 23, SYSDATETIME())
INSERT INTO Комментарии
VALUES (24, 'При попытке добавить товар в корзину на него оформляется заказ', 24, SYSDATETIME())
INSERT INTO Комментарии
VALUES (25, 'Не работает каталог', 25, SYSDATETIME())
INSERT INTO Комментарии
VALUES (26, 'Не работают отзывы', 26, SYSDATETIME())
INSERT INTO Комментарии
VALUES (27, 'Загружаются только старые новости', 27, SYSDATETIME())
INSERT INTO Комментарии
VALUES (28, 'Неправильное цветовое оформление', 28, SYSDATETIME())
INSERT INTO Комментарии
VALUES (30, 'Старый логотип', 30, SYSDATETIME())