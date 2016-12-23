import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author 2016 GRZEGORZ PRZYTU£A ALL RIGHTS RESERVED 
 * - Client with my own protocol through TCP/IP - Ensures adding data to server
 */
public class LogClient extends JFrame
{
	private static final long serialVersionUID = 1L;

	/** Socket fields */
	private Socket connection;
	private ObjectOutputStream oOutputStream;
	private ObjectInputStream oInputStream;

	/** Server data */
	private InetAddress serverAdress;
	private JTextArea serverResponseTextArea;

	/** Identifier that will be send to server */
	private String machineId;

	/** CREATING NEW EVENT-LEDGER name + types and values + checkboxes */
	private JTextField tabNameTextField;
	private List<JComboBox<String>> creatingTypes;
	private List<JTextField> creatingValues;
	private List<JCheckBox> checkBoxes;

	/** Combobox with received table names */
	JComboBox<String> tablesComboBox;

	/** Handlers to send new record */
	private List<JTextField> receivedColumnNames;
	private List<JTextArea> sendingValues;
	private List<JScrollPane> asociatedWithSendingValuesScrolls;

	public LogClient()
	{
		this(null, null);
	}

	/**
	 * Create the frame.
	 */
	public LogClient(String machineId, InetAddress serverAdress)
	{
		this.machineId = machineId;
		this.serverAdress = serverAdress;
		setTitle("LogClient");
		if (machineId != null)
			setTitle("LogClient: " + machineId);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 765, 608);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel creatingLogPanel = new JPanel();
		creatingLogPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		creatingLogPanel.setToolTipText("");
		creatingLogPanel.setBounds(10, 11, 374, 456);
		contentPane.add(creatingLogPanel);
		creatingLogPanel.setLayout(null);

		JLabel titleLbl = new JLabel("CREATING NEW EVENT-LEDGER");
		titleLbl.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		titleLbl.setHorizontalAlignment(SwingConstants.CENTER);
		titleLbl.setEnabled(false);
		titleLbl.setBounds(10, 11, 354, 14);
		creatingLogPanel.add(titleLbl);

		JLabel columnTypeLbl = new JLabel("COLUMN TYPE");
		columnTypeLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		columnTypeLbl.setHorizontalAlignment(SwingConstants.CENTER);
		columnTypeLbl.setEnabled(false);
		columnTypeLbl.setBounds(10, 59, 120, 14);
		creatingLogPanel.add(columnTypeLbl);

		JLabel columnNameLbl = new JLabel("COLUMN NAME");
		columnNameLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		columnNameLbl.setHorizontalAlignment(SwingConstants.CENTER);
		columnNameLbl.setEnabled(false);
		columnNameLbl.setBounds(140, 59, 138, 14);
		creatingLogPanel.add(columnNameLbl);

		JTextField txtEventTime = new JTextField();
		txtEventTime.setText(ProtocolConsts.EVENT_TIME_NAME);
		txtEventTime.setEnabled(false);
		txtEventTime.setEditable(false);
		txtEventTime.setBounds(140, 84, 138, 20);
		creatingLogPanel.add(txtEventTime);

		JTextField txtMachineId = new JTextField();
		txtMachineId.setEnabled(false);
		txtMachineId.setEditable(false);
		txtMachineId.setText(ProtocolConsts.MACHINE_ID_NAME);
		txtMachineId.setColumns(10);
		txtMachineId.setBounds(140, 115, 138, 20);
		creatingLogPanel.add(txtMachineId);

		creatingValues = new ArrayList<>();
		JTextField columnNameTextField1 = new JTextField();
		columnNameTextField1.setVisible(false);
		columnNameTextField1.setColumns(10);
		columnNameTextField1.setBounds(140, 146, 138, 20);
		creatingLogPanel.add(columnNameTextField1);
		creatingValues.add(columnNameTextField1);

		JTextField columnNameTextField2 = new JTextField();
		columnNameTextField2.setVisible(false);
		columnNameTextField2.setColumns(10);
		columnNameTextField2.setBounds(140, 177, 138, 20);
		creatingLogPanel.add(columnNameTextField2);
		creatingValues.add(columnNameTextField2);

		JTextField columnNameTextField3 = new JTextField();
		columnNameTextField3.setVisible(false);
		columnNameTextField3.setColumns(10);
		columnNameTextField3.setBounds(140, 208, 138, 20);
		creatingLogPanel.add(columnNameTextField3);
		creatingValues.add(columnNameTextField3);

