package myLib;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.*; 
import javax.swing.*;


public class Menu {
	
	@SuppressWarnings("serial")
	public class MFrame extends JFrame {
		JFrame frame = new JFrame("Menu");
		JPanel northPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel southPanel = new JPanel();
		JButton stockInButton = new JButton("Stock in");
		JButton stockQueryButton = new JButton("Query");
		JButton brButton = new JButton("Borrow\\Return");
		JButton cardButton = new JButton("Card");
		JButton exitButton = new JButton("Exit");
		JButton regButton = new JButton("Admin Register");
		public MFrame() {			
			setWindows();
			EventsListener();
		}
		public void setWindows(){
			frame.setIconImage(new ImageIcon("icon/messages.png").getImage());
			frame.setLayout(new BorderLayout());
			frame.add(northPanel, "North");
			frame.add(centerPanel, "Center");
			frame.add(southPanel, "South");
			northPanel.setLayout(new GridLayout(1,2));
			centerPanel.setLayout(new GridLayout(1,2));
			southPanel.setLayout(new GridLayout(1,2));
			northPanel.add(stockInButton);
			northPanel.add(stockQueryButton);
			centerPanel.add(brButton);
			centerPanel.add(cardButton);
			southPanel.add(regButton);
			southPanel.add(exitButton);
			northPanel.setPreferredSize(new Dimension(0,100));//修改panel的宽高
			southPanel.setPreferredSize(new Dimension(0,80));
			if(LogIn.Lmode == 0) {cardButton.setEnabled(false); stockInButton.setEnabled(false); regButton.setEnabled(false);}
			frame.setSize(300, 300);
			int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
			int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
			frame.setLocation((screen_width - frame.getWidth()) / 2,
					(screen_height - frame.getHeight()) / 2);
			frame.setVisible(true);
			frame.setResizable(false);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		public void EventsListener(){
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					frame.dispose();
					new LogIn().new LFrame().frame.setVisible(true);
				}
			});
			exitButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.dispose();
					new LogIn().new LFrame().frame.setVisible(true);
				}
			});
			stockInButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(LogIn.Lmode == 0) {
						JOptionPane.showMessageDialog(frame, 
								"Sorry! You have no authority!",
								"Error", JOptionPane.ERROR_MESSAGE);
					}
					else {
						new StockIn().new SIFrame();
					frame.dispose();
					}
				}
			});
			stockQueryButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new Query().new QFrame();
					frame.dispose();
				}
			});
			brButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					new Bor_Ret().new BRFrame();
					frame.dispose();
				}
			});
			regButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(LogIn.Lmode == 0) {
						JOptionPane.showMessageDialog(frame, 
								"Sorry! You have no authority!",
								"Error", JOptionPane.ERROR_MESSAGE);
					}
					else {
						new Register().new RFrame();
						frame.dispose();
					}
				}
			});
			cardButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(LogIn.Lmode == 0) {
						JOptionPane.showMessageDialog(frame, 
								"Sorry! You have no authority!",
								"Error", JOptionPane.ERROR_MESSAGE);
					}
					else {
						new Card().new CFrame();
						frame.dispose();
					}
				}
			});
		}
	}
}
