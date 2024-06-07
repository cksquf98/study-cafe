-- menu에 대한 PL/SQL문(패키지, 프로시저, 함수 활용)
---- 메뉴 조회, 재고 관리 기능
CREATE OR REPLACE PACKAGE menu_inventory AS
    -- 패키지 프로시저 : 음료의 이름, 가격 조회하는 프로시저
    PROCEDURE get_menu_info(
        drink_idx IN NUMBER,
        drink_name OUT VARCHAR2,
        drink_price OUT NUMBER,
        drink_image OUT VARCHAR2
    );

     -- 패키지 함수 : 음료의 총 개수 반환하는 함수
    FUNCTION get_menu_count RETURN NUMBER;
    
    -- 패키지 프로시저 : 음료의 재고 정보를 조회하는 프로시저
    PROCEDURE get_menu_remaining(
        drink_idx IN NUMBER,
        drink_name OUT VARCHAR2,
        remaining OUT NUMBER,
        drink_image OUT VARCHAR2
    );
    
    -- 패키지 프로시저 : 음료 주문 후 재고에 음료를 추가하는 프로시저
    PROCEDURE add_drink(
        drink_idx IN NUMBER,
        quantity IN NUMBER,
        total_remaining OUT NUMBER
    );
END menu_inventory;
/

-- 위 패키지에 대해 자세히 정의
CREATE OR REPLACE PACKAGE BODY menu_inventory AS
    -- 패키지 프로시저 : 음료의 이름, 가격 조회하는 프로시저(Customer)
    PROCEDURE get_menu_info(
        drink_idx IN NUMBER,
        drink_name OUT VARCHAR2,
        drink_price OUT NUMBER,
        drink_image OUT VARCHAR2
    ) IS
    BEGIN 
        SELECT drink_name, drink_price, drink_image
        INTO drink_name, drink_price, drink_image
        FROM menu
        WHERE drink_idx = get_menu_info.drink_idx;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            drink_name := 'Not Found';
            drink_price := 0;
            drink_image := 'Not Found';
    END get_menu_info;
    
    -- 패키지 함수 : 음료의 총 개수 반환하는 함수
    FUNCTION get_menu_count RETURN NUMBER IS
        v_count NUMBER;
    BEGIN 
        SELECT COUNT(drink_idx) INTO v_count FROM menu;
        RETURN v_count;
    END get_menu_count;
    
    -- 패키지 프로시저 : 음료의 재고 정보를 조회하는 프로시저(Admin)
    PROCEDURE get_menu_remaining (
        drink_idx IN NUMBER,
        drink_name OUT VARCHAR2,
        remaining OUT NUMBER,
        drink_image OUT VARCHAR2
    )IS
    BEGIN
        SELECT drink_name, remaining, drink_image
        INTO drink_name, remaining, drink_image
        FROM menu
        WHERE drink_idx = get_menu_remaining.drink_idx;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            drink_name := 'Not Found';
            remaining := 0;
            drink_image := 'Not Found';
    END get_menu_remaining;
    
    -- 패키지 프로시저 : 음료 주문 후 재고에 음료를 추가하는 프로시저
   PROCEDURE add_drink(
        drink_idx IN NUMBER, 
        quantity IN NUMBER,
        total_remaining OUT NUMBER
    ) IS
        current_remaining NUMBER;
    BEGIN
        -- drink_idx에 해당하는 음료의 remaining을 조회
        SELECT remaining INTO current_remaining
        FROM menu
        WHERE drink_idx = add_drink.drink_idx;
        -- remaining 업데이트
        total_remaining := current_remaining + quantity;
        UPDATE menu
        SET remaining = total_remaining
        WHERE drink_idx = add_drink.drink_idx;
        
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.PUT_LINE('Drink Not Found');
            total_remaining := -1;
    END add_drink;
END menu_inventory;
/

-- 트리거 추가 (재고가 0이 되었을 때 알림을 주는 트리거)
CREATE OR REPLACE TRIGGER zero_remaining_trigger
AFTER UPDATE OF remaining ON menu
FOR EACH ROW
WHEN (new.remaining = 0)
BEGIN
    -- 트리거 동작 : 재고가 0이 되었을 때 알림
    DBMS_OUTPUT.PUT_LINE(:new.drink_name || '의 재고가 0이 되었습니다!');
END;
/
    
