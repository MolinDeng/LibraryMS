package myLib;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;


public class Bor_Ret {
	@SuppressWarnings("serial")
	public class BRFrame extends JFrame{
		final long day = 86400000L;
		final long week = 604800000L;
		final long month = 2592000000L;
		long timespan = week;
		JFrame frame = new JFrame("Borrow&Return");
		JPanel nPanel = new JPanel();
		JPanel cPanel = new JPanel();
		JPanel sPanel = new JPanel();
		JButton bButton = new JButton("Borrow");
		JButton rButton = new JButton("Return");
		JButton eButton = new JButton("Exit");
		JButton qButton = new JButton("Query");
		JTextField bnoField = new JTextField();
		JTextField cnoField = new JTextField();
		JTextField timeField = new JTextField();
		JLabel bnoLabel = new JLabel("Book No.");
		JLabel cnoLabel = new JLabel("Card No.");
		JLabel timeLabel = new JLabel("Time");
		Vector<String> tV = new Vector<String>() {{
			add("day(s)");add("week(s)");add("month(s)(30 days)");
		}};
		JComboBox<String> tBox = new JComboBox<String>(tV);
		Vector<String> cNames = new Vector<String>() {{
			add("bno");add("category");add("title");add("press");
			add("year");add("author");add("price");add("total");add("stock");
		}};
		Vector<Vector<String>> tValues =  new Vector<>();
		JTable table = new JTable();
		JScrollPane scrollPane = new JScrollPane();
		DefaultTableModel defaultTableModel;
		
