package edu.eci.arem.server;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;



public class HttpServer {
		public HttpServer(){
			
		}
		 public int getPort() {
	    	 if (System.getenv("PORT") != null) {
	    		 return Integer.parseInt(System.getenv("PORT"));
	    	 }
	    	 return 36000; //returns default port if heroku-port isn't set
	    }

		 public void start() throws IOException {
			   int puerto=this.getPort();	
			   ServerSocket serverSocket = null;
			   try { 
			      serverSocket = new ServerSocket(puerto);
			   } catch (IOException e) {
			      System.err.println("Could not listen on port: 36000.");
			      System.exit(1);
			   }
			   while(true){
				   Socket clientSocket = null;
				   try {
				       System.out.println("Listo para recibir ...");
				       clientSocket = serverSocket.accept();
				   } catch (IOException e) {
				       System.err.println("Accept failed.");
				       System.exit(1);
				   }
				   processRequest(clientSocket);
				    clientSocket.close(); 
				  }
	}
		 private void processRequest(Socket clientSocket) throws IOException {
		        BufferedReader in = new BufferedReader(
		                new InputStreamReader(clientSocket.getInputStream()));
		        String inputLine;
		        Map<String, String> request = new HashMap<>();
		        boolean requestLineReady = false;
		        while ((inputLine = in.readLine()) != null) {
		        	 System.out.println("Recibo: " + inputLine);
		            if (!requestLineReady) {
		                request.put("requestLine", inputLine);
		                requestLineReady = true;
		            } else {
		                String[] entry = createEntry(inputLine);
		                if (entry.length > 1) {
		                    request.put(entry[0], entry[1]);
		                }
		            }
		            if (!in.ready()) {
		                break;
		            }
		        }
		        if(request.get("requestLine") != null) {
			        System.out.println("esto          "+request.get("requestLine"));
			        
			        Request req = new Request(request.get("requestLine"));
	
			        System.out.println("RequestLine: " + req);
	
			        createResponse(req, new PrintWriter(
			                clientSocket.getOutputStream(), true),clientSocket);
		        }
		        in.close();
		    }

		    private String[] createEntry(String rawEntry) {
		        return rawEntry.split(":");
		    }
		    
		    private String getFormat(Request req) {
		    	URI theuri = req.getTheuri();
		        String formato;
	        	if((theuri.getPath()).equals("/favicon.ico")) {
	        		formato = "ico";
	        	}
	        	else {
		        	String[] compoPath = (theuri.getPath()).split("\\.");
			        formato =  compoPath[1]; 
		        }
	        	return formato;
		    }

		    private void createResponse(Request req, PrintWriter out,Socket clientSocket) {
		        String outputLine = testResponse();
		        URI theuri = req.getTheuri();
		        if((theuri.getPath()).equals("/")) {
		        	getStaticResource("html","/index.html",out);
		        }
		        else if (theuri.getPath().contains("PNG") || theuri.getPath().contains("JPG") || theuri.getPath().contains("JPEG") || theuri.getPath().contains("JFIF") ) {
		        	getStaticImagen(theuri,clientSocket,out);
		               
		        } else {
		        	String formato = getFormat(req);
		        	getStaticResource(formato,theuri.getPath(), out);
		        }
		        out.close();
		    }

		    private String testResponse() {
		        String outputLine = "HTTP/1.1 200 OK\r\n"
		                + "Content-Type: text/html\r\n"
		                + "\r\n"
		                + "<!DOCTYPE html>\n"
		                + "<html>\n"
		                + "<head>\n"
		                + "<meta charset=\"UTF-8\">\n"
		                + "<title>Title of the document</title>\n"
		                + "</head>\n"
		                + "<body>\n"
		                + "<h1>Mi propio mensaje</h1>\n"
		                + "</body>\n"
		                + "</html>\n";
		        return outputLine;
		    }
		    private void notFound(PrintWriter out) {
		    	String Error="HTTP/1.1 404 Not Found \r\nContent-Type: text/html \r\n\r\n <!DOCTYPE html> "
						+"		<link href='//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css' rel='stylesheet' id='bootstrap-css'>"
						+"<script src='//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js'></script>"
						+"<script src='//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js'></script>"
						+"<div class='page-wrap d-flex flex-row align-items-center'>"
						+"    <div class='container'>"
						+"        <div class='row justify-content-center'>"
						+"            <div class='col-md-12 text-center'>"
						+"               <span class='display-1 d-block'>404</span>"
						+"                <div class='mb-4 lead'>The page you are looking for was not found.</div>"
						+"                <a href='https://arem-taller3.herokuapp.com/' class='btn btn-link'>Back to Home</a>"
						+"           </div>"
						+"       </div>"
						+"   </div>"
						+"</div>";
				out.print(Error);
		    	
		    }
		    private void getStaticImagen(URI theuri,Socket clientSocket,PrintWriter out) {
		    	File dirImg = new File("src/main/resources/public_html"+theuri.getPath());
        		BufferedImage image;
				try {
					image = ImageIO.read(dirImg);
					ByteArrayOutputStream ArrBytes = new ByteArrayOutputStream();
	                DataOutputStream writeimg = new DataOutputStream(clientSocket.getOutputStream());
	                ImageIO.write(image, "PNG", ArrBytes);
	                writeimg.writeBytes("HTTP/1.1 200 OK \r\n" + "Content-Type: image/png \r\n" + "\r\n");
	                writeimg.write(ArrBytes.toByteArray());
				} catch (IOException e) {
					notFound(out);
				}
		    }
		    private void getStaticResource(String type,String path, PrintWriter out) {
		    	System.out.println("Type RequestLine: " + type);
		        Path file = Paths.get("src/main/resources/public_html" + path);
		        try (InputStream in = Files.newInputStream(file);
		                BufferedReader reader
		                = new BufferedReader(new InputStreamReader(in))) {
		            String header = "HTTP/1.1 200 OK\r\n"
		                + "Content-Type: text/"+type+"\r\n"
		                + "\r\n";
		            out.println(header);
		            String line = null; 
		            while ((line = reader.readLine()) != null) {
		                out.println(line);
		                System.out.println(line);
		            }
		        } catch (IOException ex) {
		        	notFound(out);
		        }
		    }
		
		 

}
