package client;

import util.Util;
import main.SocketListener;
import main.ConsoleListener;
import main.DownloadThread;

import client.ClientQueue;

import java.net.Socket;
import java.net.InetAddress;

import java.io.DataOutputStream;
import java.io.DataInputStream;

public class Client {

	private InetAddress HOSTNAME = null;
	private int PORT_NUMBER = 3000;
	private Socket SOCKET = null;
	private DataOutputStream CLIENT_OUT = null;
	private ClientQueue MESSAGES = null;


	public Client(String[] args){
		if(args.length <= 1){
			Util.printMsg("Optional arguments: -h=[hostname] -p=[port-number]");
		}

		// generate a default InetAddress to connect to
		parseHostname("localhost");
		parsePortNumber("3000");
		for(int i=1; i<args.length; i++){
			handleArgument(args[i]);
		}
		connectToServer();
		openInputFromTerminal();
		openInputFromServer();
		//openDownloadFromServer();
	}

	private void handleArgument(String arg){
		Util.printMsg("Command line argumnet: " + arg);

		if(arg.startsWith("-h")){
			String hostString = arg.substring(3, arg.length());
			parseHostname(hostString);
		}

		if(arg.startsWith("-p")){

			String port = arg.substring(3, arg.length());
			parsePortNumber(port);
		}
	}

	private void parseHostname(String host){
		if(host.equals("localhost")){
			try{
				this.HOSTNAME = InetAddress.getLocalHost();
			} catch (Exception e){
				Util.catchException("Could not find localhost", e);
			}
		} else {
			try{
				this.HOSTNAME = InetAddress.getByName(host);
			} catch (Exception e){
				Util.catchException("Could not parse hostname", e);
			}
		}
		//Util.printMsg("Set hostname to: " + this.HOSTNAME.getHostName());
	}

	private void parsePortNumber(String port){
		try{
			int portInt = Integer.parseInt(port);
			this.PORT_NUMBER = portInt;
			//Util.printMsg("Set port number to: " + this.PORT_NUMBER);
		} catch (Exception e) {
			Util.catchException("Can not parse port number", e);
		}
	}

	private void connectToServer(){
		try{
			this.SOCKET = new Socket(this.HOSTNAME, this.PORT_NUMBER);
		} catch (Exception e){
			Util.catchException("Could not open socket to server", e);
		}
	}

	private void openOutputToServer(){
		try{
			this.CLIENT_OUT = new DataOutputStream(this.SOCKET.getOutputStream());
		} catch (Exception e){
			Util.catchException("Could not open output stream to server", e);
		}
	}

	private void openInputFromTerminal(){
		try{
			ConsoleListener console = new ConsoleListener(System.in, this.SOCKET.getOutputStream());
			Thread consoleThread = new Thread(console);
			consoleThread.start();
		} catch (Exception e) {
			Util.catchException("Could not open output to server", e);
		}
	}

	private void openInputFromServer(){
		try{
			this.MESSAGES = new ClientQueue(this.SOCKET);
			SocketListener server = new SocketListener(this.SOCKET, this.MESSAGES);
			Thread serverThread = new Thread(server);
			serverThread.start();
		} catch (Exception e){
			Util.catchException("Can not open thread to listen to server", e);
		}
	}

	private void openDownloadFromServer(){
		try{
			DownloadThread download = new DownloadThread(this.SOCKET);
			Thread downloadThread = new Thread(download);
			downloadThread.start();
		} catch (Exception e){
			Util.catchException("Can not open download thread", e);
		}
	}
}
