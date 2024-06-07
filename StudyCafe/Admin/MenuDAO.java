package StudyCafe.Admin;

import java.sql.*;
import StudyCafe.Admin.MenuVO;
import StudyCafe.DB.DBConnection;

public class MenuDAO {
    Connection conn = null;
    CallableStatement cstmt = null;

    // 음료 정보 조회(이름, 이미지, 가격) (고객용)
    public MenuVO getMenuInfo(int drinkIdx) throws SQLException {
        conn = DBConnection.getConnection();

        String menuQuery = null;
        MenuVO menuInfo = null;

        try {
            // 패키지의 함수 호출을 위한 CallableStatement 생성
            menuQuery = "{call menu_inventory.get_menu_info(?, ?, ?, ?, ?)}";
            CallableStatement cstmt = conn.prepareCall(menuQuery);

            // 입력 매개변수 설정
            cstmt.setInt(1, drinkIdx);

            // 출력 매개변수 설정
            cstmt.registerOutParameter(2, Types.VARCHAR); //음료 이름
            cstmt.registerOutParameter(3, Types.NUMERIC); //음료 가격
            cstmt.registerOutParameter(4, Types.NUMERIC); //음료 재고
            cstmt.registerOutParameter(5, Types.VARCHAR); //음료 이미지
            // 함수 호출
            cstmt.execute();

            // 결과 가져오기
            String drinkName = cstmt.getString(2);
            int drinkPrice = cstmt.getInt(3);
            int remaining = cstmt.getInt(4);
            String drinkImage = cstmt.getString(5);

            menuInfo = new MenuVO(drinkIdx, drinkName, drinkPrice, remaining, drinkImage);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return menuInfo;
    }



    public int getMenuCount() throws SQLException {
        conn = DBConnection.getConnection();
        int result = 0; // 결과를 저장할 변수를 try 블록 내부에서 선언

        try {
            // 패키지의 함수 호출을 위한 CallableStatment 생성
            String menuCount = "{ ? = call menu_inventory.get_menu_count()}";
            CallableStatement callableStatement = conn.prepareCall(menuCount);

            // 출력 매개변수 설정
            callableStatement.registerOutParameter(1, Types.INTEGER);

            // 함수 호출
            callableStatement.execute();

            // 결과 가져오기
            result = callableStatement.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return result; // 결과 반환

    }

    public MenuVO addDrink(int drinkIdx, int addStock) throws SQLException {
        System.out.println(drinkIdx + " " + addStock);
        conn = DBConnection.getConnection();
        String addQuery = null;
        MenuVO menu = null;

        try {
            // 패키지의 함수 호출을 위한 CallableStatement 생성
            addQuery = "{call menu_inventory.add_drink(?, ?, ?)}";
            CallableStatement cstmt = conn.prepareCall(addQuery);

            // 입력 매개변수 설정
            cstmt.setInt(1, drinkIdx);
            cstmt.setInt(2, addStock);

            // 출력 매개변수 설정
            cstmt.registerOutParameter(3, Types.INTEGER);
            
            // 함수 호출
            cstmt.execute();

            // 결과 가져오기
            int totalRemaining = cstmt.getInt(3);
            System.out.println(totalRemaining);
            if (totalRemaining == -1) {
                System.out.println("Drink Not Found");
            } else {
                menu = new MenuVO(drinkIdx);
                menu.setRemaining(totalRemaining);
            }

            // 커밋 명시적 수행
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            // 예외 발생 시 롤백
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            // 리소스 정리
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return menu;
    }

    public MenuVO minusDrink(int drinkIdx, int minusStock) throws SQLException {
        System.out.println(drinkIdx + " " + minusStock);
        conn = DBConnection.getConnection();
        String addQuery = null;
        MenuVO menu = null;

        try {
            // 패키지의 함수 호출을 위한 CallableStatement 생성
            addQuery = "{call menu_inventory.minus_drink(?, ?, ?)}";
            CallableStatement cstmt = conn.prepareCall(addQuery);

            // 입력 매개변수 설정
            cstmt.setInt(1, drinkIdx);
            cstmt.setInt(2, minusStock);

            // 출력 매개변수 설정
            cstmt.registerOutParameter(3, Types.INTEGER);
            
            // 함수 호출
            cstmt.execute();

            // 결과 가져오기
            int totalRemaining = cstmt.getInt(3);
            System.out.println(totalRemaining);
            if (totalRemaining == -1) {
                System.out.println("Drink Not Found");
            } else {
                menu = new MenuVO(drinkIdx);
                menu.setRemaining(totalRemaining);
            }

            // 커밋 명시적 수행
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            // 예외 발생 시 롤백
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            // 리소스 정리
            if (cstmt != null) {
                try {
                    cstmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return menu;
    }


    private void closeResources() {
        try {
//            if (rs != null) rs.close();
            if (cstmt != null) cstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