		JTextField columnNameTextField4 = new JTextField();
		columnNameTextField4.setVisible(false);
		columnNameTextField4.setColumns(10);
		columnNameTextField4.setBounds(140, 239, 138, 20);
		creatingLogPanel.add(columnNameTextField4);
		creatingValues.add(columnNameTextField4);

		JTextField columnNameTextField5 = new JTextField();
		columnNameTextField5.setVisible(false);
		columnNameTextField5.setColumns(10);
		columnNameTextField5.setBounds(140, 270, 138, 20);
		creatingLogPanel.add(columnNameTextField5);
		creatingValues.add(columnNameTextField5);

		JTextField columnNameTextField6 = new JTextField();
		columnNameTextField6.setVisible(false);
		columnNameTextField6.setColumns(10);
		columnNameTextField6.setBounds(140, 301, 138, 20);
		creatingLogPanel.add(columnNameTextField6);
		creatingValues.add(columnNameTextField6);

		JTextField columnNameTextField7 = new JTextField();
		columnNameTextField7.setVisible(false);
		columnNameTextField7.setColumns(10);
		columnNameTextField7.setBounds(140, 332, 138, 20);
		creatingLogPanel.add(columnNameTextField7);
		creatingValues.add(columnNameTextField7);

		JTextField columnNameTextField8 = new JTextField();
		columnNameTextField8.setVisible(false);
		columnNameTextField8.setColumns(10);
		columnNameTextField8.setBounds(140, 363, 138, 20);
		creatingLogPanel.add(columnNameTextField8);
		creatingValues.add(columnNameTextField8);

		JTextField columnNameTextField9 = new JTextField();
		columnNameTextField9.setVisible(false);
		columnNameTextField9.setColumns(10);
		columnNameTextField9.setBounds(140, 394, 138, 20);
		creatingLogPanel.add(columnNameTextField9);
		creatingValues.add(columnNameTextField9);

		JTextField columnNameTextField10 = new JTextField();
		columnNameTextField10.setVisible(false);
		columnNameTextField10.setColumns(10);
		columnNameTextField10.setBounds(140, 425, 138, 20);
		creatingLogPanel.add(columnNameTextField10);
		creatingValues.add(columnNameTextField10);

		JComboBox<String> comboBoxEventTime = new JComboBox<>();
		comboBoxEventTime.setEnabled(false);
		comboBoxEventTime
				.setModel(new DefaultComboBoxModel<String>(new String[] { ProtocolConsts.EVENT_TIME_TYPE }));
		comboBoxEventTime.setSelectedIndex(0);
		comboBoxEventTime.setBounds(10, 84, 120, 20);
		comboBoxEventTime.setVisible(false);
		creatingLogPanel.add(comboBoxEventTime);

		JComboBox<String> comboBoxMachineId = new JComboBox<>();
		comboBoxMachineId.setEnabled(false);
		comboBoxMachineId
				.setModel(new DefaultComboBoxModel<String>(new String[] { ProtocolConsts.MACHINE_ID_TYPE }));
		comboBoxMachineId.setSelectedIndex(0);
		comboBoxMachineId.setBounds(10, 115, 120, 20);
		comboBoxMachineId.setVisible(false);
		creatingLogPanel.add(comboBoxMachineId);

		creatingTypes = new ArrayList<>();
		JComboBox<String> comboBox1 = new JComboBox<String>();
		comboBox1.setEditable(true);
		comboBox1.setVisible(false);
		comboBox1.setModel(new DefaultComboBoxModel<String>(ValueType.getValues()));
		comboBox1.setSelectedIndex(1);
		comboBox1.setBounds(10, 146, 120, 20);
		creatingLogPanel.add(comboBox1);
		creatingTypes.add(comboBox1);

		JComboBox<String> comboBox2 = new JComboBox<String>();
		comboBox2.setEditable(true);
		comboBox2.setVisible(false);
		comboBox2.setModel(new DefaultComboBoxModel<String>(ValueType.getValues()));
		comboBox2.setSelectedIndex(1);
		comboBox2.setBounds(10, 177, 120, 20);
		creatingLogPanel.add(comboBox2);
		creatingTypes.add(comboBox2);

		JComboBox<String> comboBox3 = new JComboBox<String>();
		comboBox3.setEditable(true);
		comboBox3.setVisible(false);
		comboBox3.setModel(new DefaultComboBoxModel<String>(ValueType.getValues()));
		comboBox3.setSelectedIndex(1);
		comboBox3.setBounds(10, 208, 120, 20);
		creatingLogPanel.add(comboBox3);
		creatingTypes.add(comboBox3);

