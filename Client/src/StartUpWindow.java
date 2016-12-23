import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class StartUpWindow extends JFrame
{
	private static final long serialVersionUID = 1L;
	/**
	 * Variables needed to property app start
	 */	
	private JTextField machineIdTextField;
	private JTextField serverIpTextField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					StartUpWindow frame = new StartUpWindow();
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public StartUpWindow()
	{
		setTitle("Log Client");
		setType(Type.UTILITY);
		JPanel contentPane;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 223, 112);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(Color.LIGHT_GRAY);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		machineIdTextField = new JTextField();
		machineIdTextField.setFont(new Font("Tahoma", Font.BOLD, 11));
		machineIdTextField.setForeground(Color.WHITE);
		machineIdTextField.setBackground(Color.LIGHT_GRAY);
		machineIdTextField.setBounds(91, 0, 116, 20);
		machineIdTextField.setColumns(10);
		contentPane.add(machineIdTextField);
		
		JButton okBtn = new JButton("OK");	
		okBtn.setForeground(Color.LIGHT_GRAY);
		okBtn.setBackground(Color.DARK_GRAY);
		okBtn.setBounds(0, 42, 105, 31);
		contentPane.add(okBtn);
		
		JButton closeBtn = new JButton("CLOSE");	
		closeBtn.setForeground(Color.LIGHT_GRAY);
		closeBtn.setBackground(Color.DARK_GRAY);
		closeBtn.setBounds(102, 42, 105, 31);
		contentPane.add(closeBtn);
		
		JTextField txtMachineId = new JTextField();
		txtMachineId.setHorizontalAlignment(SwingConstants.RIGHT);
		txtMachineId.setEnabled(false);
		txtMachineId.setEditable(false);
		txtMachineId.setText("Machine ID:");
		txtMachineId.setForeground(Color.WHITE);
		txtMachineId.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtMachineId.setColumns(10);
		txtMachineId.setBackground(Color.GRAY);
		txtMachineId.setBounds(0, 0, 92, 20);
		contentPane.add(txtMachineId);
		
		serverIpTextField = new JTextField();
		serverIpTextField.setForeground(Color.WHITE);
		serverIpTextField.setFont(new Font("Tahoma", Font.BOLD, 11));
		serverIpTextField.setColumns(10);
		serverIpTextField.setBackground(Color.LIGHT_GRAY);
		serverIpTextField.setBounds(91, 21, 116, 20);
		contentPane.add(serverIpTextField);
		
		JTextField txtServerIp = new JTextField();
		txtServerIp.setText("Server IP:");
		txtServerIp.setHorizontalAlignment(SwingConstants.RIGHT);
		txtServerIp.setForeground(Color.WHITE);
		txtServerIp.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtServerIp.setEnabled(false);
		txtServerIp.setEditable(false);
		txtServerIp.setColumns(10);
		txtServerIp.setBackground(Color.GRAY);
		txtServerIp.setBounds(0, 21, 92, 20);
		contentPane.add(txtServerIp);
		
		/** LISTENERS */	
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				okActionPerformed();
			}
		});
		closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelActionPerformed();
			}
		});
		
		try
		{
			getServerIP();
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(StartUpWindow.this, "connection.properties file writing/reading failed.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	protected void getServerIP() throws IOException
	{
		File file = new File("connection.properties");
		if (!file.exists())
		{
			file.createNewFile();
			String defaultData = "#SERVER IP ADRESS\n" + "serverIp=127.0.0.1\n" +
						"#MACHINE ID THAT WILL BE SHOWN TO SERVER\n" +
						"machineIdentifier=DEFAULT";
			FileWriter fileWritter = new FileWriter(file.getName(), true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(defaultData);
			bufferWritter.close();
		}
		try (FileReader reader = new FileReader("connection.properties"))
		{
			Properties prop = new Properties();
			prop.load(reader);
			serverIpTextField.setText(prop.getProperty("serverIp"));
			machineIdTextField.setText(prop.getProperty("machineIdentifier"));
		}
	}

	private void okActionPerformed()
	{
		if(machineIdTextField.getText().isEmpty())
		{
			JOptionPane.showMessageDialog(StartUpWindow.this, "Machine identifier needed.", "NEEDED",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		try
		{
			LogClient frame = new LogClient(machineIdTextField.getText(),InetAddress.getByName(serverIpTextField.getText()));
			frame.setVisible(true);
		}
		catch (UnknownHostException e)
		{
			JOptionPane.showMessageDialog(StartUpWindow.this, "Unknown Host", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
		
		this.setVisible(false);	
		this.dispose();
	}
	
	private void cancelActionPerformed()
	{
		this.dispose();
	}
}
