package edu.eci.arem;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
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
		  server.start();
	  }
   
	    	
}
