package StudyCafe.Menu;

import StudyCafe.Admin.MenuVO;
import StudyCafe.UserVO;
import StudyCafe.Admin.MenuDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    
    public Kiosk(int selectedSeatNumber, UserVO user) {
    	this.user = user;
    	this.selectedSeatNumber = selectedSeatNumber;

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

        totalLabel = new JLabel("음료 금액: 0원", SwingConstants.CENTER);
        bottomPanel.add(totalLabel);

        paymentButton = new JButton("결제");
        paymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!orderList.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "결제화면으로 넘어갑니다\n" + totalAmount + "원");
                    
                    // Order로 넘겨주기
                    //new Order(selectedSeatNumber, user, orderList);
                    
                    // 새 화면을 위한 초기화
                    orderList.clear();
                    orderListPanel.removeAll();
                    totalAmount = 0;
                    totalLabel.setText("음료 금액: 0원");
                    revalidate();
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(null, "선택한 음료가 없습니다.");
                    // 선택한 음료가 없으면 이 페이지에 그냥 있을 것(추가 코드 없음)
                }
            }
        });
        bottomPanel.add(paymentButton);

        nextButton = new JButton("넘어가기");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (orderList.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "음료를 선택하지 않았습니다.\n  다음 화면으로 넘어갑니다.");
                    // 다음 단계로 넘어가기(좌석 결제 화면으로 이동) 
                    //new Order(selectedSeatNumber, user, orderList);
                    
                } else {
                    JOptionPane.showMessageDialog(null, "  음료를 선택하지 않고\n다음 화면으로 넘어갑니다.");
                    //new Order(selectedSeatNumber, user, orderList);
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
                            totalLabel.setText("음료 금액: " + totalAmount + " 원");
                		}
                	}
                	else {
                		addOrderItem(menu);
                		totalAmount += menu.getDrinkPrice();
                		totalLabel.setText("음료 금액: " + totalAmount + " 원");
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
                totalLabel.setText("음료 금액: " + totalAmount + "원");
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
                    totalLabel.setText("음료 금액: " + totalAmount + "원");
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
                totalLabel.setText("음료 금액: " + totalAmount + "원");
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

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new Kiosk(selectedSeatNumber, user).setVisible(true);
//            }
//        });
//    }
}
