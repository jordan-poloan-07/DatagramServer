package com.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.server.ServerImpl.ServerImplCallBack;

public class Server implements ActionListener, ServerImplCallBack {

	private JFrame frame;
	private JTextField chatInput;
	private JTextArea chatField;

	private ServerImpl sImpl;

	public Server() throws SocketException {

		frame = new JFrame("Server");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 600);

		chatInput = new JTextField();
		chatField = new JTextArea();
		chatField.setEditable(false);

		frame.add(new JScrollPane(chatField));
		frame.add(chatInput, BorderLayout.SOUTH);

		chatInput.addActionListener(this);

		frame.setVisible(true);

		sImpl = new ServerImpl(this);
		sImpl.startServerImpl();

	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		
		String entered = evt.getActionCommand();
		chatInput.setText("");
		
		String deliveredMessage = "Server >> " + entered;
		
		sImpl.sendToAll(deliveredMessage);
		
		append(deliveredMessage);
	}

	@Override
	public void callback(String message) {
		append(message);
	}

	private void append(String message) {
		chatField.append(message + "\n");
	}

}