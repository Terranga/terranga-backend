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

public class Dream {
	//AUTO GENERATED
	private String id;
	private Date timestamp;		
	//USER INPUTED
	private String profileID;
	private String title;
	private Long fundraisingGoal;
	private Long fundraisingCurrent;
	private Long numContributers;
	private Text description;
	
	//DEFAULT CONSTRUCTOR
	public Dream(){
		setId(randomString(8));
		setTimestamp(new Date());
		setProfileID("none");
		setTitle("none");
		setFundraisingGoal(0L);
		setFundraisingCurrent(0L);
		setNumContributers(0L);
		setDescription(new Text(""));
	}
	
	//CONSTRUCTOR FROM ENTITY
	public Dream(Entity ent){
		setId(ent.getKey().getName());
		setTimestamp((Date)ent.getProperty("timestamp"));	
		setProfileID((String)ent.getProperty("profileID"));
		setTitle((String)ent.getProperty("title"));
		setFundraisingGoal((Long)ent.getProperty("fundraisingGoal"));
		setFundraisingCurrent((Long)ent.getProperty("fundraisingCurrent"));
		setNumContributers((Long)ent.getProperty("numContributers"));
		setDescription((Text)ent.getProperty("description"));
	}
	
	//ENTITY CONSTRUCTOR
	public Entity createEntityVersion(){
		Entity d = new Entity("Dream", getId());
		d.setProperty("timestamp", getTimestamp());
		d.setProperty("profileID", getProfileID());
		d.setProperty("title", getTitle());
		d.setProperty("fundraisingGoal", getFundraisingGoal());
		d.setProperty("fundraisingCurrent", getFundraisingCurrent());
		d.setProperty("numContributers", getNumContributers());
		d.setProperty("description", getDescription());
		return d;
	}
	
	
	//GET SUMMARY FOR JSON USE
	public Map<String, Object> getSummary(){
		Map<String, Object> summary = new HashMap<String, Object>();
		
		//AUTO GENERATED
		summary.put("id", getId());
		summary.put("timestamp", getTimestamp().toString());
		
		//PROFILE ID
		if(getProfileID().equals("none"))
			summary.put("profileID", "");
		else
			summary.put("profileID", getProfileID());
		//CATEGORY TAG
		if(getTitle().equals("none"))
			summary.put("title", "");
		else
			summary.put("title", getTitle());
		//DESCRIPTION
		if(getDescription().getValue().equals("none"))
			summary.put("description", "");
		else
			summary.put("description", getDescription().getValue());
		
		summary.put("fundraisingGoal", Long.toString(getFundraisingGoal()));
		summary.put("fundraisingCurrent", Long.toString(getFundraisingCurrent()));
		summary.put("numContributers", Long.toString(getNumContributers()));
	
		return summary;
	}
	
	
	//UPDATE FROM JSON-SUMMARY
	public void update(JSONObject json) throws JSONException {
		if (json.has("profileID")){
			String pID = json.getString("profileID");
			if (pID.length() > 0)
				setProfileID(pID);
		}

		if (json.has("title")){
			String t = json.getString("title");
			if (t.length() > 0)
				setTitle(t);
		}
		
		if (json.has("description"))
			setDescription(new Text(json.getString("description")));
		
		if (json.has("fundraisingGoal")){
			String fg = json.getString("fundraisingGoal");
			if (fg.length() > 0)
				setFundraisingGoal(Long.parseLong(json.getString("fundraisingGoal")));
		}
		
		if (json.has("fundraisingCurrent")){
			String fc = json.getString("fundraisingCurrent");
			if (fc.length() > 0)
				setFundraisingCurrent(Long.parseLong(json.getString("fundraisingCurrent")));
		}
		
		if (json.has("numContributers")){
			String nc = json.getString("numContributers");
			if (nc.length() > 0)
				setNumContributers(Long.parseLong(json.getString("numContributers")));
		}
		
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
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getProfileID() {
		return profileID;
	}

	public void setProfileID(String profileID) {
		this.profileID = profileID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Text getDescription() {
		return description;
	}

	public void setDescription(Text description) {
		this.description = description;
	}	
	
	public Long getFundraisingGoal() {
		return fundraisingGoal;
	}

	public void setFundraisingGoal(Long fundraisingGoal) {
		this.fundraisingGoal = fundraisingGoal;
	}

	public Long getFundraisingCurrent() {
		return fundraisingCurrent;
	}

	public void setFundraisingCurrent(Long fundraisingCurrent) {
		this.fundraisingCurrent = fundraisingCurrent;
	}

	public Long getNumContributers() {
		return numContributers;
	}

	public void setNumContributers(Long numContributers) {
		this.numContributers = numContributers;
	}
	
	
	//- - - - - - - - - - - - - - - - - - - - - - QUERIES - - - - - - - - - - - - - - - - - - - - - -//
	
		//EXECUTE QUERY
		private static ArrayList<Dream> executeQuery(DatastoreService datastore, Query q, int limit){
			PreparedQuery pq = datastore.prepare(q);
					
			FetchOptions options = (limit==0) ? FetchOptions.Builder.withDefaults() : FetchOptions.Builder.withLimit(limit);
			QueryResultList<Entity> results = pq.asQueryResultList(options); //THE REQUEST
			ArrayList<Dream> dreams = new ArrayList<Dream>();
			for(Entity e : results){
				dreams.add(new Dream(e));
			}
			return dreams;
		}
		
		//FETCH SINGLE Dream (ID)
		public static Dream fetchDream(DatastoreService datastore, String dreamID){
			Dream d = null;
			try {
				Entity ent = datastore.get(KeyFactory.createKey("Dream", dreamID));
				d = new Dream(ent);
			}
			catch(EntityNotFoundException e){
			}
			return d;
		}
		
		//FETCH ALL INSIGHTS
		public static ArrayList<Dream> fetchDreams(DatastoreService datastore, int limit){
			Query q = new Query("Dream").addSort("timestamp", Query.SortDirection.DESCENDING);
			ArrayList<Dream> dreams = executeQuery(datastore, q, limit);
			return dreams;
		}


		
}
