--신고 관련 PL/SQL
----------------------------------------------------------
--Trigger
create or replace TRIGGER update_seat_on_report
BEFORE INSERT ON Reports
FOR EACH ROW
DECLARE
    reportCount NUMBER;
    seatIndex NUMBER;
BEGIN
    -- 신고된 사용자의 report_count와 seat_idx 조회
    SELECT u.report_count, s.seat_idx INTO reportCount, seatIndex
    FROM Users u, Seats s
    WHERE u.user_idx = :NEW.reported_user AND s.user_idx = :NEW.reported_user;

    -- 해당 사용자에 대한 정보가 없을 경우 트리거를 종료합니다.
    IF reportCount IS NULL THEN
        RETURN;
    END IF;

    -- report_count가 4 이하인 유저라면 신고 접수되고 cnt+1
    IF reportCount < 4 THEN
        UPDATE Users
        SET report_count = report_count + 1
        WHERE user_idx = :NEW.reported_user;
    END IF;

    -- report_count = 4인 유저의 경우 신고 접수 시 5번 누적되는거니까 좌석 OUT
    IF reportCount = 4 THEN
        UPDATE Seats
        SET user_idx = NULL, seat_start_time = NULL, seat_end_time = NULL
        WHERE seat_idx = seatIndex;
        UPDATE Users
        SET report_count = 5
        WHERE user_idx = :NEW.reported_user;
    END IF;
END;


--View
--먼저 만들어져있던 뷰가 있다면 삭제해주세용.
--drop view report_users_view;

CREATE OR REPLACE VIEW Report_Users_View AS
SELECT R.report_idx,
       U.user_idx as reporting_user_idx,
       U.user_name AS reporting_user,
       R.report_category,
       TO_CHAR(R.report_date, 'YYYY-MM-DD HH24:MI:SS') AS report_date,
       R.report_status
  FROM Reports R
  INNER JOIN Users U ON R.reporting_user = U.user_idx;

--Procedure
 -- 모든 유저의 신고 내역 조회 프로시저
CREATE OR REPLACE PROCEDURE getReportAll (
    p_cursor OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN p_cursor FOR
    SELECT * FROM Report_Users_View
    ORDER BY report_date;
END;
/

-- 사용자의 신고 내역 조회 프로시저
create or replace PROCEDURE getUserReport (
  p_userIdx IN NUMBER,
  p_cursor OUT SYS_REFCURSOR
) AS
BEGIN
  OPEN p_cursor FOR
  SELECT 
    report_idx, 
    reporting_user_idx, 
    reporting_user, 
    report_category, 
    report_date, 
    report_status
  FROM 
    REPORT_USERS_VIEW 
  WHERE 
    reporting_user_idx = p_userIdx
  ORDER BY report_date desc;
END;
/
