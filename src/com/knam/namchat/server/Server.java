package com.knam.namchat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

public class Server implements Runnable {
	
	private ArrayList<ServerClient> clients = new ArrayList<ServerClient>();

	private DatagramSocket socket;
	private int port;
	private boolean running = false;

	private Thread run, manage, send, receive;

	// constructor
	public Server(int port) {
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		run = new Thread(this, "Server");
		run.start();
	}

	// main
	public void run() {
		running = true;
		System.out.println("Server started on port: " + port);
		manageClients();
		receive();
	}

	// checks clients
	private void manageClients() {
		manage = new Thread("Manage") {
			public void run() {
				while (running) {
					
				}
			}
		};
		manage.start();
	}

	// receives all data
	private void receive() {
		receive = new Thread("Receive") {
			public void run() {
				while (running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data,
							data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
				}
			}
		};
		receive.start();
	}
	
	private void sendToAll(String message){
		for(int i=0;i<clients.size();i++){
			ServerClient client = clients.get(i);
			send(message.getBytes(),client.ip,client.port);
		}
	}
	
	private void send(final byte[] data, final InetAddress address, final int port){
		send = new Thread("Send"){
			public void run(){
				DatagramPacket packet = new DatagramPacket(data,data.length,address,port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	
	private void process(DatagramPacket packet){
		String string = new String(packet.getData());
		
		//New Connection
		if(string.startsWith("/c/")){
			int id = UniqueID.getID();
			final InetAddress ip = packet.getAddress();
			final int port = packet.getPort();
			
			String newuser = string.split("/c/|/e/")[1];
			clients.add(new ServerClient(newuser, packet.getAddress(), packet.getPort(), id));
			
			System.out.println(newuser + " has connected.");
			System.out.println("ID = " + id + ",IP = " + packet.getAddress() + ":" + packet.getPort() );
			sendToAll(newuser + " has joined NamChat!");
			
			String giveID = "/c/" + id + "/e/";
			send(giveID.getBytes(), ip, port);
		
		//Message
		}else if(string.startsWith("/m/")) {
			sendToAll(string.substring(3,string.length()));
			System.out.println(string.substring(3,string.length()));
			
		//Disconnection
		}else if(string.startsWith("/dc/")){
			Iterator<ServerClient> sc = clients.iterator();
			while(sc.hasNext()){
				ServerClient temp = sc.next();
				if(temp.ID == Integer.parseInt(string.split("/dc/|/e/")[1])){
					String dcname = temp.name;
					sendToAll(dcname + " has left NamChat.");
					System.out.println(dcname + " has left the server");
					sc.remove();
				}
			}
			
		}else{
			System.out.println(string);
		}
	}

}
