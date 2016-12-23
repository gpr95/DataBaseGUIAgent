import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * @author 2016 GRZEGORZ PRZYTU£A ALL RIGHTS RESERVED 
 * - Client with my own protocol through TCP/IP  - ensures preview to server tables
 */
public class LogViewer extends JFrame
{
	private static final long serialVersionUID = 1L;


	/** SWING utilities */
	private JTabbedPane tabbedPane;
	private JTextArea serverLog;
	private JButton refreshBtn;
	
	
	/** Handlers */
	private List<String> tabNames;
	private List<TableRowSorter<TableModel>> sorters;
	private List<JTable> tabs;
	

	/** Socket fields */
	private InetAddress serverAdress;
	private Socket connection;
	private ObjectOutputStream oOutputStream;
	private ObjectInputStream oInputStream;

	
	public LogViewer()
	{
		this(null);
	}


	public LogViewer(InetAddress serverAdress)
	{
		this.serverAdress = serverAdress;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 731, 560);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 55, 695, 455);
		contentPane.add(tabbedPane);

		refreshBtn = new JButton("REFRESH");
		refreshBtn.setBounds(609, 11, 96, 39);
		contentPane.add(refreshBtn);

		serverLog = new JTextArea();
		serverLog.setFont(new Font("Monospaced", Font.PLAIN, 10));
		serverLog.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(serverLog);
		scrollPane.setViewportBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(10, 11, 482, 39);
		contentPane.add(scrollPane);
		
		JButton deleteRowBtn = new JButton("DELETE ROW");
		deleteRowBtn.setBounds(495, 11, 110, 17);
		contentPane.add(deleteRowBtn);
		
		JTextField filterTxtField = new JTextField();
		filterTxtField.setBounds(544, 28, 55, 17);
		contentPane.add(filterTxtField);
		filterTxtField.setColumns(10);
		
		JLabel filterLbl = new JLabel("FILTER:");
		filterLbl.setEnabled(false);
		filterLbl.setBounds(495, 28, 45, 14);
		contentPane.add(filterLbl);

		makeTableGettingConnection();
		
		/** LISTENERS */
		filterTxtField.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void changedUpdate(DocumentEvent arg0)
			{
					applyTextFieldFilter(filterTxtField.getText());
			}

			@Override
			public void insertUpdate(DocumentEvent arg0)
			{
					applyTextFieldFilter(filterTxtField.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent arg0)
			{
					applyTextFieldFilter(filterTxtField.getText());
			}});
		refreshBtn.addActionListener(new ActionListener()
		{
			private int selectedIdx;

			public void actionPerformed(ActionEvent arg0)
			{
				selectedIdx = tabbedPane.getSelectedIndex();
				makeTableGettingConnection();
				tabbedPane.setSelectedIndex(selectedIdx);
			}
		});
		deleteRowBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = tabs.get(tabbedPane.getSelectedIndex()).getSelectedRow();
				if(selectedRow != 0 && tabs.get(tabbedPane.getSelectedIndex()).getColumnName(0).equals("ID"))
					makeDeleteConnection(tabNames.get(tabbedPane.getSelectedIndex()),tabs.get(tabbedPane.getSelectedIndex()).getValueAt(selectedRow, 0).toString());
			}
		});
	}
	
	
	/** FILTERS FOR TABLE*/
	private void applyTableFieldFilter(JTable tab, TableRowSorter<TableModel> sorter)
	{
		if (tab.getSelectedRow() != 0 || tab.getValueAt(tab.getSelectedRow(), tab.getSelectedColumn()) == null)
			return;
	
		RowFilter<TableModel, Object> rf = null;
		try
		{
			rf = RowFilter.regexFilter(tab.getValueAt(tab.getSelectedRow(), tab.getSelectedColumn()).toString(), tab.getSelectedColumn());
		}
		catch (PatternSyntaxException e)
		{
			JOptionPane.showMessageDialog(LogViewer.this, "Filter failed", "WARNING",
					JOptionPane.WARNING_MESSAGE);
		}
		sorter.setRowFilter(rf);
	}
	private void applyTextFieldFilter(String filter)
	{
		RowFilter<TableModel, Object> rf = null;
		try
		{
			rf = RowFilter.regexFilter("(?i)" +filter);
		}
		catch (PatternSyntaxException e)
		{
			JOptionPane.showMessageDialog(LogViewer.this, "Filter failed", "WARNING",
					JOptionPane.WARNING_MESSAGE);
		}
		sorters.get(tabbedPane.getSelectedIndex()).setRowFilter(rf);
	}
	
	
	/** RECEIVING METHODS */
	private void receiveDeleteRequestAnswer() throws IOException
	{
		String msg = null;
		try
		{
			msg = (String) oInputStream.readObject();
		}
		catch (ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(LogViewer.this, "Class not found", "ERROR",
					JOptionPane.WARNING_MESSAGE);
		}
		DateFormat dateFormat = new SimpleDateFormat(ProtocolConsts.DATE_FORMAT);
		Calendar cal = Calendar.getInstance();
		serverLog.setText(serverLog.getText() + msg + " #" + dateFormat.format(cal.getTime()) + "\n");
	}
	private EditableJTable receiveAndSetColumnsFromServer() throws IOException
	{
		String msg = null;
		try
		{
			msg = (String) oInputStream.readObject();
		}
		catch (ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(LogViewer.this, "Class not found", "ERROR",
					JOptionPane.WARNING_MESSAGE);
		}
		List<String> splitted = Arrays.asList(msg.split("[<>]+"));
		msg = splitted.get(1);
		splitted = splitted.subList(2, splitted.size());
		if (splitted.isEmpty())
			return null;
		String[] columns = splitted.get(0).split("[,]+");
		splitted = splitted.subList(1, splitted.size());
		String[][] rows = new String[splitted.size() + 1][columns.length];
		rows[0] = new String[columns.length];
		for (int i = 0; i < splitted.size(); i++)
		{
			rows[i + 1] = splitted.get(i).split("[,]+");
		}
		DateFormat dateFormat = new SimpleDateFormat(ProtocolConsts.DATE_FORMAT);
		Calendar cal = Calendar.getInstance();
		serverLog.setText(serverLog.getText() + msg + " #" + dateFormat.format(cal.getTime()) + "\n");
		return new EditableJTable(rows, columns);
	}
	private void receiveAndSetTableNamesFromServer() throws IOException
	{
		String msg = null;
		try
		{
			msg = (String) oInputStream.readObject();
		}
		catch (ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(LogViewer.this, "Class not found", "ERROR",
					JOptionPane.WARNING_MESSAGE);
		}
		List<String> splitted = Arrays.asList(msg.split("[<>]+"));
		msg = splitted.get(1);
		String[] tables = splitted.get(2).split("[,]+");
		DateFormat dateFormat = new SimpleDateFormat(ProtocolConsts.DATE_FORMAT);
		Calendar cal = Calendar.getInstance();
		serverLog.setText(serverLog.getText() + msg + " #" + dateFormat.format(cal.getTime()) + "\n");
		tabbedPane.removeAll();
		sorters = new ArrayList<>();
		tabs = new ArrayList<>();
		tabNames = new ArrayList<>();
		for (int i = 0; i < tables.length; i++)
		{
			closeConnection();
			connectToServer();
			sendColumnRequest(tables[i]);
			JTable tab = receiveAndSetColumnsFromServer();
			TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tab.getModel());
			tab.setRowSorter(sorter);
			tab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tab.addKeyListener(new KeyListener()
			{
				@Override
				public void keyReleased(KeyEvent e)
				{
					applyTableFieldFilter(tab, sorter);
				}

				public void keyPressed(KeyEvent e)
				{
					applyTableFieldFilter(tab, sorter);
				}

				public void keyTyped(KeyEvent e)
				{
					applyTableFieldFilter(tab, sorter);
				}
			});
			JScrollPane jsp = new JScrollPane(tab);
			tabbedPane.addTab(tables[i], null, jsp, null);
			
			sorters.add(sorter);
			tabs.add(tab);
			tabNames.add(tables[i]);
		}
	}
	
	

	/** SENDING METHODS */
	private void sendDeleteRequest(String tabName, String id) throws IOException
	{
		oOutputStream.writeObject(new String("<" + ProtocolConsts.DELETE_HEADER+ ">" + "<" + tabName + ">"+ "<" + id + ">"));
		oOutputStream.flush();
	}
	protected void sendTableRequest() throws IOException
	{
		oOutputStream.writeObject(new String("<" + ProtocolConsts.GET_TABLES_HEADER + ">"));
		oOutputStream.flush();
	}
	protected void sendColumnRequest(String tabname) throws IOException
	{
		oOutputStream.writeObject(
				new String("<" + ProtocolConsts.GET_CLUMNS_AND_VALUES_HEADER + "><" + tabname + ">"));
		oOutputStream.flush();
	}
	
	/** CONNECTION METHODS */
	private void connectToServer() throws IOException
	{
		connection = new Socket(serverAdress, 6666);
		oOutputStream = new ObjectOutputStream(connection.getOutputStream());
		oOutputStream.flush();
		oInputStream = new ObjectInputStream(connection.getInputStream());
	}
	private void closeConnection() throws IOException
	{
		connection.close();
		oOutputStream.close();
		oInputStream.close();
	}
	protected void makeDeleteConnection(String tabName, String id)
	{
		try
		{
			connectToServer();
			sendDeleteRequest(tabName,id);
			receiveDeleteRequestAnswer();
			closeConnection();
			refreshBtn.doClick();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(LogViewer.this, "Error while requesting for delete record.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	private void makeTableGettingConnection()
	{
		try
		{
			connectToServer();
			sendTableRequest();
			receiveAndSetTableNamesFromServer();
			closeConnection();
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(LogViewer.this, "Error while requesting for tables.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	

	/** Class that provides filer row editable */
	class EditableJTable extends JTable
	{
		private static final long serialVersionUID = 1L;

		public EditableJTable(String[][] rows, String[] columns)
		{
			super(rows, columns);
		}

		@Override
		public boolean isCellEditable(int row, int column)
		{
			if (row == 0)
				return true;
			return false;
		}
	}
}
