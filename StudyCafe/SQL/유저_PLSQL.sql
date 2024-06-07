--유저 관련 PL/SQL
----------------------------------------------------------
-- 유저 패키지
-- 유저 프로시저랑 함수를 패키지로 묶어버림
CREATE OR REPLACE PACKAGE UserManagementPackage AS
    PROCEDURE CreateUser(
        p_name IN VARCHAR2,
        p_phone IN VARCHAR2,
        p_pw IN VARCHAR2,
        p_result OUT NUMBER
    );

  -- 사용자 조회 프로시저
  PROCEDURE GetUser (
    p_phone IN VARCHAR2,
    p_password IN VARCHAR2,
    p_cursor OUT SYS_REFCURSOR
  );

  -- 사용자 확인 함수
  FUNCTION CheckUser (
    p_username IN VARCHAR2,
    p_password IN VARCHAR2
  ) RETURN NUMBER;

  -- 사용 중인 사용자 확인 함수
  FUNCTION CheckUsingUser (
    p_phone IN VARCHAR2
  ) RETURN NUMBER;

  -- 사용자의 신고 누적 횟수 확인 함수
  FUNCTION CheckReportCount (
    p_userIdx IN NUMBER
  ) RETURN NUMBER;
END UserManagementPackage;
/

-- 패키지 바디
CREATE OR REPLACE PACKAGE BODY UserManagementPackage AS

    PROCEDURE CreateUser(
        p_name IN VARCHAR2,
        p_phone IN VARCHAR2,
        p_pw IN VARCHAR2,
        p_result OUT NUMBER
    ) AS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO v_count
        FROM users
        WHERE user_phone = p_phone;

        IF v_count > 0 THEN
            p_result := -1; -- 폰 번호 중복
        ELSE
            INSERT INTO users (user_idx, user_name, user_phone, user_pw, report_count, admin)
            VALUES (SEQ_USER_IDX.NEXTVAL, p_name, p_phone, p_pw, 0, 'N');
            p_result := 1; -- 생성 완료
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            p_result := -2; -- 예외 발생
    END CreateUser;
  
  -- 사용자 조회 프로시저 구현
  PROCEDURE GetUser (
    p_phone IN VARCHAR2,
    p_password IN VARCHAR2,
    p_cursor OUT SYS_REFCURSOR
  ) AS
    v_user_idx USERS.USER_IDX%TYPE;
    v_user_name USERS.USER_NAME%TYPE;
    v_user_phone USERS.USER_PHONE%TYPE;
    v_report_count USERS.REPORT_COUNT%TYPE;
    v_admin USERS.ADMIN%TYPE;
  BEGIN
    -- 사용자 조회 로직 구현
    SELECT USER_IDX, USER_NAME, USER_PHONE, REPORT_COUNT, ADMIN
    INTO v_user_idx, v_user_name, v_user_phone, v_report_count, v_admin
    FROM USERS
    WHERE USER_PHONE = p_phone
    AND USER_PW = p_password;
    
    -- 커서 열기
    OPEN p_cursor FOR
    SELECT v_user_idx, v_user_name, v_user_phone, v_report_count, v_admin
    FROM DUAL;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      NULL;
  END GetUser;
  
  -- 사용자 확인 함수 구현
  FUNCTION CheckUser (
    p_username IN VARCHAR2,
    p_password IN VARCHAR2
  ) RETURN NUMBER AS
    v_user_id NUMBER;
  BEGIN
    -- 사용자 확인 로직 구현
    SELECT USER_IDX INTO v_user_id FROM USERS WHERE USER_NAME = p_username AND USER_PW = p_password;
    RETURN v_user_id;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      RETURN NULL;
  END CheckUser;
  
  -- 사용 중인 사용자 확인 함수 구현
  FUNCTION CheckUsingUser (
    p_phone IN VARCHAR2
  ) RETURN NUMBER AS
    v_result NUMBER;
  BEGIN
    -- 사용 중인 사용자 확인 로직 구현
    SELECT COUNT(*) INTO v_result FROM USERS WHERE USER_PHONE = p_phone;
    RETURN v_result;
  END CheckUsingUser;
  
  -- 사용자의 신고 누적 횟수 확인 함수 구현
  FUNCTION CheckReportCount (
    p_userIdx IN NUMBER
  ) RETURN NUMBER AS
    v_reportCount NUMBER;
  BEGIN
    -- 사용자의 신고 누적 횟수 확인 로직 구현
    SELECT REPORT_COUNT INTO v_reportCount FROM USERS WHERE USER_IDX = p_userIdx;
    RETURN v_reportCount;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      RETURN -1;
  END CheckReportCount;
  
END UserManagementPackage;
/
