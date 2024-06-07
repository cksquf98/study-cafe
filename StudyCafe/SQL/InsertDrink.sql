DROP SEQUENCE seq_drink_idx;
CREATE SEQUENCE seq_drink_idx
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;
    
-- 커피, 기타 음료 추가
DECLARE
    etc_names SYS.ODCIVARCHAR2LIST := SYS.ODCIVARCHAR2LIST('아메리카노', '카페라떼', '생수', '오렌지쥬스');
    etc_prices SYS.ODCINUMBERLIST := SYS.ODCINUMBERLIST(2500, 3000, 1000, 3500);
    etc_images SYS.ODCIVARCHAR2LIST := SYS.ODCIVARCHAR2LIST('Images/Americano.JPG', 'Images/Latte.JPG' ,'Images/Water.PNG', 'Images/Orange.JPG');
BEGIN
    FOR i IN 1..etc_names.COUNT LOOP
        INSERT INTO menu (drink_idx, drink_name, drink_price, remaining, drink_image)
        VALUES (seq_drink_idx.NEXTVAL, etc_names(i), etc_prices(i), ROUND(DBMS_RANDOM.VALUE(0, 50)), etc_images(i));
    END LOOP;
END;
/


-- 에이드 추가
DECLARE
    ade_names SYS.ODCIVARCHAR2LIST := SYS.ODCIVARCHAR2LIST('레몬에이드', '자몽에이드', '딸기에이드', '청포도에이드');
    ade_images SYS.ODCIVARCHAR2LIST := SYS.ODCIVARCHAR2LIST('Images/Lemonade.JPG' ,'Images/Grapefruit.JPG', 'Images/StrawJuice.JPG' ,'Images/GreenGrape.JPG');
BEGIN
    FOR i IN 1..ade_names.COUNT LOOP
        INSERT INTO menu (drink_idx, drink_name, drink_price, remaining, drink_image)
        VALUES (seq_drink_idx.NEXTVAL, ade_names(i), 4000, ROUND(DBMS_RANDOM.VALUE(10, 50)), ade_images(i));
    END LOOP;
END;
/

-- 스무디 추가
DECLARE
    smoothie_names SYS.ODCIVARCHAR2LIST := SYS.ODCIVARCHAR2LIST('블루베리스무디', '망고스무디', '딸기스무디', '키위스무디');
    smoothie_images SYS.ODCIVARCHAR2LIST := SYS.ODCIVARCHAR2LIST('Images/Blueberry.PNG' ,'Images/Mango.JPG', 'Images/StrawSmoothie.JPG' ,'Images/Kiwi.JPG');
BEGIN
    FOR i IN 1..smoothie_names.COUNT LOOP
        INSERT INTO menu (drink_idx, drink_name, drink_price, remaining, drink_image)
        VALUES (seq_drink_idx.NEXTVAL, smoothie_names(i), 4500, ROUND(DBMS_RANDOM.VALUE(10, 50)), smoothie_images(i));
    END LOOP;
END;
/



