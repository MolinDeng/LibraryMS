package myLib;

import java.sql.*;

import java.awt.BorderLayout; 
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.*; 
import javax.swing.*;

public class LogIn {
	private String driverName="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private String dbURL="jdbc:sqlserver://localhost:1433;DatabaseName=LMS";
	private String Name="sa";
	private String Pwd="123";
	public static Connection conn = null;
	public static boolean isconn = false;
	public static String curr_admin = "system";//通过LogIn.curr_user访问，获取当前经手人
	public static String curr_user;//记录当前使用的学生cno
	public static int Lmode = 0;//0 user, 1 admin
	public static void main(String[] args) {
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
		UIManager.put("Button.font", new java.awt.Font("Consolas", 0, 12)); 
		new LogIn().new LFrame();
		
	}
	@SuppressWarnings("serial")
	public class LFrame extends JFrame{
		JFrame frame = new JFrame("Login");
		JPanel northPanel = new JPanel(new BorderLayout());
		JPanel centerPanel = new JPanel(new BorderLayout());
		JPanel southPanel = new JPanel(new BorderLayout());
		JTextField usernameField = new JTextField();
		JTextField cnoField = new JTextField();
		JPasswordField pwField = new JPasswordField();
		JLabel username = new JLabel("Ano.");
		JLabel password = new JLabel("Password");
		JLabel mode = new JLabel("Log in as");
		JLabel cno = new JLabel("Cno.");
		JButton linkButton = new JButton("Link DB");
		JButton confirmButton = new JButton("Confirm");
		JRadioButton aMode = new JRadioButton("Admin");
		JRadioButton uMode = new JRadioButton("User");
		ButtonGroup btnGroup = new ButtonGroup();
		
		public LFrame() {	
			setWindows();
			EventsListener();	
		}
		public void EventsListener() {
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					if(isconn)
						try {
							conn.close();
						} catch (SQLException e1) {
							e1.printStackTrace();
							System.out.println("Fail to disconnect");
						}
					System.exit(0);
					//frame.dispose();
				}
			});
			linkButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(conn != null) {
						JOptionPane.showMessageDialog(frame, 
								"Already connected to database",
								"Warning", JOptionPane.INFORMATION_MESSAGE);
						confirmButton.setEnabled(true);
					}
					else {
						try {
					    	Class.forName(driverName);
							conn = DriverManager.getConnection(dbURL,Name,Pwd);
							if( conn == null) {
								JOptionPane.showMessageDialog(frame, 
										"Unable to link database!",
										"Error", JOptionPane.ERROR_MESSAGE);
							}
							else {
								JOptionPane.showMessageDialog(frame, 
									"Successful! Please login with your account",
									"Success", JOptionPane.INFORMATION_MESSAGE);
								isconn = true;
								confirmButton.setEnabled(true);
							}
						} catch (Exception e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(frame, 
									"Unable to link database!",
									"Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			confirmButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(uMode.isSelected()) {
						Lmode = 0;
						if(SQL_cnoCheck()) {
							curr_user = cnoField.getText();
							new Menu().new MFrame();
							frame.dispose();
						}
						else {
							JOptionPane.showMessageDialog(frame, 
									"No such user!",
									"Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					else if(aMode.isSelected()) {
						Lmode = 1;
						if(SQL_loginCheck()) {
							curr_admin = usernameField.getText();
							new Menu().new MFrame();
							frame.dispose();
						}
						else {
							JOptionPane.showMessageDialog(frame, 
									"Please enter a validated username or password!",
									"Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			uMode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(uMode.isSelected()) {
						Lmode = 0;
						usernameField.setEnabled(false);
						pwField.setEnabled(false);
						cnoField.setEditable(true);
					}
				}
			});
			aMode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(aMode.isSelected()) {
						Lmode = 1;
						usernameField.setEnabled(true);
						pwField.setEnabled(true);
						cnoField.setEditable(false);
					}
				}
			});
		}
		public void setWindows() {
			frame.setIconImage(new ImageIcon("icon/messages.png").getImage());
			frame.setLayout(new BorderLayout());
			frame.add(northPanel,"North");
			frame.add(centerPanel, "Center");
			frame.add(southPanel, "South");
			northPanel.setLayout(new GridLayout(1,3));
			northPanel.add(mode);
			northPanel.add(aMode);
			northPanel.add(uMode);
			btnGroup.add(aMode);
			btnGroup.add(uMode);
			uMode.setSelected(true);
			cno.setHorizontalAlignment(SwingConstants.CENTER);
			mode.setHorizontalAlignment(SwingConstants.CENTER);
			username.setHorizontalAlignment(SwingConstants.CENTER);
			password.setHorizontalAlignment(SwingConstants.CENTER);
			centerPanel.setLayout(new GridLayout(3, 2, 0, 1));
			centerPanel.add(cno);
			centerPanel.add(cnoField);
			centerPanel.add(username);
			centerPanel.add(usernameField);
			centerPanel.add(password);
			centerPanel.add(pwField);
			southPanel.setLayout(new GridLayout(1, 2));
			southPanel.add(linkButton);
			southPanel.add(confirmButton);
			if(!isconn) confirmButton.setEnabled(false);//无法按下
			usernameField.setEnabled(false);
			pwField.setEnabled(false);
			frame.setSize(280, 160);
			int screen_width = Toolkit.getDefaultToolkit().getScreenSize().width;
			int screen_height = Toolkit.getDefaultToolkit().getScreenSize().height;
			frame.setLocation((screen_width - frame.getWidth()) / 2,
					(screen_height - frame.getHeight()) / 2);
			frame.setVisible(true);
			frame.setResizable(false);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		public boolean SQL_loginCheck() {
			String username = usernameField.getText().trim();
			String sql = "select * from admin where ano = ?";
			@SuppressWarnings("deprecation")
			String pw = pwField.getText();
			try {
				PreparedStatement pStm = conn.prepareStatement(sql);
				pStm.setString(1, username);
				ResultSet rs = pStm.executeQuery();
				if(rs.next())
					return rs.getString("pw").equals(pw);
				else return false;
			} catch (SQLException e) {
				return false;
			}
		}
		public boolean SQL_cnoCheck() {
			String cno = cnoField.getText().trim();
			String sql = "select * from card where cno = ?";
			try {
				PreparedStatement pStm = conn.prepareStatement(sql);
				pStm.setString(1, cno);
				ResultSet rs = pStm.executeQuery();
				if(rs.next())
					return true;
				else return false;
			} catch (SQLException e) {
				return false;
			}
		}
	}
}