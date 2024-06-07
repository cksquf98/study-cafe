package StudyCafe.Admin;

import javax.swing.*;

public class MenuVO {
    private int drinkIdx;
    private String drinkName;
    private int drinkPrice;
    private int remaining;
    private String drinkImage;
    private JLabel stockLabel;

    public MenuVO() {

    }

    public MenuVO(int drinkIdx) {
        this.drinkIdx = drinkIdx;
    }
    public MenuVO(int drinkIdx, int remaining) {
        this.drinkIdx = drinkIdx;
        this.remaining = remaining;
    }

    public MenuVO(int drinkIdx, String drinkName, int remaining, String drinkImage) {
        this.drinkIdx = drinkIdx;
        this.drinkName = drinkName;
        this.remaining = remaining;
        this.drinkImage = drinkImage;
    }

    public MenuVO(int drinkIdx, String drinkName, int drinkPrice, int remaining, String drinkImage) {
        this.drinkIdx = drinkIdx;
        this.drinkName = drinkName;
        this.drinkPrice = drinkPrice;
        this.remaining = remaining;
        this.drinkImage = drinkImage;
    }


    public int getDrinkIdx() {
        return drinkIdx;
    }

    public void setDrinkIdx(int drinkIdx) {
        this.drinkIdx = drinkIdx;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public int getDrinkPrice() {
        return drinkPrice;
    }

    public void setDrinkPrice(int drinkPrice) {
        this.drinkPrice = drinkPrice;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public String getDrinkImage() {
        return drinkImage;
    }

    public void setDrinkImage(String dinkImage) {
        this.drinkImage = drinkImage;
    }

    // 재고 관리: 일정 수량 이하이면 JLabel의 색깔을 변경하기 위해 getter, setter 사용
    public JLabel getStockLabel() {
        return stockLabel;
    }

    public void setStockLabel(JLabel stockLabel) {
        this.stockLabel = stockLabel;
    }
}
