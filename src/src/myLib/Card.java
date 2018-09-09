package myLib;

import java.sql.*;
import java.util.Enumeration;
import java.util.Vector;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.*; 
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class Card {
	@SuppressWarnings("serial")
	public class CFrame extends JFrame{
		JFrame frame = new JFrame("Card Register");
		JPanel northPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel cnPanel = new JPanel();
		JPanel ccPanel = new JPanel();
		JPanel cnePanel = new JPanel();
		JPanel southPanel = new JPanel();
		JButton addButton = new JButton("Add");
		JButton exitButton = new JButton("Exit");
		JButton delButton = new JButton("Delete");
		JTextField cnoField = new JTextField();
		JTextField nameField = new JTextField();
		JTextField depField = new JTextField();
		JLabel cnoLabel = new JLabel("Card No.");
		JLabel nameLabel = new JLabel("Name");
		JLabel depLabel = new JLabel("Department");
		JLabel typeLabel = new JLabel("Card Type");
		JRadioButton tButton = new JRadioButton("T");
		JRadioButton gButton = new JRadioButton("G");
		JRadioButton uButton = new JRadioButton("U");
		JRadioButton oButton = new JRadioButton("O");
		ButtonGroup btnGroup = new ButtonGroup();
		Vector<String> cNames = new Vector<>();
		Vector<Vector<String>> tValues = new Vector<>();
		JTable table = new JTable();
		JScrollPane scrollPane = new JScrollPane();
		DefaultTableModel defaultTableModel;
		public CFrame() {
			setWindows();
			//EventsListener
			EventsListener();
		}
		public void setWindows() {
			frame.setIconImage(new ImageIcon("icon/messages.png").getImage());
			frame.add(northPanel, "North");
			frame.add(centerPanel, "Center");
			frame.add(southPanel, "South");
			northPanel.setLayout(new BorderLayout());
			centerPanel.add(cnPanel,"North");
			centerPanel.add(ccPanel,"Center");
			cnPanel.setLayout(new GridLayout(1,2));
			cnePanel.setLayout(new GridLayout(1,4));
			ccPanel.setLayout(new GridLayout(3,2,0,1));
			southPanel.setLayout(new GridLayout(1,3));
			//north
			cNames.add("cno");
			cNames.add("name");
			cNames.add("department");
			cNames.add("type");
			SQL_cardInfo();//构建表格
			defaultTableModel = new DefaultTableModel(tValues, cNames) {
				public boolean isCellEditable(int row, int column) {
				    return false;
				   }
			};
	        table.setModel(defaultTableModel);
			scrollPane.setViewportView(table);
			northPanel.add(scrollPane);
			northPanel.setBorder(new TitledBorder("Card Info."));
			northPanel.setPreferredSize(new Dimension(300,150));
			table.getColumnModel().getColumn(0).setPreferredWidth(30);
			table.getColumnModel().getColumn(1).setPreferredWidth(30);
			table.getColumnModel().getColumn(3).setPreferredWidth(30);
			//center north
			btnGroup.add(tButton);
			btnGroup.add(gButton);
			btnGroup.add(uButton);
			btnGroup.add(oButton);
			cnPanel.add(typeLabel);
			cnPanel.add(cnePanel);
			cnePanel.setPreferredSize(new Dimension(155,21));
			cnePanel.add(tButton);
			cnePanel.add(gButton);
			cnePanel.add(uButton);
			cnePanel.add(oButton);
			tButton.setSelected(true);
			//center center
			ccPanel.setPreferredSize(new Dimension(290,63));
			ccPanel.add(cnoLabel);
			ccPanel.add(cnoField);
			ccPanel.add(nameLabel);
			ccPanel.add(nameField);
			ccPanel.add(depLabel);
			ccPanel.add(depField);
			//south
			southPanel.add(addButton);
			southPanel.add(delButton);
			southPanel.add(exitButton);
			//label
			cnoLabel.setHorizontalAlignment(SwingConstants.CENTER);
			depLabel.setHorizontalAlignment(SwingConstants.CENTER);
			nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
			typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
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
			addButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SQL_insert();
					SQL_cardInfo();
					scrollPane.updateUI();
				}
			});
			delButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SQL_del();
					SQL_cardInfo();
					scrollPane.updateUI();
				}
			});
		}
		public void SQL_insert() {
			String sql = "insert into card values(?,?,?,?)";
			String cno = cnoField.getText().trim();
			String name = nameField.getText().trim();
			String dep = depField.getText().trim();
			String type = "";
			Enumeration<AbstractButton> rBtn = btnGroup.getElements();
			while(rBtn.hasMoreElements()) {
				AbstractButton r = rBtn.nextElement();
				if(r.isSelected()) {
					type = r.getText();
					break;
				}
			}
			if(cno == null || cno.equals("") 
					|| name == null || name.equals("") 
					|| dep == null || dep.equals("")) {
				JOptionPane.showMessageDialog(frame, 
						"Card no, Name and Department can't be empty!",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			else if(SQL_keyCheck()) {
				JOptionPane.showMessageDialog(frame, 
						"Card number already exits, choose another one!",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				PreparedStatement pStm = LogIn.conn.prepareStatement(sql);
				pStm.setString(1, cno);
				pStm.setString(2, name);
				pStm.setString(3, dep);
				pStm.setString(4, type);
				pStm.executeUpdate();
				JOptionPane.showMessageDialog(frame, 
						"Card created successfully",
						"Success", JOptionPane.INFORMATION_MESSAGE);
				cnoField.setText(null);
				nameField.setText(null);
				depField.setText(null);
			}catch(SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, 
						"Opraetion failed",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		public void SQL_del() {
			String sql = "delete from card where cno = ?";
			String cno = cnoField.getText().trim();
			if(cno == null || cno.equals("")) {
				JOptionPane.showMessageDialog(frame, 
						"Card no. can't be empty!",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			else if(!SQL_keyCheck()) {
				JOptionPane.showMessageDialog(frame, 
						"Card number not exist!",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				PreparedStatement pStm = LogIn.conn.prepareStatement(sql);
				pStm.setString(1, cno);
				pStm.executeUpdate();
				JOptionPane.showMessageDialog(frame, 
						"Card delete successfully",
						"Success", JOptionPane.INFORMATION_MESSAGE);
				cnoField.setText(null);
				nameField.setText(null);
				depField.setText(null);
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		public boolean SQL_keyCheck() {
			String cno = cnoField.getText().trim();
			String sql = "select * from card where cno = ?";
			try {
				PreparedStatement pStm = LogIn.conn.prepareStatement(sql);
				pStm.setString(1, cno);
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
		public void SQL_cardInfo() {
			String sql = "select * from card";
			tValues.clear();
			try {
				Statement stm = LogIn.conn.createStatement();
				ResultSet rs = stm.executeQuery(sql);
				while(rs.next()) {
					Vector<String> rv = new Vector<>();
					rv.add(rs.getString("cno"));
					rv.add(rs.getString("name"));
					rv.add(rs.getString("department"));
					rv.add(rs.getString("type"));
					tValues.add(rv);
				}
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
