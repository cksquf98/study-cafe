package Admin;

import java.sql.*;
import DB.DBConnection;
import DB.JdbcConnection;

public class MenuDAO {

	Connection conn = null;
	PreparedStatement pstmt = null; 
    public MenuDAO() {
    	// JDBC 연결 설정
        conn = DBConnection.getConnection();
    }

    // 음료 정보 조회(이름, 이미지, 가격) (고객용)
    public MenuVO getMenuInfo(int drinkIdx) {
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
            
            cstmt.close();
            //conn.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        
        return menuInfo;
    }


    
    public int getMenuCount() {
        int result = 0; // 결과를 저장할 변수를 try 블록 내부에서 선언

        try {
            // 패키지의 함수 호출을 위한 CallableStatment 생성
            String menuCount = "{ ? = call menu_inventory.get_menu_count()}";
            CallableStatement callableStatement = conn.prepareCall(menuCount);

            // 출력 매개변수 설정
            callableStatement.registerOutParameter(1, java.sql.Types.INTEGER);

            // 함수 호출
            callableStatement.execute();

            // 결과 가져오기
            result = callableStatement.getInt(1);

            // 연결 종료
            callableStatement.close();
           // conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result; // 결과 반환
     
    }
    
    // PL/SQL문의 get_menu_remaining과 매치 (관리자용)
    public MenuVO getMenuStock(int drinkIdx) {
    	String stockQuery = null;
    	MenuVO menuInfo = null;
        
        try {
            // 패키지의 함수 호출을 위한 CallableStatement 생성
            stockQuery = "{call menu_inventory.get_menu_remaining(?, ?, ?, ?)}";
            CallableStatement cstmt = conn.prepareCall(stockQuery);

            // 입력 매개변수 설정
            cstmt.setInt(1, drinkIdx);
            
            // 출력 매개변수 설정
            cstmt.registerOutParameter(2, Types.VARCHAR); //음료 이름
            cstmt.registerOutParameter(3, Types.NUMERIC); //재고
            cstmt.registerOutParameter(4, Types.VARCHAR); //음료 이미지            
            // 함수 호출
            cstmt.execute();

            // 결과 가져오기
            String drinkName = cstmt.getString(2);
            int remaining = cstmt.getInt(3);
            String drinkImage = cstmt.getString(4);
            
            menuInfo = new MenuVO(drinkIdx, drinkName, remaining, drinkImage);
            
            cstmt.close();
            //conn.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        
        return menuInfo;
    }
    
    public MenuVO addDrink(int drinkIdx, int addStock) {
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
            if (totalRemaining == -1) {
            	System.out.println("Drink Not Found");
            }
            else {
            	menu = new MenuVO(drinkIdx);
            	menu.setRemaining(totalRemaining);
            }
            
            
            cstmt.close();
            //conn.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        
        return menu;
    }

}