		JComboBox<String> comboBox4 = new JComboBox<String>();
		comboBox4.setEditable(true);
		comboBox4.setVisible(false);
		comboBox4.setModel(new DefaultComboBoxModel<String>(ValueType.getValues()));
		comboBox4.setSelectedIndex(1);
		comboBox4.setBounds(10, 239, 120, 20);
		creatingLogPanel.add(comboBox4);
		creatingTypes.add(comboBox4);

		JComboBox<String> comboBox5 = new JComboBox<String>();
		comboBox5.setEditable(true);
		comboBox5.setVisible(false);
		comboBox5.setModel(new DefaultComboBoxModel<String>(ValueType.getValues()));
		comboBox5.setSelectedIndex(1);
		comboBox5.setBounds(10, 270, 120, 20);
		creatingLogPanel.add(comboBox5);
		creatingTypes.add(comboBox5);

		JComboBox<String> comboBox6 = new JComboBox<String>();
		comboBox6.setEditable(true);
		comboBox6.setVisible(false);
		comboBox6.setModel(new DefaultComboBoxModel<String>(ValueType.getValues()));
		comboBox6.setSelectedIndex(1);
		comboBox6.setBounds(10, 301, 120, 20);
		creatingLogPanel.add(comboBox6);
		creatingTypes.add(comboBox6);

		JComboBox<String> comboBox7 = new JComboBox<String>();
		comboBox7.setEditable(true);
		comboBox7.setVisible(false);
		comboBox7.setModel(new DefaultComboBoxModel<String>(ValueType.getValues()));
		comboBox7.setSelectedIndex(1);
		comboBox7.setBounds(10, 332, 120, 20);
		creatingLogPanel.add(comboBox7);
		creatingTypes.add(comboBox7);

		JComboBox<String> comboBox8 = new JComboBox<String>();
		comboBox8.setEditable(true);
		comboBox8.setVisible(false);
		comboBox8.setModel(new DefaultComboBoxModel<String>(ValueType.getValues()));
		comboBox8.setSelectedIndex(1);
		comboBox8.setBounds(10, 363, 120, 20);
		creatingLogPanel.add(comboBox8);
		creatingTypes.add(comboBox8);

		JComboBox<String> comboBox9 = new JComboBox<String>();
		comboBox9.setEditable(true);
		comboBox9.setVisible(false);
		comboBox9.setModel(new DefaultComboBoxModel<String>(ValueType.getValues()));
		comboBox9.setSelectedIndex(1);
		comboBox9.setBounds(10, 394, 120, 20);
		creatingLogPanel.add(comboBox9);
		creatingTypes.add(comboBox9);

		JComboBox<String> comboBox10 = new JComboBox<String>();
		comboBox10.setEditable(true);
		comboBox10.setVisible(false);
		comboBox10.setModel(new DefaultComboBoxModel<String>(ValueType.getValues()));
		comboBox10.setSelectedIndex(1);
		comboBox10.setBounds(10, 425, 120, 20);
		creatingLogPanel.add(comboBox10);
		creatingTypes.add(comboBox10);

		checkBoxes = new ArrayList<>();
		JCheckBox checkBox1 = new JCheckBox("ADD");
		checkBox1.setFont(new Font("Tahoma", Font.PLAIN, 11));
		checkBox1.setBounds(284, 143, 80, 23);
		creatingLogPanel.add(checkBox1);
		checkBoxes.add(checkBox1);

		JCheckBox checkBox2 = new JCheckBox("ADD");
		checkBox2.setBounds(284, 174, 80, 23);
		checkBox2.setVisible(false);
		creatingLogPanel.add(checkBox2);
		checkBoxes.add(checkBox2);

		JCheckBox checkBox3 = new JCheckBox("ADD");
		checkBox3.setBounds(284, 205, 80, 23);
		checkBox3.setVisible(false);
		creatingLogPanel.add(checkBox3);
		checkBoxes.add(checkBox3);

		JCheckBox checkBox4 = new JCheckBox("ADD");
		checkBox4.setBounds(284, 236, 80, 23);
		checkBox4.setVisible(false);
		creatingLogPanel.add(checkBox4);
		checkBoxes.add(checkBox4);

