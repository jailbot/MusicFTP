package util;

import java.io.BufferedReader;
import java.io.FileReader;

public class Util {

	public static void readTextFileAt(String path){
		try(BufferedReader in = new BufferedReader(new FileReader(path))){
			String line = in.readLine();
			while(line != null){
				System.out.println(line);
				line = in.readLine();
			}
		} catch (Exception e){
			printErr("Problem reading config file");
			e.printStackTrace();
		}
	}



	public static void catchException(Exception e){
		e.printStackTrace();
		System.exit(1);
	}
	public static void catchException(String m, Exception e){
		printErr("ERROR: " + m);
		e.printStackTrace();
		System.exit(1);
	}

	public static void printHelp(){
		printMsg("Usage: java MusicFTP [--client | --server]");
	}
	public static void printMsg(String msg){
		System.out.println(msg);
	}
	public static void printErr(String err){
		System.err.println(err);
	}
}
