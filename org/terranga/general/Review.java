package org.terranga.general;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;

public class Review {
	//AUTO GENERATED
	private String id;
	private Date timestamp;
				
	//USER INPUTED
	private String reviewedBy;
	private String reviewed;
	private Text description;
	
	
	//DEFAULT CONSTRUCTOR
	public Review(){
		setId(randomString(8));
		setTimestamp(new Date());
		
		setReviewed("none");
		setReviewedBy("none");
		setDescription(new Text(""));
	}
	
	//CONSTRUCTOR FROM ENTITY
	public Review(Entity ent){
		setId(ent.getKey().getName());
		setTimestamp((Date)ent.getProperty("timestamp"));
		setReviewed((String)ent.getProperty("reviewed"));
		setReviewedBy((String)ent.getProperty("reviewedBy"));
		setDescription((Text)ent.getProperty("description"));
	}
	
	//ENTITY CONSTRUCTOR
	public Entity createEntityVersion(){
		Entity e = new Entity("Review", getId());
		e.setProperty("timestamp", getTimestamp());
		e.setProperty("reviewed", getReviewed());
		e.setProperty("reviewedBy", getReviewedBy());
		e.setProperty("description", getDescription());
		
		return e;
	}	
	
	//GET SUMMARY FOR JSON USE
	public Map<String, Object> getSummary(){
		Map<String, Object> summary = new HashMap<String, Object>();
		
		//AUTO GENERATED
		summary.put("id", getId());
		summary.put("timestamp", getTimestamp().toString());
		
		//REVIEWED ID
		if(getReviewed().equals("none"))
			summary.put("reviewed", "");
		else
			summary.put("reviewed", getReviewed());
		
		//CATEGORY TAG
		if(getReviewedBy().equals("none"))
			summary.put("reviewedBy", "");
		else
			summary.put("reviewedBy", getReviewedBy());
		
		//DESCRIPTION
		if(getDescription().getValue().equals("none"))
			summary.put("description", "");
		else
			summary.put("description", getDescription().getValue());
	
		return summary;
	}
	
	//UPDATE FROM JSON-SUMMARY
	public void update(JSONObject json) throws JSONException {
		if (json.has("reviewed")){
			String e = json.getString("reviewed");
			if (e.length() > 0)
				setReviewed(e);
		}

		if (json.has("reviewedBy")){
			String eb = json.getString("reviewedBy");
			if (eb.length() > 0)
				setReviewedBy(eb);
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

	public String getReviewedBy() {
		return reviewedBy;
	}

	public void setReviewedBy(String reviewedBy) {
		this.reviewedBy = reviewedBy;
	}

	public String getReviewed() {
		return reviewed;
	}

	public void setReviewed(String reviewed) {
		this.reviewed = reviewed;
	}

	public Text getDescription() {
		return description;
	}

	public void setDescription(Text description) {
		this.description = description;
	}
}
