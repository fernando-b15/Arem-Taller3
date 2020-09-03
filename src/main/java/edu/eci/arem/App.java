package edu.eci.arem;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.omg.CORBA.portable.InputStream;

import edu.eci.arem.server.HttpServer;

/**
 * Hello world!
 *
 */
public class App {
	  public static void main(String[] args) throws IOException {
		  HttpServer server=new HttpServer();
		  server.get("/hello", (req) -> Hello());
		  server.get("/home", (req) -> index());
		  server.start();
	  }
	  private static String Hello() {
	        return  "HTTP/1.1 200 OK\r\n"
			        + "Content-Type: text/"+"html"+"\r\n"
			        + "\r\n"
			        +"<!DOCTYPE html>"
	                + "<html>"
	                + "<body>"
	                + "<h1>Hello world</h1>"
	                + "</body>"
	                + "</html>";
	    }
	  private static String index() {
	        File file = new File("src/main/resources/public_html/index.html");
	        FileReader reader;
			try {
				reader = new FileReader(file);
				 BufferedReader buffer = new BufferedReader(reader);
				    String header = "HTTP/1.1 200 OK\r\n"
				+ "Content-Type: text/"+"html"+"\r\n"
				+ "\r\n";
				String line = null; 
				while ((line = buffer.readLine()) != null) {
				    System.out.println(line);
				    header+=line;
				}
				return header;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
	       
	  }

   
	    	
}
