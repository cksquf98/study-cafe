package StudyCafe.Menu;

import StudyCafe.Admin.MenuVO;
import StudyCafe.MyPage;
import StudyCafe.Order.OrderDAO;
import StudyCafe.Seats.SeatDao;
import StudyCafe.UserVO;
import StudyCafe.Admin.MenuDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Kiosk extends JFrame {
    private JPanel drinkPanel;
    private JPanel orderPanel;
    private JPanel bottomPanel;
    private JPanel orderListPanel;
    private JScrollPane orderScrollPane;
    private JLabel totalLabel;
    private JButton paymentButton;
    private JButton nextButton;

    private Map<Integer, Integer> orderList;
    private int totalAmount;
    MenuDAO menuDAO;
    private static UserVO user;
    private static int selectedSeatNumber;
    private static int usingTime;
    
    public Kiosk(int selectedSeatNumber, UserVO user, int usingTime) throws SQLException {
        this.user = user;
        this.selectedSeatNumber = selectedSeatNumber;
        this.usingTime = usingTime;

        // GUI 초기화
        setTitle("음료 키오스크");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setLayout(new BorderLayout());

        totalAmount = 0;
        menuDAO = new MenuDAO();
        orderList = new HashMap<>();    // 음료와 선택 수량을 저장하는 HashMap

        // 상단 음료 패널
        drinkPanel = new JPanel();
        drinkPanel.setLayout(new GridLayout(3, 4));
        add(drinkPanel, BorderLayout.NORTH);

        // 음료 메뉴 로드 및 패널 추가
        int menuCount = menuDAO.getMenuCount();
        for (int i = 1; i <= menuCount; i++) {
            MenuVO menu = menuDAO.getMenuInfo(i);
            if (menu != null) {
                addBeveragePanel(menu);
            }
        }

        SeatDao seatDao = new SeatDao();
        try {
            int seatPrice = seatDao.getSeatPrice(selectedSeatNumber);
            totalAmount += seatPrice*usingTime;
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // 하단 주문 패널
        orderPanel = new JPanel();
        orderPanel.setLayout(new BorderLayout());

        orderListPanel = new JPanel();
        orderListPanel.setLayout(new BoxLayout(orderListPanel, BoxLayout.Y_AXIS));

        orderScrollPane = new JScrollPane(orderListPanel);
        orderPanel.add(orderScrollPane, BorderLayout.CENTER);

        add(orderPanel, BorderLayout.CENTER);

        // 하단 결제 패널
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());

        totalLabel = new JLabel("총 금액: " + totalAmount + "원", SwingConstants.CENTER);
        bottomPanel.add(totalLabel);

        paymentButton = new JButton("결제");




        paymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!orderList.isEmpty()) {
                    StringBuilder beverageDialog = new StringBuilder();
                    for (Map.Entry<Integer, Integer> entry : orderList.entrySet()) {
                        MenuVO InfoMenu = null;
                        MenuVO updateMenu = null;
                        try {
                            InfoMenu = menuDAO.getMenuInfo(entry.getKey());			// 결제 정보를 표시
                            updateMenu = menuDAO.minusDrink(entry.getKey(), entry.getValue());		// 주문한 수량만큼 재고에 반영

                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                        
                        
                        String beverage = InfoMenu.getDrinkName();
                        int quan = entry.getValue();
                        beverageDialog.append(beverage).append(": ").append(quan).append("잔\n");
                        
                        if (updateMenu != null) {
                        	int newRemaining = updateMenu.getRemaining();
                        	InfoMenu.setRemaining(newRemaining);
                        }
                    }
                    String dialog = "결제가 완료되었습니다.\n\n"
                            + "좌석 번호: " + selectedSeatNumber + "\n"
                            + "사용 시간: " + usingTime + "시간\n"
                            + "<음료 내역>\n" + beverageDialog + "\n\n"
                            + "총 금액: " + totalAmount + "원";
                    OrderDAO orderDao = new OrderDAO();
                    for (Map.Entry<Integer, Integer> entry : orderList.entrySet()) {
                        for (int i = 0; i < entry.getValue(); i++) {
                            try {
                                orderDao.insertOrder(user.getUserIdx(), entry.getKey(), totalAmount,selectedSeatNumber, usingTime);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    JOptionPane.showMessageDialog(null, dialog);


                    // 새 화면을 위한 초기화
                    orderList.clear();
                    orderListPanel.removeAll();
                    totalAmount = 0;
                    totalLabel.setText("총 금액: " + totalAmount +"원");
                    revalidate();
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(null, "선택한 음료가 없습니다.");
                    // 선택한 음료가 없으면 이 페이지에 그냥 있을 것(추가 코드 없음)
                }

                //결제된 후에 MyPage로 보낼라고
                MyPage myPage = null;
                try {
                    myPage = new MyPage(user);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                myPage.setVisible(true);
                dispose();

            }
        });
        bottomPanel.add(paymentButton);

        nextButton = new JButton("넘어가기");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (orderList.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "음료를 선택하지 않았습니다.\n좌석만 결제됩니다.\n\n"
                            + "좌석 번호: " + selectedSeatNumber + "\n\n"
                            + "총 금액: " + totalAmount + "원");
                    //결제된 후에 MyPage로 보낼라고
                    MyPage myPage = null;
                    try {
                        myPage = new MyPage(user);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    myPage.setVisible(true);
                    dispose();

                } else {
                    JOptionPane.showMessageDialog(null, "  음료를 주문하지 않고\n좌석만 결제됩니다."
                            + "좌석 번호: " + selectedSeatNumber + "\n\n"
                            + "총 금액: " + totalAmount + "원");
                    //결제된 후에 MyPage로 보낼라고
                    MyPage myPage = null;
                    try {
                        myPage = new MyPage(user);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    myPage.setVisible(true);
                    dispose();
                }
            }
        });
        bottomPanel.add(nextButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addBeveragePanel(MenuVO menu) {
        // 음료 버튼 생성
        JButton drinkButton = new JButton();
        drinkButton.setLayout(new BorderLayout());

        // 음료 이미지
        JLabel drinkImageLabel = new JLabel(new ImageIcon(menu.getDrinkImage()));
        drinkImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        drinkButton.add(drinkImageLabel, BorderLayout.CENTER);

        // 음료 이름과 가격
        JLabel drinkLabel = new JLabel("<html>" + menu.getDrinkName() + "<br>" + menu.getDrinkPrice() + "원</html>", SwingConstants.CENTER);
        drinkButton.add(drinkLabel, BorderLayout.SOUTH);

        // 버튼에 액션 리스너 추가
        drinkButton.setActionCommand(menu.getDrinkName());
        drinkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (menu.getRemaining() > 0) {
                    if (orderList.containsKey(menu.getDrinkIdx())) {
                        int quantity = orderList.get(menu.getDrinkIdx());
                        if (quantity >= menu.getRemaining()) {
                            JOptionPane.showMessageDialog(null, "재고가 부족합니다.");
                        }
                        else {
                            addOrderItem(menu);
                            totalAmount += menu.getDrinkPrice();
                            totalLabel.setText("총 금액: " + totalAmount + " 원");
                        }
                    }
                    else {
                        addOrderItem(menu);
                        totalAmount += menu.getDrinkPrice();
                        totalLabel.setText("총 금액: " + totalAmount + " 원");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "품절입니다.");
                }
            }
        });

        drinkPanel.add(drinkButton);
    }

    private void addOrderItem(MenuVO menu) {
        if (orderList.containsKey(menu.getDrinkIdx())) {
            int quantity = orderList.get(menu.getDrinkIdx());
            if (quantity < menu.getRemaining()) {
                orderList.put(menu.getDrinkIdx(), quantity + 1);
                updateOrderItem(menu);
            } else {
                JOptionPane.showMessageDialog(null, "재고가 부족합니다.");
            }
        } else {
            orderList.put(menu.getDrinkIdx(), 1);
            createOrderItem(menu);
        }
    }

    private void createOrderItem(MenuVO menu) {
        JPanel orderItemPanel = new JPanel();
        orderItemPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        orderItemPanel.setPreferredSize(new Dimension(600, 50));
        orderItemPanel.setMaximumSize(new Dimension(600, 50));

        String whiteSpace = "               ";
        JLabel orderItemLabel = new JLabel(menu.getDrinkName() + whiteSpace + 1 + "잔" + whiteSpace + menu.getDrinkPrice() + "원");
        orderItemLabel.setPreferredSize(new Dimension(300, 50));
        orderItemPanel.add(orderItemLabel);

        JPanel quantityPanel = new JPanel();
        quantityPanel.setLayout(new FlowLayout());

        JButton minusButton = new JButton("-");
        minusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int quantity = orderList.get(menu.getDrinkIdx());
                if (quantity > 1) {
                    orderList.put(menu.getDrinkIdx(), quantity - 1);
                    totalAmount -= menu.getDrinkPrice();
                    updateOrderItem(menu);
                }
                if (quantity - 1 == 1) {
                    minusButton.setEnabled(false);
                }
                totalLabel.setText("총 금액: " + totalAmount + "원");
            }
        });
        minusButton.setEnabled(false); // 초기 수량은 1이므로 비활성화
        quantityPanel.add(minusButton);

        JLabel quantityLabel = new JLabel("1");
        quantityPanel.add(quantityLabel);

        JButton plusButton = new JButton("+");
        plusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int quantity = orderList.get(menu.getDrinkIdx());
                if (quantity < menu.getRemaining()) {
                    orderList.put(menu.getDrinkIdx(), quantity + 1);
                    totalAmount += menu.getDrinkPrice();
                    minusButton.setEnabled(true);
                    updateOrderItem(menu);
                    totalLabel.setText("총 금액: " + totalAmount + "원");
                } else {
                    JOptionPane.showMessageDialog(null, "재고가 부족합니다.");
                }
            }
        });

        quantityPanel.add(plusButton);

        // 취소 버튼과 수량 조절 버튼 간 여백 주기
        JLabel spaceLabel = new JLabel("                           ");
        quantityPanel.add(spaceLabel);

        JButton cancelButton = new JButton("취소");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int quantity = orderList.get(menu.getDrinkIdx());
                totalAmount -= menu.getDrinkPrice() * quantity;
                orderList.remove(menu.getDrinkIdx());
                orderListPanel.remove(orderItemPanel);
                totalLabel.setText("총 금액: " + totalAmount + "원");
                revalidate();
                repaint();
            }
        });
        quantityPanel.add(cancelButton);

        orderItemPanel.add(quantityPanel);

        orderListPanel.add(orderItemPanel);
        revalidate();
        repaint();
    }

    private void updateOrderItem(MenuVO menu) {
        for (Component component : orderListPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel orderItemPanel = (JPanel) component;
                JLabel orderItemLabel = (JLabel) orderItemPanel.getComponent(0);
                String labelText = orderItemLabel.getText();
                if (labelText.startsWith(menu.getDrinkName())) {
                    int quantity = orderList.get(menu.getDrinkIdx());
                    String whiteSpace = "               ";
                    orderItemLabel.setText(menu.getDrinkName() + whiteSpace + quantity + "잔" + whiteSpace + menu.getDrinkPrice() * quantity + "원");
                    JPanel quantityPanel = (JPanel) orderItemPanel.getComponent(1);
                    JLabel quantityLabel = (JLabel) quantityPanel.getComponent(1);
                    quantityLabel.setText(String.valueOf(quantity));

                    JButton minusButton = (JButton) quantityPanel.getComponent(0);
                    minusButton.setEnabled(quantity > 1);

                    break;
                }
            }
        }
        revalidate();
        repaint();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
					new Kiosk(selectedSeatNumber, user, usingTime).setVisible(true);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	}
        });
    }
}