		JCheckBox checkBox5 = new JCheckBox("ADD");
		checkBox5.setBounds(284, 267, 80, 23);
		checkBox5.setVisible(false);
		creatingLogPanel.add(checkBox5);
		checkBoxes.add(checkBox5);

		JCheckBox checkBox6 = new JCheckBox("ADD");
		checkBox6.setBounds(284, 298, 80, 23);
		checkBox6.setVisible(false);
		creatingLogPanel.add(checkBox6);
		checkBoxes.add(checkBox6);

		JCheckBox checkBox7 = new JCheckBox("ADD");
		checkBox7.setBounds(284, 329, 80, 23);
		checkBox7.setVisible(false);
		creatingLogPanel.add(checkBox7);
		checkBoxes.add(checkBox7);

		JCheckBox checkBox8 = new JCheckBox("ADD");
		checkBox8.setBounds(284, 360, 80, 23);
		checkBox8.setVisible(false);
		creatingLogPanel.add(checkBox8);
		checkBoxes.add(checkBox8);

		JCheckBox checkBox9 = new JCheckBox("ADD");
		checkBox9.setBounds(284, 391, 80, 23);
		checkBox9.setVisible(false);
		creatingLogPanel.add(checkBox9);
		checkBoxes.add(checkBox9);

		JCheckBox checkBox10 = new JCheckBox("ADD");
		checkBox10.setBounds(284, 422, 80, 23);
		checkBox10.setVisible(false);
		creatingLogPanel.add(checkBox10);
		checkBoxes.add(checkBox10);

		JLabel tabNameLbl = new JLabel("EVENT-LEDGER NAME");
		tabNameLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		tabNameLbl.setHorizontalAlignment(SwingConstants.CENTER);
		tabNameLbl.setEnabled(false);
		tabNameLbl.setBounds(10, 36, 120, 14);
		creatingLogPanel.add(tabNameLbl);

		tabNameTextField = new JTextField();
		tabNameTextField.setColumns(10);
		tabNameTextField.setBounds(168, 33, 110, 20);
		creatingLogPanel.add(tabNameTextField);

		JButton sendCreatingBtn = new JButton("SEND");
		sendCreatingBtn.setFont(new Font("Tahoma", Font.BOLD, 11));
		sendCreatingBtn.setForeground(Color.DARK_GRAY);
		sendCreatingBtn.setBounds(288, 29, 76, 29);
		creatingLogPanel.add(sendCreatingBtn);

		JLabel nextColumnLbl = new JLabel("NEXT COLUMN");
		nextColumnLbl.setHorizontalAlignment(SwingConstants.CENTER);
		nextColumnLbl.setFont(new Font("Tahoma", Font.BOLD, 11));
		nextColumnLbl.setEnabled(false);
		nextColumnLbl.setBounds(288, 112, 76, 20);
		creatingLogPanel.add(nextColumnLbl);

		JPanel logAddingPanel = new JPanel();
		logAddingPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		logAddingPanel.setBounds(394, 11, 347, 547);
		contentPane.add(logAddingPanel);
		logAddingPanel.setLayout(null);

		JLabel title2Lbl = new JLabel("SENDING NEW LOG RECORD");
		title2Lbl.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		title2Lbl.setHorizontalAlignment(SwingConstants.CENTER);
		title2Lbl.setEnabled(false);
		title2Lbl.setBounds(10, 11, 317, 14);
		logAddingPanel.add(title2Lbl);

		tablesComboBox = new JComboBox<String>();
		tablesComboBox.setBounds(10, 32, 238, 20);
		logAddingPanel.add(tablesComboBox);

		JButton refreshBtn = new JButton("REFRESH EVENT-LEDGERS LIST");
		refreshBtn.setForeground(Color.DARK_GRAY);
		refreshBtn.setFont(new Font("Tahoma", Font.BOLD, 11));
		refreshBtn.setBounds(10, 54, 237, 23);
		logAddingPanel.add(refreshBtn);

		JTextField infoTextFieldEventTime = new JTextField();
		infoTextFieldEventTime.setText(ProtocolConsts.EVENT_TIME_NAME);
		infoTextFieldEventTime.setEnabled(false);
		infoTextFieldEventTime.setEditable(false);
		infoTextFieldEventTime.setColumns(10);
		infoTextFieldEventTime.setBounds(10, 80, 157, 15);
		logAddingPanel.add(infoTextFieldEventTime);

