package Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class MenuInventory extends JFrame {
    private JPanel mainPanel;
    MenuDAO menuDAO;
    
    
    public MenuInventory() {
        setTitle("음료 재고 관리");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuDAO = new MenuDAO();

        //beverages = new HashMap<>();
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
        
 
        int menuCount = menuDAO.getMenuCount();
        for (int i = 1; i <= menuCount; i++) {
        	MenuVO menu = menuDAO.getMenuStock(i);
        	if (menu != null) {
        		addBeveragePanel(menu);
        	}
        }
        
        JButton confirmButton = new JButton("확인");
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 확인 버튼을 눌렀을 때 수행할 작업
                JOptionPane.showMessageDialog(mainPanel, "확인 버튼이 클릭되었습니다.");
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        bottomPanel.add(confirmButton);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addBeveragePanel(MenuVO menu) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 음료 이름, 이미지 출력
        JPanel imageNamePanel = new JPanel();
        imageNamePanel.setLayout(new BoxLayout(imageNamePanel, BoxLayout.Y_AXIS));
        JLabel nameLabel = new JLabel(menu.getDrinkName());
        JLabel imageLabel = new JLabel(new ImageIcon(menu.getDrinkImage()));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imageNamePanel.add(imageLabel);
        imageNamePanel.add(nameLabel);
       
        // 재고 관리(일정 수 이하이면 색이 빨간색으로 변함) 
        JLabel stockLabel = new JLabel("재고 수량: " + menu.getRemaining());
        menu.setStockLabel(stockLabel);
        updateStockLabelColor(menu);
        
        
        JButton orderButton = new JButton("주문하기");
        orderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                orderBeverage(menu);
           }
        });
    
        // 음료 위치와 이름 (왼쪽)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(imageNamePanel, gbc);

        // 재고 수량 위치 (가운데)
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(stockLabel, gbc);

        // 주문 버튼 위치 (오른쪽)
        gbc.gridx = 50;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(orderButton, gbc);

        panel.setMaximumSize(new Dimension(600, 150));
        mainPanel.add(panel);

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(600, 1));
        mainPanel.add(separator);
        mainPanel.revalidate();
    }

    private void orderBeverage(MenuVO menu) {
        JDialog orderDialog = new JDialog(this, "주문하기 - " + menu.getDrinkName(), true);
        orderDialog.setSize(300, 200);
        orderDialog.setLayout(new FlowLayout());

        JLabel orderLabel = new JLabel("주문 수량:");
        JTextField quantityField = new JTextField("0", 5);
        JButton plusButton = new JButton("+");
        JButton minusButton = new JButton("-");

        plusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int quantity = Integer.parseInt(quantityField.getText());
                quantityField.setText(String.valueOf(++quantity));
            }
        });

        minusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity > 0) {
                    quantityField.setText(String.valueOf(--quantity));
                }
            }
        });

        JButton confirmButton = new JButton("확인");
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int quantity = Integer.parseInt(quantityField.getText());
                MenuVO updateMenu = menuDAO.addDrink(menu.getDrinkIdx(), quantity); // addDrink 메서드를 한 번만 호출하여 업데이트된 재고를 가져옴
                
                if (updateMenu != null) {
                	int newRemaining = updateMenu.getRemaining();  

                	// 업데이트된 재고로만 계산하도록 수정
                	menu.setRemaining(newRemaining);
                	menu.getStockLabel().setText("재고 수량: " + menu.getRemaining());
                	updateStockLabelColor(menu);
                }else {
                	System.out.println("선택된 음료가 없습니다");
                }
                orderDialog.dispose();
            }
        });

        orderDialog.add(orderLabel);
        orderDialog.add(minusButton);
        orderDialog.add(quantityField);
        orderDialog.add(plusButton);
        orderDialog.add(confirmButton);

        orderDialog.setVisible(true);
    }

    
    private void updateStockLabelColor(MenuVO menu) {
        if (menu.getRemaining() <= 10) {
            menu.getStockLabel().setForeground(Color.RED);
        } else {
            menu.getStockLabel().setForeground(Color.BLACK);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new MenuInventory().setVisible(true);
            }
        });
    }
}