		public BRFrame() {
			setWindows();
			//EventsListener
			EventsListener();
		}
		public void setWindows() {
			frame.setIconImage(new ImageIcon("icon/messages.png").getImage());
			frame.add(nPanel, "North");//table
			frame.add(cPanel, "Center");//query by
			frame.add(sPanel, "South");//btn
			nPanel.setLayout(new BorderLayout());
			cPanel.setLayout(new GridLayout(1, 7));
			sPanel.setLayout(new GridLayout(1, 4));
			//north
			SQL_bookInfo();//表格信息
			defaultTableModel = new DefaultTableModel(tValues, cNames) {
				public boolean isCellEditable(int row, int column) {
				    return false;
				   }
			};
			table.setModel(defaultTableModel);
			scrollPane.setViewportView(table);
			nPanel.setPreferredSize(new Dimension(815,220));
			nPanel.add(scrollPane);
			nPanel.setBorder(new TitledBorder("Book Info."));
			//center
			cPanel.add(cnoLabel);
			cPanel.add(cnoField);
			cPanel.add(bnoLabel);
			cPanel.add(bnoField);
			cPanel.add(timeLabel);
			cPanel.add(timeField);
			cPanel.add(tBox);
			tBox.setEnabled(true);
			//south
			sPanel.add(qButton);
			sPanel.add(bButton);
			sPanel.add(rButton);
			sPanel.add(eButton);
			//label
			bnoLabel.setHorizontalAlignment(SwingConstants.CENTER);
			cnoLabel.setHorizontalAlignment(SwingConstants.CENTER);
			timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
			//field
			if(LogIn.Lmode == 0) {
				cnoField.setText(LogIn.curr_user);
				cnoField.setEditable(false);
			}
			//frame
			frame.setSize(815, 300);
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
			qButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SQL_borrowInfo();
					scrollPane.updateUI();
				}
			});
			eButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frame.dispose();
					new Menu().new MFrame().frame.setVisible(true);
				}
			});
			rButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SQL_return();
					scrollPane.updateUI();
				}
			});
			bButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SQL_borrow();
					scrollPane.updateUI();
				}
			});
		}
		public void SQL_borrowInfo() {
			String cno = cnoField.getText().trim();
			if(cno == null || cno.equals("")) {
				JOptionPane.showMessageDialog(frame, 
						"Card No. cannot be empty!",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			else if(!SQL_cnocheck()) {
				JOptionPane.showMessageDialog(frame, 
						"Card No. not exist!",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			else {
				String sql = "select book.* from book join borrow on book.bno = borrow.bno where cno = ?";
				//String sql = "select * from book where bno in (select bno from borrow where cno = ?)";
				tValues.clear();
				try {
					PreparedStatement pStm = LogIn.conn.prepareStatement(sql);
					pStm.setString(1, cno);
					ResultSet rs = pStm.executeQuery();
					while(rs.next()) {
						Vector<String> rv = new Vector<>();
						rv.add(rs.getString("bno"));
						rv.add(rs.getString("category"));
						rv.add(rs.getString("title"));
						rv.add(rs.getString("press"));
						rv.add(rs.getString("year"));
						rv.add(rs.getString("author"));
						rv.add(rs.getString("price"));
						rv.add(rs.getString("total"));
						rv.add(rs.getString("stock"));
						tValues.add(rv);
						//if(rs.getRow() > 50) break;
					}
				}catch(SQLException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(frame, 
							"Borrow info. retrieved failed!",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		public void SQL_bookInfo() {
			String sql = "select * from book order by title";
			tValues.clear();
			try {
				Statement stm = LogIn.conn.createStatement();
				ResultSet rs = stm.executeQuery(sql);
				while(rs.next()) {
					Vector<String> rv = new Vector<>();
					rv.add(rs.getString("bno"));
					rv.add(rs.getString("category"));
					rv.add(rs.getString("title"));
					rv.add(rs.getString("press"));
					rv.add(rs.getString("year"));
					rv.add(rs.getString("author"));
					rv.add(rs.getString("price"));
					rv.add(rs.getString("total"));
					rv.add(rs.getString("stock"));
					tValues.add(rv);
					//if(rs.getRow() > 50) break;
				}
			}catch(SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, 
						"Book info retrieved failed!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		public void SQL_borrow() {
			String cno = cnoField.getText().trim();
			String bno = bnoField.getText().trim();
			if(cno == null || bno == null
					|| cno.equals("") || bno.equals("")) {
				JOptionPane.showMessageDialog(frame, 
						"Card No. or Book No. cannot be empty!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
			else if(!SQL_cnocheck() || !SQL_bnocheck()) {
				JOptionPane.showMessageDialog(frame, 
						"Card or Book not exist!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
			else if(!SQL_hasStock()){
				String s = SQL_returnTime();
				JOptionPane.showMessageDialog(frame, 
						"Sorry, no Stock! The most recent return time is "+ s,
						"Failed", JOptionPane.INFORMATION_MESSAGE);
			}
			else if(SQL_isBorrow()) {
				JOptionPane.showMessageDialog(frame, 
						"You have borrowed this book!",
						"Failed", JOptionPane.ERROR_MESSAGE);
			}
			else {
				SQL_bTrans();
				String sql = "select book.* from book join borrow on book.bno = borrow.bno where cno = ?";
				//String sql = "select * from book where bno in (select bno from borrow where cno = ?)";
				tValues.clear();
				try {
					PreparedStatement pStm = LogIn.conn.prepareStatement(sql);
					pStm.setString(1, cno);
					ResultSet rs = pStm.executeQuery();
					while(rs.next()) {
						Vector<String> rv = new Vector<>();
						rv.add(rs.getString("bno"));
						rv.add(rs.getString("category"));
						rv.add(rs.getString("title"));
						rv.add(rs.getString("press"));
						rv.add(rs.getString("year"));
						rv.add(rs.getString("author"));
						rv.add(rs.getString("price"));
						rv.add(rs.getString("total"));
						rv.add(rs.getString("stock"));
						tValues.add(rv);
						//if(rs.getRow() > 50) break;
					}
				}catch(SQLException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(frame, 
							"Borrow info. retrieved failed!",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		public void SQL_return() {
			String cno = cnoField.getText().trim();
			String bno = bnoField.getText().trim();
			if(cno == null || bno == null
					|| cno.equals("") || bno.equals("")) {
				JOptionPane.showMessageDialog(frame, 
						"Card No. or Book No. cannot be empty!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
			else if(!SQL_cnocheck() || !SQL_bnocheck()) {
				JOptionPane.showMessageDialog(frame, 
						"Card or Book not exist!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
			else if(!SQL_isBorrow()) {
				JOptionPane.showMessageDialog(frame, 
						"You never borrow this book!",
						"Failed", JOptionPane.ERROR_MESSAGE);
			}
			else {
				SQL_rTrans();
				String sql = "select book.* from book join borrow on book.bno = borrow.bno where cno = ?";
				//String sql = "select * from book where bno in (select bno from borrow where cno = ?)";
				tValues.clear();
				try {
					LogIn.conn.setAutoCommit(true);
					PreparedStatement pStm = LogIn.conn.prepareStatement(sql);
					pStm.setString(1, cno);
					ResultSet rs = pStm.executeQuery();
					while(rs.next()) {
						Vector<String> rv = new Vector<>();
						rv.add(rs.getString("bno"));
						rv.add(rs.getString("category"));
						rv.add(rs.getString("title"));
						rv.add(rs.getString("press"));
						rv.add(rs.getString("year"));
						rv.add(rs.getString("author"));
						rv.add(rs.getString("price"));
						rv.add(rs.getString("total"));
						rv.add(rs.getString("stock"));
						tValues.add(rv);
						//if(rs.getRow() > 50) break;
					}
				}catch(SQLException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(frame, 
							"Borrow info. retrieved failed!",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		public boolean SQL_cnocheck() {
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
				return false;
			}
		}
		public boolean SQL_bnocheck() {
			String bno = bnoField.getText().trim();
			String sql = "select * from book where bno = ?";
			try {
				PreparedStatement pStm = LogIn.conn.prepareStatement(sql);
				pStm.setString(1, bno);
				ResultSet rs = pStm.executeQuery();
				if(rs.next())	return true;
				else return false;
			}catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, 
						"Checktime error",
						"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		public boolean SQL_hasStock() {
			String bno = bnoField.getText().trim();
			String sql = "select stock from book where bno = ?";
			try {
				PreparedStatement pStm = LogIn.conn.prepareStatement(sql);
				pStm.setString(1, bno);
				ResultSet rs = pStm.executeQuery();
				if(rs.next()) {
					return (rs.getInt("stock") > 0);
				}
				else return false;
			}catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, 
						"Check-stock-time error",
						"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		public boolean SQL_isBorrow() {
			String cno = cnoField.getText().trim();
			String bno = bnoField.getText().trim();
			String sql = "select bno from borrow where cno = ?";
			try {
				PreparedStatement pStm = LogIn.conn.prepareStatement(sql);
				pStm.setString(1, cno);
				ResultSet rs = pStm.executeQuery();
				while(rs.next()) {
					if(rs.getString("bno").trim().equals(bno)) {
						return true;
					}
				}
				return false;
			}catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, 
						"SQL_isBorrow error!",
						"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		public void SQL_bTrans() {
			if( timeField.getText().trim() != null 
					&& !timeField.getText().trim().equals("")) {
				int n = Integer.parseInt(timeField.getText().trim());
				if(tBox.getSelectedItem().toString().equals("day(s)"))
						timespan = n * day;
				else if(tBox.getSelectedItem().toString().equals("week(s)"))
						timespan = n * week;
				else 	timespan = n * month;
			}
			String bno = bnoField.getText().trim();
			String cno = cnoField.getText().trim();
			String sql1 = "update book set stock = stock - 1 where bno = ?";
			String sql2 = "insert into borrow values(?,?,?,?,?)";
			java.sql.Date fDate = java.sql.Date.valueOf(MyDate.dateTrans(System.currentTimeMillis()));
			java.sql.Date tDate = java.sql.Date.valueOf(MyDate.dateTrans(System.currentTimeMillis()+timespan));
			Savepoint savepoint1 = null;
			try {
				LogIn.conn.setAutoCommit(false);
				savepoint1 = LogIn.conn.setSavepoint("BEFORE");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			try {
				PreparedStatement pStm1 = LogIn.conn.prepareStatement(sql1);
				pStm1.setString(1, bno);
				pStm1.executeUpdate();
				LogIn.conn.commit();
				PreparedStatement pStm2 = LogIn.conn.prepareStatement(sql2);
				pStm2.setString(1, cno);
				pStm2.setString(2, bno);
				pStm2.setDate(3, fDate);
				pStm2.setDate(4, tDate);
				pStm2.setString(5, LogIn.curr_admin);
				pStm2.executeUpdate();
				LogIn.conn.commit();
				JOptionPane.showMessageDialog(frame, 
						"Successfully! Return time:" + MyDate.dateTrans(System.currentTimeMillis()+timespan),
						"", JOptionPane.INFORMATION_MESSAGE);
				LogIn.conn.setAutoCommit(true);
				bnoField.setText(null);
				timeField.setText(null);
			}catch (SQLException e2) {
				e2.printStackTrace();
				JOptionPane.showMessageDialog(frame, 
						"BorrowTransaction failed!",
						"Error", JOptionPane.ERROR_MESSAGE);
				try {
					LogIn.conn.rollback(savepoint1);
				} catch (SQLException e3) {
					e3.printStackTrace();
				}
			}
		}
		public String SQL_returnTime() {
			String bno = bnoField.getText().trim();
			String sql = "select distinct return_date from borrow where bno = ? order by return_date ASC";
			String now = MyDate.dateTrans(System.currentTimeMillis());
			Vector<String> s = new Vector<>();
			try {
				PreparedStatement pStm = LogIn.conn.prepareStatement(sql);
				pStm.setString(1, bno);
				ResultSet rs = pStm.executeQuery();
				while(rs.next()) {
					s.add(rs.getString("return_date"));
				}
				if(s.isEmpty()) return "None";
				else {
					Iterator<String> iter = s.iterator();
					String d = "None";
					while(iter.hasNext()) {
						d = iter.next();
						if(d.compareTo(now) >= 0) {
							break;
						}
						if(!iter.hasNext()) {
							d += "(expired)";
							break;
						}
					}
					return d;
				}
			}catch (SQLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, 
						"SQL_findReturnTime error!",
						"Error", JOptionPane.ERROR_MESSAGE);
				return "Error";
			}
		}
		public void SQL_rTrans() {
			String bno = bnoField.getText().trim();
			String cno = cnoField.getText().trim();
			String sql1 = "update book set stock = stock + 1 where bno = ?";
			String sql2 = "delete from borrow where bno = ? and cno = ?";
			Savepoint savepoint1 = null;
			try {
				LogIn.conn.setAutoCommit(false);
				savepoint1 = LogIn.conn.setSavepoint("BEFORE");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			try {
				PreparedStatement pStm1 = LogIn.conn.prepareStatement(sql1);
				pStm1.setString(1, bno);
				pStm1.executeUpdate();
				LogIn.conn.commit();
				PreparedStatement pStm2 = LogIn.conn.prepareStatement(sql2);
				pStm2.setString(1, bno);
				pStm2.setString(2, cno);
				pStm2.executeUpdate();
				LogIn.conn.commit();
				JOptionPane.showMessageDialog(frame, 
						"Return successfully!",
						"", JOptionPane.INFORMATION_MESSAGE);
				LogIn.conn.setAutoCommit(true);
			}catch (SQLException e2) {
				e2.printStackTrace();
				JOptionPane.showMessageDialog(frame, 
						"ReturnTransaction failed!",
						"Error", JOptionPane.ERROR_MESSAGE);
				try {
					LogIn.conn.rollback(savepoint1);
				} catch (SQLException e3) {
					e3.printStackTrace();
				}
			}
		}
	}
}