		JTextField infoTextFieldReportingMachine = new JTextField();
		infoTextFieldReportingMachine.setText(ProtocolConsts.MACHINE_ID_NAME);
		infoTextFieldReportingMachine.setEnabled(false);
		infoTextFieldReportingMachine.setEditable(false);
		infoTextFieldReportingMachine.setColumns(10);
		infoTextFieldReportingMachine.setBounds(10, 99, 157, 15);
		logAddingPanel.add(infoTextFieldReportingMachine);

		receivedColumnNames = new ArrayList<>();
		JTextField infoTextField1 = new JTextField();
		infoTextField1.setEnabled(false);
		infoTextField1.setEditable(false);
		infoTextField1.setColumns(10);
		infoTextField1.setBounds(10, 117, 139, 20);
		logAddingPanel.add(infoTextField1);
		receivedColumnNames.add(infoTextField1);

		JTextField infoTextField2 = new JTextField();
		infoTextField2.setEnabled(false);
		infoTextField2.setEditable(false);
		infoTextField2.setColumns(10);
		infoTextField2.setBounds(10, 155, 139, 20);
		logAddingPanel.add(infoTextField2);
		receivedColumnNames.add(infoTextField2);

		JTextField infoTextField3 = new JTextField();
		infoTextField3.setEnabled(false);
		infoTextField3.setEditable(false);
		infoTextField3.setColumns(10);
		infoTextField3.setBounds(10, 196, 139, 20);
		logAddingPanel.add(infoTextField3);
		receivedColumnNames.add(infoTextField3);

		JTextField infoTextField4 = new JTextField();
		infoTextField4.setEnabled(false);
		infoTextField4.setEditable(false);
		infoTextField4.setColumns(10);
		infoTextField4.setBounds(10, 234, 139, 20);
		logAddingPanel.add(infoTextField4);
		receivedColumnNames.add(infoTextField4);

		JTextField infoTextField5 = new JTextField();
		infoTextField5.setEnabled(false);
		infoTextField5.setEditable(false);
		infoTextField5.setColumns(10);
		infoTextField5.setBounds(10, 275, 139, 20);
		logAddingPanel.add(infoTextField5);
		receivedColumnNames.add(infoTextField5);

		JTextField infoTextField6 = new JTextField();
		infoTextField6.setEnabled(false);
		infoTextField6.setEditable(false);
		infoTextField6.setColumns(10);
		infoTextField6.setBounds(10, 316, 139, 20);
		logAddingPanel.add(infoTextField6);
		receivedColumnNames.add(infoTextField6);

		JTextField infoTextField7 = new JTextField();
		infoTextField7.setEnabled(false);
		infoTextField7.setEditable(false);
		infoTextField7.setColumns(10);
		infoTextField7.setBounds(10, 357, 139, 20);
		logAddingPanel.add(infoTextField7);
		receivedColumnNames.add(infoTextField7);

		JTextField infoTextField8 = new JTextField();
		infoTextField8.setEnabled(false);
		infoTextField8.setEditable(false);
		infoTextField8.setColumns(10);
		infoTextField8.setBounds(10, 398, 139, 20);
		logAddingPanel.add(infoTextField8);
		receivedColumnNames.add(infoTextField8);

		JTextField infoTextField9 = new JTextField();
		infoTextField9.setEnabled(false);
		infoTextField9.setEditable(false);
		infoTextField9.setColumns(10);
		infoTextField9.setBounds(10, 439, 139, 20);
		logAddingPanel.add(infoTextField9);
		receivedColumnNames.add(infoTextField9);

		JTextField infoTextField10 = new JTextField();
		infoTextField10.setEnabled(false);
		infoTextField10.setEditable(false);
		infoTextField10.setColumns(10);
		infoTextField10.setBounds(10, 480, 139, 20);
		logAddingPanel.add(infoTextField10);
		receivedColumnNames.add(infoTextField10);
		receivedColumnNames.forEach(info -> info.setVisible(false));

		TimerApplet ta1 = new TimerApplet();
		ta1.setEnabled(false);
		ta1.setBounds(170, 80, 157, 15);
		logAddingPanel.add(ta1);
		ta1.init();
		ta1.start();

		JTextField ta2 = new JTextField();
		ta2.setHorizontalAlignment(SwingConstants.CENTER);
		ta2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		ta2.setDisabledTextColor(Color.BLACK);
		ta2.setEnabled(false);
		ta2.setEditable(false);
		if (machineId != null)
			ta2.setText(machineId);
		ta2.setBounds(170, 99, 157, 15);
		logAddingPanel.add(ta2);

