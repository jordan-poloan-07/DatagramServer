package com.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServerImpl {

	interface ServerImplCallBack {
		void callback(String message);
	}

	private ServerImplCallBack serverGuiCallback;

	public ServerImpl(ServerImplCallBack serverGuiCallback) {
		this.serverGuiCallback = serverGuiCallback;
	}

	public void startServerImpl() {
		new MainReceiverThread().start();
	}

	public void sendToAll(String message) {

		try {

			DatagramSocket socket = new DatagramSocket(4445);

			byte[] buf = message.trim().getBytes();

			InetAddress group = InetAddress.getByName("230.0.0.1");
			DatagramPacket packet = new DatagramPacket(buf, buf.length, group,
					4446);

			socket.send(packet);
			socket.close();

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void receivePacket() {

		DatagramSocket datagramSocket = null;

		try {

			datagramSocket = new DatagramSocket(9770);

			byte[] buffer = new byte[60000];

			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			datagramSocket.receive(packet);

			new ServerReceiverThread(new String(buffer).trim()).start();

			datagramSocket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	class MainReceiverThread extends Thread {
		@Override
		public void run() {
			while (true) {
				receivePacket();
			}
		}

	}

	class ServerReceiverThread extends Thread {

		private String message;

		public ServerReceiverThread(String message) {
			this.message = message;
		}

		@Override
		public void run() {
			serverGuiCallback.callback(message);
			sendToAll(message);
		}

	}

}
