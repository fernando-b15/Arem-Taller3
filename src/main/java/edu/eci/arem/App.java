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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.xml.ws.Response;

import org.omg.CORBA.portable.InputStream;

import com.mongodb.BasicDBObject;

import edu.eci.arem.Persistence.MongoConnection;
import edu.eci.arem.server.HttpServer;
import edu.eci.arem.server.Request;

/**
 * Hello world!
 *
 */
public class App {
	  private static MongoConnection mongo;
	  public static void main(String[] args) throws IOException {
		  HttpServer server=new HttpServer();
		  mongo=server.getMongoConnecion();
		  server.get("/hello", (req) -> Hello());
		  server.get("/home", (req) -> index());
		  server.get("/registro", (req) -> mensajes(req));
		  server.get("/save", (req) -> save(req));
		  server.start();
	  }
	  private static String save(Request req) {
		  	System.out.println("post          "+req.getValFromQuery("name"));
		  	mongo.add(req.getValFromQuery("name"),req.getValFromQuery("message"));
		    return  "HTTP/1.1 200 OK\r\n"
		        + "Content-Type: text/"+"html"+"\r\n"
		        + "\r\n"
		        +"<!DOCTYPE html>"
             + "<html>"
		     +"<head>"
		     +"<meta http-equiv='Content-Type' content='text/html; charset=utf-8'>"
             +"<meta http-equiv='refresh' content='2;URL=https://arem-taller3.herokuapp.com/Apps/registro'>"
             +"</head>"
             + "<body>"
             + "<h1>Message Saved Successfully</h1>"
             + "</body>"
             + "</html>";
 
	  }
	  private static String mensajes(Request req) {
		  String res="";
		  ArrayList<BasicDBObject> list= mongo.consult();
		  for(BasicDBObject d:list) {
			 
			   res+="<tr><td>"+(d.get("nombre")).toString()+"</td><td>"+(d.get("mensaje")).toString()+"</td></tr>";
		  }
		  System.out.println(res);
		  String header = "HTTP/1.1 200 OK\r\n"
					+ "Content-Type: text/"+"html"+"\r\n"
					+ "\r\n";
		  String view = "<!DOCTYPE html>"
	                 + "<html>"
	                 + "<body style=\"background-color:#32CD32;\">"
	                 + "<style>"
	                 + "table, th, td {"
	                 + "border: 1px solid black;"
	                 + "border-collapse: collapse;"
	                 + "}"
	                 + "</style>"
	                 +"<center>"
	                 + "<form name='loginForm' action='save'>"
	                 +"Name: <input type='text' name='name'/> <br/>"
	                 +"Message: <input type='message' name='message'/> <br/>"
	                 +"<input type='submit' value='submit' />"
	                 +"</form>"
	 				 + "<Table>"
	 				 + "<tr>"
	 			     + "<th>Name</th>"
	 			     + "<th>Messages</th>"
	 			  	 + "</tr>"
	 			  	 + res
	 			     + "</Table>"
	                 +"</center>"
	                 + "</body>"
	                 + "</html>";
	         return header+view;
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