		sendingValues = new ArrayList<>();
		asociatedWithSendingValuesScrolls = new ArrayList<>();
		JTextArea ta3 = new JTextArea();
		JScrollPane scrollPane3 = new JScrollPane(ta3);
		scrollPane3.setViewportBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		scrollPane3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane3.setBounds(159, 117, 168, 37);
		logAddingPanel.add(scrollPane3);
		sendingValues.add(ta3);
		asociatedWithSendingValuesScrolls.add(scrollPane3);

		JTextArea ta4 = new JTextArea();
		JScrollPane scrollPane4 = new JScrollPane(ta4);
		scrollPane4.setViewportBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		scrollPane4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane4.setBounds(159, 155, 168, 40);
		logAddingPanel.add(scrollPane4);
		sendingValues.add(ta4);
		asociatedWithSendingValuesScrolls.add(scrollPane4);

		JTextArea ta5 = new JTextArea();
		JScrollPane scrollPane5 = new JScrollPane(ta5);
		scrollPane5.setViewportBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		scrollPane5.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane5.setBounds(159, 196, 168, 37);
		logAddingPanel.add(scrollPane5);
		sendingValues.add(ta5);
		asociatedWithSendingValuesScrolls.add(scrollPane5);

		JTextArea ta6 = new JTextArea();
		JScrollPane scrollPane6 = new JScrollPane(ta6);
		scrollPane6.setViewportBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		scrollPane6.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane6.setBounds(159, 234, 168, 40);
		logAddingPanel.add(scrollPane6);
		sendingValues.add(ta6);
		asociatedWithSendingValuesScrolls.add(scrollPane6);

		JTextArea ta7 = new JTextArea();
		JScrollPane scrollPane7 = new JScrollPane(ta7);
		scrollPane7.setViewportBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		scrollPane7.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane7.setBounds(159, 275, 168, 40);
		logAddingPanel.add(scrollPane7);
		sendingValues.add(ta7);
		asociatedWithSendingValuesScrolls.add(scrollPane7);

		JTextArea ta8 = new JTextArea();
		JScrollPane scrollPane8 = new JScrollPane(ta8);
		scrollPane8.setViewportBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		scrollPane8.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane8.setBounds(159, 316, 168, 40);
		logAddingPanel.add(scrollPane8);
		sendingValues.add(ta8);
		asociatedWithSendingValuesScrolls.add(scrollPane8);

		JTextArea ta9 = new JTextArea();
		JScrollPane scrollPane9 = new JScrollPane(ta9);
		scrollPane9.setViewportBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		scrollPane9.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane9.setBounds(159, 357, 168, 40);
		logAddingPanel.add(scrollPane9);
		sendingValues.add(ta9);
		asociatedWithSendingValuesScrolls.add(scrollPane9);

		JTextArea ta10 = new JTextArea();
		JScrollPane scrollPane10 = new JScrollPane(ta10);
		scrollPane10.setViewportBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		scrollPane10.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane10.setBounds(159, 398, 168, 40);
		logAddingPanel.add(scrollPane10);
		sendingValues.add(ta10);
		asociatedWithSendingValuesScrolls.add(scrollPane10);

		JTextArea ta11 = new JTextArea();
		JScrollPane scrollPane11 = new JScrollPane(ta11);
		scrollPane11.setViewportBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		scrollPane11.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane11.setBounds(159, 439, 168, 40);
		logAddingPanel.add(scrollPane11);
		sendingValues.add(ta11);
		asociatedWithSendingValuesScrolls.add(scrollPane11);

		JTextArea ta12 = new JTextArea();
		JScrollPane scrollPane12 = new JScrollPane(ta12);
		scrollPane12.setViewportBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		scrollPane12.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane12.setBounds(159, 480, 168, 40);
		logAddingPanel.add(scrollPane12);
		sendingValues.add(ta12);
		asociatedWithSendingValuesScrolls.add(scrollPane12);

		asociatedWithSendingValuesScrolls.forEach(v -> v.setVisible(false));

		JButton sendRecordBtn = new JButton("SEND");
		sendRecordBtn.setFont(new Font("Tahoma", Font.BOLD, 11));
		sendRecordBtn.setForeground(Color.DARK_GRAY);
		sendRecordBtn.setBounds(258, 29, 76, 29);
		logAddingPanel.add(sendRecordBtn);

		serverResponseTextArea = new JTextArea();
		serverResponseTextArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
		serverResponseTextArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(serverResponseTextArea);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setViewportBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		scrollPane.setBounds(10, 494, 374, 64);
		contentPane.add(scrollPane);

