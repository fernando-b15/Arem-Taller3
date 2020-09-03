package edu.eci.arem.Persistence;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;



public class MongoConnection {
	private DBCollection coleccion;
	private DB db; 
	public MongoConnection() throws UnknownHostException {
		MongoClientURI uri = new MongoClientURI("mongodb+srv://admin:protocolo15@basemongocluster.otant.mongodb.net/AREM?retryWrites=true&w=majority");
        MongoClient mongoClient = new MongoClient(uri);
        db= mongoClient.getDB("AREM");
        coleccion= db.getCollection("LAB");
        
	}
	public void add(String name,String mensaje) {
		BasicDBObject objeto= new BasicDBObject();
        objeto.put("nombre",name);
        objeto.put("mensaje",mensaje);
        coleccion.insert(objeto);
		
	}
	public ArrayList<BasicDBObject> consult() {
		ArrayList<BasicDBObject> registros = new ArrayList<BasicDBObject>();
		DBCursor mensajes = coleccion.find();
		while (mensajes.hasNext()){
			  
			  registros.add((BasicDBObject) mensajes.next());
		}
		return registros;
	}

}
