package org.terranga.general;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Text;

public class Endorsement {
	//AUTO GENERATED
	private String id;
	private Date timestamp;
				
	//USER INPUTED
	private String endorsed;
	private String endorsedBy;
	private Text description;
	
	
	//DEFAULT CONSTRUCTOR
	public Endorsement(){
		setId(randomString(8));
		setTimestamp(new Date());
		
		setEndorsed("none");
		setEndorsedBy("none");
		setDescription(new Text(""));
	}
	
	//CONSTRUCTOR FROM ENTITY
	public Endorsement(Entity ent){
		setId(ent.getKey().getName());
		setTimestamp((Date)ent.getProperty("timestamp"));
		setEndorsed((String)ent.getProperty("endorsed"));
		setEndorsedBy((String)ent.getProperty("endorsedBy"));
		setDescription((Text)ent.getProperty("description"));
	}
	
	//ENTITY CONSTRUCTOR
	public Entity createEntityVersion(){
		Entity e = new Entity("Insight", getId());
		e.setProperty("timestamp", getTimestamp());
		e.setProperty("endorsed", getEndorsed());
		e.setProperty("endorsedBy", getEndorsedBy());
		e.setProperty("description", getDescription());
		
		return e;
	}	
	
	//GET SUMMARY FOR JSON USE
	public Map<String, Object> getSummary(){
		Map<String, Object> summary = new HashMap<String, Object>();
		
		//AUTO GENERATED
		summary.put("id", getId());
		summary.put("timestamp", getTimestamp().toString());
		
		//PROFILE ID
		if(getEndorsed().equals("none"))
			summary.put("endorsed", "");
		else
			summary.put("endorsed", getEndorsed());
		//CATEGORY TAG
		if(getEndorsedBy().equals("none"))
			summary.put("endorsedBy", "");
		else
			summary.put("endorsedBy", getEndorsedBy());
		//DESCRIPTION
		if(getDescription().getValue().equals("none"))
			summary.put("description", "");
		else
			summary.put("description", getDescription().getValue());
	
		return summary;
	}
	
	//UPDATE FROM JSON-SUMMARY
	public void update(JSONObject json) throws JSONException {
		if (json.has("endorsed")){
			String e = json.getString("endorsed");
			if (e.length() > 0)
				setEndorsed(e);
		}

		if (json.has("endorsedBy")){
			String eb = json.getString("endorsedBy");
			if (eb.length() > 0)
				setEndorsedBy(eb);
		}
		
		if (json.has("description"))
			setDescription(new Text(json.getString("description")));
	}	
	
	//SAVE METHODS
	public void save(){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(createEntityVersion());
	}

	public void save(DatastoreService datastore){
		datastore.put(createEntityVersion());
	}	
	
	//RANDOM STRING GENERATOR
	public static String randomString(int length){
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replace("-", "");
		uuid = uuid.substring(0, length);
		return uuid;
	}
	
	//GET AND SET METHODS
	public String getEndorsed() {
		return endorsed;
	}


	public void setEndorsed(String endorsed) {
		this.endorsed = endorsed;
	}


	public Date getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getEndorsedBy() {
		return endorsedBy;
	}


	public void setEndorsedBy(String endorsedBy) {
		this.endorsedBy = endorsedBy;
	}


	public Text getDescription() {
		return description;
	}


	public void setDescription(Text description) {
		this.description = description;
	}
	
	
//- - - - - - - - - - - - - - - - - - - - - - QUERIES - - - - - - - - - - - - - - - - - - - - - -//
	
	//EXECUTE QUERY
	private static ArrayList<Endorsement> executeQuery(DatastoreService datastore, Query q, int limit){
		PreparedQuery pq = datastore.prepare(q);
					
		FetchOptions options = (limit==0) ? FetchOptions.Builder.withDefaults() : FetchOptions.Builder.withLimit(limit);
		QueryResultList<Entity> results = pq.asQueryResultList(options); //THE REQUEST
		ArrayList<Endorsement> endorsements = new ArrayList<Endorsement>();
		for(Entity e : results){
			endorsements.add(new Endorsement(e));
		}
		return endorsements;
	}
			
	//FETCH SINGLE Endorsement (ID)
	public static Endorsement fetchEndorsement(DatastoreService datastore, String endorsementID){
		Endorsement search = null;
		try {
			Entity ent = datastore.get(KeyFactory.createKey("Endorsement", endorsementID));
			search = new Endorsement(ent);
		}
		catch(EntityNotFoundException e){
		}
		return search;
	}
		
	

}