		JLabel responseLbl = new JLabel("SERVER RESPONSE");
		responseLbl.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		responseLbl.setHorizontalAlignment(SwingConstants.CENTER);
		responseLbl.setEnabled(false);
		responseLbl.setBounds(20, 478, 354, 14);
		contentPane.add(responseLbl);

		/** LISTENERS */
		checkBox1.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent arg0)
			{
				showNextMetadataRow(1, checkBox1.isSelected());
			}
		});
		checkBox2.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent arg0)
			{
				showNextMetadataRow(2, checkBox2.isSelected());
			}
		});
		checkBox3.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent arg0)
			{
				showNextMetadataRow(3, checkBox3.isSelected());
			}
		});
		checkBox4.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent arg0)
			{
				showNextMetadataRow(4, checkBox4.isSelected());
			}
		});
		checkBox5.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent arg0)
			{
				showNextMetadataRow(5, checkBox5.isSelected());
			}
		});
		checkBox6.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent arg0)
			{
				showNextMetadataRow(6, checkBox6.isSelected());
			}
		});
		checkBox7.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent arg0)
			{
				showNextMetadataRow(7, checkBox7.isSelected());
			}
		});
		checkBox8.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent arg0)
			{
				showNextMetadataRow(8, checkBox8.isSelected());
			}
		});
		checkBox9.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent arg0)
			{
				showNextMetadataRow(9, checkBox9.isSelected());
			}
		});
		checkBox10.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent arg0)
			{
				showNextMetadataRow(10, checkBox10.isSelected());
			}
		});
		tablesComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if (tablesComboBox.getModel().getSize() > 0
							&& !((String) tablesComboBox.getSelectedItem()).contains("404"))
					{
						connectToServer();
						sendColumnRequest();
						receiveColumnsFromServer();
						closeConnection();
					}
				}
				catch (IOException ex)
				{
					JOptionPane.showMessageDialog(LogClient.this, "Error while requesting for columns.", "ERROR",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		sendCreatingBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					if (!tabNameTextField.getText().isEmpty())
					{
						connectToServer();
						sendTableMetaDataToAdd();
						receiveMessageFromServer();
						closeConnection();
					}
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(LogClient.this, "Error while requesting for ledger create.", "ERROR",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		refreshBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					connectToServer();
					sendTableNamesRequest();
					receiveTablesFromServer();
					closeConnection();
					if (tablesComboBox.getModel().getSize() > 0
							&& !((String) tablesComboBox.getSelectedItem()).contains("404"))
					{
						connectToServer();
						sendColumnRequest();
						receiveColumnsFromServer();
						closeConnection();
					}
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(LogClient.this, "Error while requesting for tables.", "ERROR",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		sendRecordBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{
					connectToServer();
					sendRecordToAddToServer();
					receiveMessageFromServer();
					closeConnection();
				}
				catch (IOException e)
				{
					JOptionPane.showMessageDialog(LogClient.this, "Error while sending record.", "ERROR",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		refreshBtn.doClick();
	}
	
	/** DYNAMIC SWING SHOWING COMPONENTS METHOD */
	protected void showNextMetadataRow(int idx, boolean show)
	{
		creatingTypes.get(idx - 1).setVisible(show);
		creatingValues.get(idx - 1).setVisible(show);
		if (idx < checkBoxes.size())
			if (show)
				checkBoxes.get(idx).setVisible(show);
			else
				for (JCheckBox cb : checkBoxes.subList(idx, checkBoxes.size()))
				{
					cb.setSelected(show);
					cb.setVisible(show);
				}
	}

	/** SENDING METHODS */
	protected void sendRecordToAddToServer() throws IOException
	{
		DateFormat dateFormat = new SimpleDateFormat(ProtocolConsts.DATE_FORMAT);
		Calendar cal = Calendar.getInstance();

		StringBuilder sb = new StringBuilder("<" + ProtocolConsts.ADD_RECORD_HEADER + ">");
		sb.append("<" + ProtocolConsts.TAB_NAME + "=" + tablesComboBox.getSelectedItem() + ">");
		sb.append("<" + ProtocolConsts.EVENT_TIME_NAME + "=" + dateFormat.format(cal.getTime()) + ">");
		sb.append("<" + ProtocolConsts.MACHINE_ID_NAME + "=" + machineId + ">");
		for (int i = 0; i < receivedColumnNames.stream().filter(n -> n.isVisible()).count(); i++)
		{
			sb.append("<")
					.append(receivedColumnNames.get(i).getText().substring(0,
							receivedColumnNames.get(i).getText().indexOf("(")))
					.append("=").append(sendingValues.get(i).getText()).append(">");
		}

		oOutputStream.writeObject(sb.toString());
	}
	protected void sendColumnRequest() throws IOException
	{
		oOutputStream.writeObject(new String(
				"<" + ProtocolConsts.GET_CLUMNS_WITH_META_HEADER + "><" + tablesComboBox.getSelectedItem() + ">"));
	}
	protected void sendTableNamesRequest() throws IOException
	{
		oOutputStream.writeObject(new String("<" + ProtocolConsts.GET_TABLES_HEADER + ">"));
	}
	private void sendTableMetaDataToAdd() throws IOException
	{
		StringBuilder msg = new StringBuilder("<" + ProtocolConsts.CREATE_TABLE_HEADER + ">");
		msg.append("<" + ProtocolConsts.TAB_NAME + "=" + tabNameTextField.getText() + ">");
		msg.append("<" + ProtocolConsts.EVENT_TIME_NAME + "=" + ProtocolConsts.EVENT_TIME_TYPE + ">");
		msg.append("<" + ProtocolConsts.MACHINE_ID_NAME + "=" + ProtocolConsts.MACHINE_ID_TYPE + ">");

		for (int i = 0; i < creatingValues.stream().filter(v -> !v.getText().isEmpty()).count(); i++)
		{
			msg.append("<" + creatingValues.get(i).getText() + "=" + creatingTypes.get(i).getSelectedItem()
					+ ">");
		}
		oOutputStream.writeObject(msg.toString());
	}

	
	/** RECEIVING METHODS */
	protected void receiveColumnsFromServer() throws IOException
	{
		String msg = getMessageFromServer();
		List<String> columns = Arrays.asList(msg.split("[<>]+"));
		msg = columns.get(1);
		columns = columns.subList(1, columns.size());
		asociatedWithSendingValuesScrolls.forEach(v -> v.setVisible(false));
		receivedColumnNames.forEach(ta ->
		{
			ta.setText("");
			ta.setVisible(false);
		});
		DateFormat dateFormat = new SimpleDateFormat(ProtocolConsts.DATE_FORMAT);
		Calendar cal = Calendar.getInstance();
		serverResponseTextArea.setText(
				serverResponseTextArea.getText() + msg + " #" + dateFormat.format(cal.getTime()) + "\n");
		if (columns.size() < 2)
			return;

		columns = columns.subList(1, columns.size());
		String column;
		String type;
		int counter = 0;
		for (String col : columns)
		{
			column = col.substring(0, col.indexOf("="));
			type = col.substring(col.indexOf("=") + 1, col.length());
			receivedColumnNames.get(counter).setText(column + "(" + type + ")");
			receivedColumnNames.get(counter).setVisible(true);
			asociatedWithSendingValuesScrolls.get(counter).setVisible(true);
			counter++;
		}
	}
	private void receiveMessageFromServer() throws IOException
	{
		String msg = getMessageFromServer();
		DateFormat dateFormat = new SimpleDateFormat(ProtocolConsts.DATE_FORMAT);
		Calendar cal = Calendar.getInstance();
		serverResponseTextArea.setText(
				serverResponseTextArea.getText() + msg + " #" + dateFormat.format(cal.getTime()) + "\n");
	}
	private void receiveTablesFromServer() throws IOException
	{
		String msg = getMessageFromServer();
		List<String> splitted = Arrays.asList(msg.split("[<>]+"));
		msg = splitted.get(1);
		String[] tables = splitted.get(2).split("[,]+");
		tablesComboBox.setModel(new DefaultComboBoxModel<String>(tables));
		DateFormat dateFormat = new SimpleDateFormat(ProtocolConsts.DATE_FORMAT);
		Calendar cal = Calendar.getInstance();
		serverResponseTextArea.setText(
				serverResponseTextArea.getText() + msg + " #" + dateFormat.format(cal.getTime()) + "\n");
	}
	private String getMessageFromServer() throws IOException
	{
		String msg = null;
		try
		{
			msg = (String) oInputStream.readObject();
		}
		catch (ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(LogClient.this, "Class not found", "ERROR",
					JOptionPane.WARNING_MESSAGE);
		}
		
		return msg;
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
}
