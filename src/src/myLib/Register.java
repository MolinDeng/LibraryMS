package myLib;

import java.sql.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.*; 
import javax.swing.*;

public class Register {
	@SuppressWarnings("serial")
	public class RFrame extends JFrame {
		JFrame frame = new JFrame("Register");
		JPanel centerPanel = new JPanel(new BorderLayout());
		JPanel southPanel = new JPanel();
		JButton confirmButton = new JButton("Confirm");
		JButton cancelButton = new JButton("Cancel");
		JTextField anoField = new JTextField();
		JPasswordField pwField = new JPasswordField();
		JTextField nameField = new JTextField();
		JTextField telField = new JTextField();
		JLabel anoLabel = new JLabel("Username*");
		JLabel pwLabel = new JLabel("Password*");
		JLabel nameLabel = new JLabel("Name*");
		JLabel telLabel = new JLabel("tel");
		public RFrame() {
			setWindows();
			EventsListener();
		}
		public void setWindows() {
			frame.setIconImage(new ImageIcon("icon/messages.png").getImage());
			frame.add(centerPanel, "Center");
			frame.add(southPanel, "South");
			centerPanel.setLayout(new GridLayout(4,2));
			southPanel.setLayout(new GridLayout(1,2));
			centerPanel.add(anoLabel);
			centerPanel.add(anoField);
			centerPanel.add(pwLabel);
			centerPanel.add(pwField);
			centerPanel.add(nameLabel);
			centerPanel.add(nameField);
			centerPanel.add(telLabel);
			centerPanel.add(telField);
			southPanel.add(confirmButton);
			southPanel.add(cancelButton);
			anoLabel.setHorizontalAlignment(SwingConstants.CENTER);
			pwLabel.setHorizontalAlignment(SwingConstants.CENTER);
			nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
			telLabel.setHorizontalAlignment(SwingConstants.CENTER);

			frame.setSize(300,200);
			int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
			int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
			frame.setLocation((screen_width - frame.getWidth()) / 2,
					(screen_height - frame.getHeight()) / 2);
			frame.setVisible(true);
			frame.setResizable(false);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
		}
		public void EventsListener() {
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					frame.dispose();
					new Menu().new MFrame().frame.setVisible(true);
				}
			});
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.dispose();
					new Menu().new MFrame().frame.setVisible(true);
				}
			});
			confirmButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!LogIn.isconn) {
						JOptionPane.showMessageDialog(frame, 
								"Database is not connected!",
								"Error", JOptionPane.ERROR_MESSAGE);
						frame.dispose();
						new LogIn().new LFrame().frame.setVisible(true);
					}
					else {
						SQL_adminInsert();
					}	
				}
			});
		}
		public void SQL_adminInsert() {
			String sql = "insert into admin values(?,?,?,?)";
			String ano = anoField.getText().trim();
			@SuppressWarnings("deprecation")
			String pw = pwField.getText().trim();
			String name = nameField.getText().trim();
			String tel = telField.getText().trim();
			if(ano == null || ano.equals("") || pw == null || pw.equals("") || name == null || name.equals("")) {
				JOptionPane.showMessageDialog(frame, 
						"Username, Password and Name can't be empty!",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			else if(SQL_keyCheck()) {
				JOptionPane.showMessageDialog(frame, 
						"Username already exists",
						"Fail", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				PreparedStatement pStm = LogIn.conn.prepareStatement(sql);
				pStm.setString(1, ano);
				pStm.setString(2, pw);
				pStm.setString(3, name);
				pStm.setString(4, tel);
				pStm.executeUpdate();
				JOptionPane.showMessageDialog(frame, 
						"Account created successfully",
						"Success", JOptionPane.INFORMATION_MESSAGE);
				frame.dispose();
				new Menu().new MFrame().frame.setVisible(true);
			} catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, 
						"Opraetion failed",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		public boolean SQL_keyCheck() {
			String ano = anoField.getText().trim();
			String sql = "select * from admin where ano = ?";
			try {
				PreparedStatement pStm = LogIn.conn.prepareStatement(sql);
				pStm.setString(1, ano);
				ResultSet rs = pStm.executeQuery();
				if(rs.next())	return true;
				else return false;
			}catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, 
						"Checktime error",
						"Error", JOptionPane.ERROR_MESSAGE);
				return true;
			}
		}
	}
}

