package myLib;

import java.sql.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.*; 
import javax.swing.*;
import java.io.*;

public class StockIn {
	@SuppressWarnings("serial")
	public class SIFrame extends JFrame {
		String defaultP = "file\\test.txt";
		boolean isFocus = false;
		JFrame frame = new JFrame("StockIn");
		JPanel northPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel southPanel = new JPanel();
		JLabel typeLabel = new JLabel("Stock-in Mode:");
		JLabel bnoLabel = new JLabel("Book No.");
		JLabel cateLabel = new JLabel("Category");
		JLabel titLabel = new JLabel("Title");
		JLabel preLabel = new JLabel("Press");
		JLabel yearLabel = new JLabel("Year");
		JLabel autLabel = new JLabel("Author");
		JLabel priLabel = new JLabel("Price");
		JLabel quaLabel = new JLabel("Quantity");
		JLabel pathLabel = new JLabel("Path");
		JTextField bnoField = new JTextField();
		JTextField cateField = new JTextField();
		JTextField titField = new JTextField();
		JTextField preField = new JTextField();
		JTextField yearField = new JTextField();
		JTextField autField = new JTextField();
		JTextField priField = new JTextField();
		JTextField quaField = new JTextField();
		JTextField pathField = new JTextField();
		JButton confirmButton=  new JButton("Confirm");
		JButton exitButton = new JButton("Exit");
		JRadioButton singButton = new JRadioButton("Single Mode");
		JRadioButton batButton = new JRadioButton("Batch Mode");
		ButtonGroup btnGroup = new ButtonGroup();
		
