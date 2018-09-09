package myLib;

import java.sql.*;
import java.util.Vector;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.*; 
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class Query {
	@SuppressWarnings("serial")
	public class QFrame extends JFrame{
		JFrame frame = new JFrame("Book Query");
		JPanel nPanel = new JPanel();
		JPanel cPanel = new JPanel();
		JPanel sPanel = new JPanel();
		JButton confirmButton = new JButton("Confirm");
		JButton resumeButton = new JButton("Resume");
		JButton exitButton = new JButton("Exit");
		JTextField fromField = new JTextField();
		JTextField toField = new JTextField();
		JLabel sLabel = new JLabel("Sorted by");
		JLabel qLabel = new JLabel("Query with");
		JLabel oLabel = new JLabel("with order");
		JLabel fromLabel = new JLabel("from");
		JLabel toLabel = new JLabel("to");
		Vector<String> sV = new Vector<String>() {{
			add("title");add("bno");add("category");add("press");
			add("year");add("author");add("price");add("total");add("stock");
		}};
		Vector<String> qV = new Vector<String>() {{
			add("category");add("title");add("press");add("year");
			add("year span");add("author");add("price");add("price span");
		}};
		Vector<String> cNames = new Vector<String>() {{
			add("bno");add("category");add("title");add("press");
			add("year");add("author");add("price");add("total");add("stock");
		}};
		Vector<String> order = new Vector<String>() {{
			add("ASC");add("DESC");
		}};
		JComboBox<String> sBox = new JComboBox<String>(sV);
		JComboBox<String> qBox = new JComboBox<String>(qV);
		JComboBox<String> oBox = new JComboBox<String>(order);
		Vector<Vector<String>> tValues =  new Vector<>();
		JTable table = new JTable();
		JScrollPane scrollPane = new JScrollPane();
		DefaultTableModel defaultTableModel;
		
		public QFrame() {
			setWindows();
			//EventsListener
			EventsListener();
		}
		public void setWindows() {
			frame.setIconImage(new ImageIcon("icon/messages.png").getImage());
			frame.add(nPanel, "North");//table
			frame.add(cPanel, "Center");//query by
			frame.add(sPanel, "South");//sorted by + btn
			nPanel.setLayout(new BorderLayout());
			cPanel.setLayout(new GridLayout(1, 10));
			sPanel.setLayout(new GridLayout(1, 3));
			//north
			SQL_defaultInfo();//表格信息
			defaultTableModel = new DefaultTableModel(tValues, cNames) {
				public boolean isCellEditable(int row, int column) {
				    return false;
				   }
			};
			table.setModel(defaultTableModel);
			scrollPane.setViewportView(table);
			nPanel.setPreferredSize(new Dimension(815,520));
			nPanel.add(scrollPane);
			nPanel.setBorder(new TitledBorder("Book Info."));
			//center
			cPanel.add(qLabel);
			cPanel.add(qBox);
			qBox.setEnabled(true);
			cPanel.add(fromLabel);
			cPanel.add(fromField);
			cPanel.add(toLabel);
			cPanel.add(toField);
			cPanel.add(sLabel);
			cPanel.add(sBox);
			sBox.setEnabled(true);
			cPanel.add(oLabel);
			cPanel.add(oBox);
			//south
			sPanel.add(confirmButton);
			sPanel.add(resumeButton);
			sPanel.add(exitButton);
			//textField
			toField.setEditable(false);
			//label
			qLabel.setHorizontalAlignment(SwingConstants.CENTER);
			sLabel.setHorizontalAlignment(SwingConstants.CENTER);
			fromLabel.setHorizontalAlignment(SwingConstants.CENTER);
			toLabel.setHorizontalAlignment(SwingConstants.CENTER);
			oLabel.setHorizontalAlignment(SwingConstants.CENTER);
			fromLabel.setVisible(false);
			toLabel.setVisible(false);
			//fromLabel.setVisible(false);
			//frame
			frame.setSize(815, 600);
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
			resumeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SQL_defaultInfo();
					scrollPane.updateUI();
				}
			});
			confirmButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SQL_queryInfo();
					scrollPane.updateUI();
				}
			});
			qBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					String sItem = e.getItem().toString().trim();
					if(sItem.equals("year span") || sItem.equals("price span")) {
						fromLabel.setVisible(true);
						toLabel.setVisible(true);
						toField.setEditable(true);
					}
					else {
						fromLabel.setVisible(false);
						toLabel.setVisible(false);
						toField.setEditable(false);
					}
				}
			});
		}
		public void SQL_defaultInfo() {
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
		public void SQL_queryInfo() {
			String qItem = qBox.getSelectedItem().toString();
			if(qItem.equals("year span"))
				qItem = "year";
			else if(qItem.equals("price span"))
				qItem = "price";
			String sql = "select * from book where " 
					+ qItem + " between ? and ? order by "
					+ sBox.getSelectedItem().toString().trim() + " "
					+ oBox.getSelectedItem().toString().trim();
			tValues.clear();
			try {
				PreparedStatement pStm = LogIn.conn.prepareStatement(sql);
				//pStm.setString(1, qBox.getSelectedItem().toString());
				pStm.setString(1, fromField.getText().trim());
				if(!qBox.getSelectedItem().toString().equals("year span") &&
						!qBox.getSelectedItem().toString().equals("price span"))
					pStm.setString(2, fromField.getText().trim());
				else	pStm.setString(2, toField.getText().trim());
				//pStm.setString(4, "'"+sBox.getSelectedItem().toString().trim()+"'");
				
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
						"Book info retrieved failed!",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
