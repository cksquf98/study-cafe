--좌석 관련 PL/SQL
----------------------------------------------------------
--좌석 데이터 Insert
alter table seats modify(user_idx NULL);


-- Seats-Price column
-- 1~10번 좌석 2000원
-- 11~20번 좌석 4000원
-- 21~30번 좌석 5000원
-- 31~35번 좌석 7000원

CREATE SEQUENCE seq_seat_idx
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

DECLARE
BEGIN
    -- 좌석 데이터 추가 (2000원 가격으로 10개)
    FOR i IN 1..10 LOOP
        INSERT INTO seats (seat_idx, seat_start_time, seat_end_time, seat_price, waiting_user, user_idx)
        VALUES (
            seq_seat_idx.NEXTVAL,
            NULL,
            NULL,
            2000,
            NULL,
            NULL
        );
    END LOOP;

    -- 좌석 데이터 추가 (4000원 가격으로 10개)
    FOR i IN 1..10 LOOP
        INSERT INTO seats (seat_idx, seat_start_time, seat_end_time, seat_price, waiting_user, user_idx)
        VALUES (
            seq_seat_idx.NEXTVAL,
            NULL,
            NULL,
            4000,
            NULL,
            NULL
        );
    END LOOP;

    -- 좌석 데이터 추가 (5000원 가격으로 10개)
    FOR i IN 1..10 LOOP
        INSERT INTO seats (seat_idx, seat_start_time, seat_end_time, seat_price, waiting_user, user_idx)
        VALUES (
            seq_seat_idx.NEXTVAL,
            NULL,
            NULL,
            5000,
            NULL,
            NULL
        );
    END LOOP;

    -- 좌석 데이터 추가 (7000원 가격으로 5개)
    FOR i IN 1..5 LOOP
        INSERT INTO seats (seat_idx, seat_start_time, seat_end_time, seat_price, waiting_user, user_idx)
        VALUES (
            seq_seat_idx.NEXTVAL,
            NULL,
            NULL,
            7000,
            NULL,
            NULL
        );
    END LOOP;
END;
/
--Procedure
--유저정보 입력 시 금일 해당 유저의 좌석 정보 출력 : NULL값은 -1로 출력되도록 함
CREATE OR REPLACE PROCEDURE getUserSeat (
    p_userIdx IN NUMBER,
    p_cursor OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_cursor FOR
    SELECT seat_idx,
           TO_CHAR(seat_start_time,'YYYY-MM-DD HH24:MI:SS') AS seat_start_time,
           TO_CHAR(seat_end_time,'YYYY-MM-DD HH24:MI:SS') AS seat_end_time,
           seat_price,
           NVL(waiting_user, -1) AS waiting_user,
           NVL(user_idx, -1) AS user_idx
    FROM SEATS
    WHERE user_idx = p_userIdx
      AND TO_CHAR(seat_start_time,'YYYY-MM-DD') = TO_CHAR(SYSDATE,'YYYY-MM-DD');
END;
/


--좌석에 앉아있는 유저 신고
CREATE OR REPLACE PROCEDURE InsertReport (
    p_user_idx IN NUMBER,
    p_seat_number IN NUMBER,
    p_selected_option IN NUMBER,
    p_result OUT NUMBER
) AS
    v_reported_user_idx NUMBER;
BEGIN
    v_reported_user_idx := GetUserIdx(p_seat_number);
    IF v_reported_user_idx IS NOT NULL THEN
        INSERT INTO Reports (report_idx, reporting_user, reported_user, report_category, report_date)
        VALUES (seq_report_idx.NEXTVAL, p_user_idx, v_reported_user_idx, p_selected_option, SYSDATE);
        COMMIT;
        p_result := 1; -- 넣기 성공
    ELSE
        p_result := -1; -- 실패
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_result := -1; -- 예외발생 실패
END InsertReport;
/