		public SIFrame() {
			setWindows();
			//EventsListener
			EventsListener();
		}
		public void setWindows() {
			frame.setIconImage(new ImageIcon("icon/messages.png").getImage());
			frame.add(northPanel, "North");
			frame.add(centerPanel, "Center");
			frame.add(southPanel, "South");
			northPanel.setLayout(new GridLayout(1,3));
			centerPanel.setLayout(new GridLayout(8,2));
			southPanel.setLayout(new GridLayout(2,2));
			//north
			btnGroup.add(singButton);
			btnGroup.add(batButton);
			northPanel.add(typeLabel);
			northPanel.add(singButton);
			northPanel.add(batButton);
			singButton.setSelected(true);
			if(!batButton.isSelected()) pathField.setEditable(false);
			//center
			centerPanel.add(bnoLabel);
			centerPanel.add(bnoField);
			centerPanel.add(cateLabel);
			centerPanel.add(cateField);
			centerPanel.add(titLabel);
			centerPanel.add(titField);
			centerPanel.add(preLabel);
			centerPanel.add(preField);
			centerPanel.add(yearLabel);
			centerPanel.add(yearField);
			centerPanel.add(autLabel);
			centerPanel.add(autField);
			centerPanel.add(priLabel);
			centerPanel.add(priField);
			centerPanel.add(quaLabel);
			centerPanel.add(quaField);
			//south
			southPanel.add(pathLabel);
			southPanel.add(pathField);
			southPanel.add(confirmButton);
			southPanel.add(exitButton);
			//label
			bnoLabel.setHorizontalAlignment(SwingConstants.CENTER);
			cateLabel.setHorizontalAlignment(SwingConstants.CENTER);
			titLabel.setHorizontalAlignment(SwingConstants.CENTER);
			preLabel.setHorizontalAlignment(SwingConstants.CENTER);
			yearLabel.setHorizontalAlignment(SwingConstants.CENTER);
			priLabel.setHorizontalAlignment(SwingConstants.CENTER);
			autLabel.setHorizontalAlignment(SwingConstants.CENTER);
			quaLabel.setHorizontalAlignment(SwingConstants.CENTER);
			typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
			pathLabel.setHorizontalAlignment(SwingConstants.CENTER);
			//frame
			frame.setSize(300, 300);
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
			exitButton.addActionListener(new ActionListener() {
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
						if(singButton.isSelected()) {
							if(!checkEmpty()) {
								String pri = priField.getText().trim();
								float price = (float)(Math.round(Float.parseFloat(pri)*100))/100;
								if(price > 99999.99)
									JOptionPane.showMessageDialog(frame, 
											"Price should be less than ￥99999.99.",
											"Error", JOptionPane.ERROR_MESSAGE);
								else if(SQL_check_bno()) {
									JOptionPane.showMessageDialog(frame, 
											"The book alraedy exists, add stock by 1.",
											"Warning", JOptionPane.INFORMATION_MESSAGE);
									SQL_addStock();
								}
								else {
									SQL_singInsert();
									bnoField.setText(null);
									cateField.setText(null);
									titField.setText(null);
									preField.setText(null);
									yearField.setText(null);
									autField.setText(null);
									priField.setText(null);
									quaField.setText(null);
								}
							}
							else {
								JOptionPane.showMessageDialog(frame, 
										"Any field cannot be empty!",
										"Error", JOptionPane.ERROR_MESSAGE);
							}
						}
						else if(batButton.isSelected()) {
							if(isFocus) {
								String path = pathField.getText().trim();
								if(!(path == null || path.equals(""))) {
									SQL_batInsert();
								}else {
									JOptionPane.showMessageDialog(frame, 
											"Path cannot be empty!",
											"Error", JOptionPane.ERROR_MESSAGE);
								}
							}else {
								SQL_batInsert();
							}
						}
					}
				}
			});
			singButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(singButton.isSelected()) {
						pathField.setEditable(false);
						pathField.setText("");;
						bnoField.setEditable(true);
						cateField.setEditable(true);
						autField.setEditable(true);
						preField.setEditable(true);
						priField.setEditable(true);
						titField.setEditable(true);
						quaField.setEditable(true);
						yearField.setEditable(true);
					}
				}
			});
			batButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(batButton.isSelected()) {
						pathField.setEditable(true);
						bnoField.setEditable(false);
						cateField.setEditable(false);
						autField.setEditable(false);
						preField.setEditable(false);
						priField.setEditable(false);
						titField.setEditable(false);
						quaField.setEditable(false);
						yearField.setEditable(false);
					}
				}
			});
			pathField.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e1) {
					isFocus = true;
					if(pathField.getText().equals(defaultP)) {
						pathField.setText("");
					}
				}
				public void focusLost(FocusEvent e2) {
					isFocus = false;
					if(singButton.isSelected()) {
						pathField.setText("");
					}
					else if("".equals(pathField.getText())) {
						pathField.setText(defaultP);
						pathField.setForeground(Color.gray);
					}
				}
			});
		}
		//不允许任何字段为空，目前是这样
		public boolean checkEmpty() {
			String bno = bnoField.getText().trim();
			String cate = cateField.getText().trim();
			String tit = titField.getText().trim();
			String pre = preField.getText().trim();
			String year = yearField.getText().trim();
			String aut = autField.getText().trim();
			String pri = priField.getText().trim();
			String qua = quaField.getText().trim();
			if(bno == null || cate == null || tit == null || pre == null ||
					 year == null || aut == null || pri == null || qua == null ||
					 bno.equals("") || cate.equals("") ||tit.equals("") ||pre.equals("") ||
					 year.equals("") ||aut.equals("") ||pri.equals("") ||qua.equals(""))
				return true;
			else return false;
		}
		public void SQL_singInsert() {
			String sql = "insert into book values(?,?,?,?,?,?,?,?,?)";
			try {
				PreparedStatement pStm = LogIn.conn.prepareStatement(sql);
				String bno = bnoField.getText().trim();
				String cate = cateField.getText().trim();
				String tit = titField.getText().trim();
				String pre = preField.getText().trim();
				int year = Integer.parseInt(yearField.getText().trim());
				String aut = autField.getText().trim();
				String pri = priField.getText().trim();
				float price = (float)(Math.round(Float.parseFloat(pri)*100))/100;
				int qua = Integer.parseInt(quaField.getText().trim());

				pStm.setString(1, bno);
				pStm.setString(2, cate);
				pStm.setString(3, tit);
				pStm.setString(4, pre);
				pStm.setInt(5, year);
				pStm.setString(6, aut);
				pStm.setFloat(7, price);
				pStm.setInt(8, qua);
				pStm.setInt(9, qua);
				pStm.executeUpdate();
				JOptionPane.showMessageDialog(frame, 
						"Insert successfully!",
						"", JOptionPane.INFORMATION_MESSAGE);
			} catch(SQLException e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, 
						"Operation failed",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		public boolean SQL_check_bno() {
			String bno = bnoField.getText().trim();
			String sql = "select * from book where bno = '" + bno + "'";
			try {
				Statement stm = LogIn.conn.createStatement();
				ResultSet rs = stm.executeQuery(sql);
				if(rs.next())	return true;
				else return false;
			}catch (SQLException e) {
				return false;
			}
		}
		//如果bno已经存在，默认输入的书名及其他信息无误，直接增加库存
		public void SQL_addStock() {
			String sql = "update book set stock = stock + 1, total = total + 1 where bno = ?";
			try {
				PreparedStatement pStm = LogIn.conn.prepareStatement(sql);
				String bno = bnoField.getText().trim();
				pStm.setString(1, bno);
				pStm.executeUpdate();
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//批量插入,错误检查较少，只为测试方便用,未处理多空格情况
		public void SQL_batInsert() {
			String filename = pathField.getText();
			BufferedReader reader = null;
			ArrayList<String> token = new ArrayList<String>() {{
				for(int i = 0;i < 8; i++) add("");
			}};
			String sql = "insert into book values(?,?,?,?,?,?,?,?,?)";
			
			try {
				File file = new File(filename);
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(file) , "GBK"));
				String line = null;
				PreparedStatement pStm = LogIn.conn.prepareStatement(sql);
				while((line = reader.readLine()) != null) {
					//读行
					StringTokenizer st = new StringTokenizer(line, "(,)");
					int i = 0;
					while(st.hasMoreElements()) {
						token.set(i++, st.nextToken());
					}
					//SQL操作
					LogIn.conn.setAutoCommit(false);
					int year = Integer.parseInt(token.get(4).trim());
					float price = (float)(Math.round(Float.parseFloat(token.get(6))*100))/100;
					int qua = Integer.parseInt(token.get(7).trim());
					pStm.setString(1, token.get(0));
					pStm.setString(2, token.get(1));
					pStm.setString(3, token.get(2));
					pStm.setString(4, token.get(3));
					pStm.setInt(5, year);
					pStm.setString(6, token.get(5));
					pStm.setFloat(7, price);
					pStm.setInt(8, qua);
					pStm.setInt(9, qua);
					pStm.addBatch();
				}
				pStm.executeBatch();
				LogIn.conn.commit();
				reader.close();
				pStm.close();
				LogIn.conn.setAutoCommit(true);
				JOptionPane.showMessageDialog(frame, 
						"Insert successfully!",
						"", JOptionPane.INFORMATION_MESSAGE);
			}catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(frame, 
						"Open file error!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}catch (SQLException e2) {
				e2.printStackTrace();
				try {
					LogIn.conn.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
