package com.knam.namchat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class ClientGUI extends JFrame implements Runnable{

	private static final long serialVersionUID = 1L;

	private JPanel mcontentPane;

	// WindowBuilder variables
	private JTextField message;
	private JTextArea history;
	private DefaultCaret caret;

	private Client client;

	private Thread listen, run;

	// constructor
	public ClientGUI(String name, String address, int port) {
		setTitle("NamChat - Client");
		client = new Client(name, address, port);
		boolean connected = client.openConnection();
		if (!connected) {
			System.err.println("Connection failed!");
			console("Connection failed!");
		}
		createWindow();
		console("Connecting to " + address + ":" + port + "...");

		String connection = ("/c/" + name + "/e/");
		client.send(connection.getBytes());
		
		run = new Thread(this,"Running");
		run.start();

	}
	
	public void run(){
		listen();
	}

	// listen method
	public void listen() {
		listen = new Thread("Listen") {
			public void run() {
				while (true) {
					String message = client.receive();
					if (message.startsWith("/c/")) {
						
						int newID = Integer.parseInt(message.split("/c/|/e/")[1]);
						console(newID + " is the ID.");
						console("Successfully connected to NamChat!");
						client.setID(newID);
						
						
						
					} else {
						console(message);
					}
				}
			}
		};
		listen.start();
	}

	// send method
	public void send(String msg) {
		if (msg.equals("")) {
			return;
		}
		if (msg.startsWith("/dc/")) {
			client.send(msg.getBytes());
		}

		else {
			client.send(("/m/" + client.getName() + ": " + msg).getBytes());
			message.setText("");
		}
	}

	// console method
	public void console(String msg) {
		history.append(msg + "\n\r");
		history.setCaretPosition(history.getDocument().getLength());
	}

	// create window
	private void createWindow() {
		// Default look of program depends on OS
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(880, 550);
		setLocationRelativeTo(null);
		mcontentPane = new JPanel();
		mcontentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mcontentPane);

		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 28, 815, 30, 7 };
		gbl_contentPane.rowHeights = new int[] { 35, 475, 40 };
		gbl_contentPane.columnWeights = new double[] { 1.0, 1.0 };
		gbl_contentPane.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		mcontentPane.setLayout(gbl_contentPane);

		history = new JTextArea();
		history.setEditable(false);
		caret = (DefaultCaret) history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scroll = new JScrollPane(history);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.insets = new Insets(0, 5, 5, 5);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 0;
		scrollConstraints.gridy = 0;
		scrollConstraints.gridwidth = 3;
		scrollConstraints.gridheight = 2;
		mcontentPane.add(scroll, scrollConstraints);

		message = new JTextField();
		message.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					send(message.getText());
				}
			}
		});
		GridBagConstraints gbc_message = new GridBagConstraints();
		gbc_message.insets = new Insets(0, 0, 0, 5);
		gbc_message.fill = GridBagConstraints.HORIZONTAL;
		gbc_message.gridx = 0;
		gbc_message.gridy = 2;
		gbc_message.gridwidth = 2;
		mcontentPane.add(message, gbc_message);
		message.setColumns(10);

		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(message.getText());
			}
		});

		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		mcontentPane.add(btnSend, gbc_btnSend);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				String dc = "/dc/" + client.getID() + "/e/";
				send(dc);
			}
		});

		setVisible(true);
		message.requestFocusInWindow();
	}

}
