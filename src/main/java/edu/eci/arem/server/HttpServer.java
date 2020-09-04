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
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.bson.Document;

import com.mongodb.BasicDBObject;

import edu.eci.arem.Persistence.MongoConnection;

/**
 * Esta clase representa el servidor http de la app definido con sockets
 *@author Fernando Barrera
 */

public class HttpServer {
		private Map<String,Function> rutas = new HashMap<String,Function>();
		private MongoConnection mongo;
		/**
		 * Este metodo es el metodo constructor de la palicacion que simplemente inicia la coneccion a la base de datos
		 * 
		 * @throws UnknownHostException
		 */
		public HttpServer() throws UnknownHostException{
			mongo = new MongoConnection();
		}
		/**
		 * Retorna el numero del puerto sobre el cual se esta ejecutando la aplicacion
		 * 
		 * @return int
		 */
		 public int getPort() {
	    	 if (System.getenv("PORT") != null) {
	    		 return Integer.parseInt(System.getenv("PORT"));
	    	 }
	    	 return 36000; //returns default port if heroku-port isn't set
	    }
		 /**
		  * Retorna la coneccion a la base de datos Mongo DB que esta conectada al servidor
		  * 
		  * @return MongoConnection
		  */
		 public  MongoConnection getMongoConnecion() {
			 return this.mongo;
		 }
		 /**
		  * Este metodo inicia el servidor
		  * 
		  * @throws IOException
		  */
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
		 /**
		  * Este metodo recibe un cliente socker para analizar las peticiones al servidor socket
		  * 
		  * @param clientSocket
		  * @throws IOException
		  */
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
		 	/**
		 	 * Este metodo simplemente se encarga de partir los datos de una peticion que viene en un solo string para facilitar su uso
		 	 * 
		 	 * @param rawEntry
		 	 * @return String[]
		 	 */
		    private String[] createEntry(String rawEntry) {
		        return rawEntry.split(":");
		    }
		    /**
		     * se encarga de retorna el formato del archivo solicitando en la peticion
		     * 
		     * @param req
		     * @return String
		     */
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
		    /**
		     * este metodo recibe una request ,un client socket y un printer para analizar la request y dependiendo si requiere un archivo o imagen o recurso dinameico
		     * con el printer se encarga de enviar la respuesta al servidor
		     * 
		     * @param req
		     * @param out
		     * @param clientSocket
		     */

		    private void createResponse(Request req, PrintWriter out,Socket clientSocket) {
		        String outputLine = testResponse();
		        URI theuri = req.getTheuri();
		        if((theuri.getPath()).equals("/")) {
		        	getStaticResource("html","/index.html",out);
		        }
		        else if(theuri.getPath().startsWith("/Apps") && !(theuri.getPath().contains(".js")) ) {
		        	try{
		        		String[] compoPath = (theuri.getPath()).split("/");
		        		System.out.println("llave "+compoPath[2]);
		        		out.print(rutas.get("/"+compoPath[2]).apply(req));
		        	}catch (Exception e) {
						notFound(out);
					}
		        }
		        else if (theuri.getPath().contains("PNG") || theuri.getPath().contains("JPG")  || theuri.getPath().contains("png") || theuri.getPath().contains("jpg") ) {
		        	String formato = getFormat(req);
		        	getStaticImagen(formato,theuri,clientSocket,out);
		               
		        } else {
		        	String formato = getFormat(req);
		        	getStaticResource(formato,theuri.getPath(), out);
		        }
		        out.close();
		    }
		    /**
		     * Metodo de plantilla de repuestas
		     * @return String
		     */
		    
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
		    /**
		     * Este metodo printea un error 404 en servidor en caso de que el recurso solicitado no sea encontardo 
		     * 
		     * @param out
		     */
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
		    /**
		     * Este metodo se encarga de recibir la  peticion,uri,socket de cliente , printer y el formato de la imagen para retornar una imagen al servidor
		     * 
		     * @param type
		     * @param theuri
		     * @param clientSocket
		     * @param out
		     */
		    private void getStaticImagen(String type,URI theuri,Socket clientSocket,PrintWriter out) {
		    	File dirImg = new File("src/main/resources/public_html"+theuri.getPath());
		    	System.out.println("Type iMAGEN xdxxxxxxxxx: " + type);
        		BufferedImage image;
				try {
					image = ImageIO.read(dirImg);
					ByteArrayOutputStream ArrBytes = new ByteArrayOutputStream();
	                DataOutputStream writeimg = new DataOutputStream(clientSocket.getOutputStream());
	                ImageIO.write(image, type, ArrBytes);
	                writeimg.writeBytes("HTTP/1.1 200 OK \r\n" + "Content-Type: image/"+type+" \r\n" + "\r\n");
	                writeimg.write(ArrBytes.toByteArray());
				} catch (IOException e) {
					notFound(out);
				}
		    }
		    
		    /**
		     * Se encarga de recibir el tipo del recurso ,el path del recurso y el printer para retornar el recuso solicitado
		     * 
		     * @param type
		     * @param path
		     * @param out
		     */
		    private void getStaticResource(String type,String path, PrintWriter out) {
		    	System.out.println("Type RequestLine: " + type+path);
		    	if(path.contains("/Apps")) {
		    		path=path.replace("/Apps","");
		    	}
		        Path file = Paths.get("src/main/resources/public_html" + path);
		        try (InputStream in = Files.newInputStream(file);
		                BufferedReader reader
		                = new BufferedReader(new InputStreamReader(in))) {
		            String header = "HTTP/1.1 200 OK\r\n"
		                + "Content-Type: text/"+"html"+"\r\n"
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
		    /**
		     * Se encarga de almacenar la relacion de los endpoints a las funciones asignadas por las funciones lambda
		     * 
		     * @param endPoint
		     * @param res
		     */
			public void get(String endPoint,Function<Request,String>  res) {
				System.out.println("ruta "+endPoint);
				System.out.println("val "+res);
				rutas.put(endPoint , res);
			}
		
		 

}
