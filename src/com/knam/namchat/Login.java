package com.knam.namchat;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel mcontentPane;
	private JTextField name;
	private JTextField address;
	private JTextField port;

	public Login() {

		// Default look of program depends on OS
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// WindowBuilder
		setResizable(false);
		setTitle("NamChat - Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 380);
		mcontentPane = new JPanel();
		mcontentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mcontentPane);
		mcontentPane.setLayout(null);

		name = new JTextField();
		name.setToolTipText("");
		name.setBounds(66, 123, 167, 25);
		mcontentPane.add(name);
		name.setColumns(10);

		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(115, 96, 70, 15);
		mcontentPane.add(lblName);

		address = new JTextField();
		address.setBounds(66, 181, 167, 25);
		mcontentPane.add(address);
		address.setColumns(10);

		JLabel lblIpAddress = new JLabel("IP Address:");
		lblIpAddress.setBounds(95, 154, 109, 15);
		mcontentPane.add(lblIpAddress);

		port = new JTextField();
		port.setColumns(10);
		port.setBounds(66, 237, 167, 25);
		mcontentPane.add(port);

		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(124, 218, 52, 15);
		mcontentPane.add(lblPort);

		// Login Button
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (name.getText().equals("") || address.getText().equals("")
						|| port.getText().equals("")) {
					
					JOptionPane.showMessageDialog(mcontentPane, "Please fill in each field.");

				} else {
					String username = name.getText();
					String useraddress = address.getText();
					int userport = Integer.parseInt(port.getText());

					login(username, useraddress, userport);
				}

			}
		});
		btnLogin.setBounds(87, 293, 117, 25);
		mcontentPane.add(btnLogin);
	}

	// Login method
	private void login(String name, String address, int port) {
		dispose();
		new ClientGUI(name, address, port);

	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
