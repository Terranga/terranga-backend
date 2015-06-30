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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class Insight {
	//AUTO GENERATED
	private String id;
	private Date timestamp;
			
	//USER INPUTED
	private String profileID;
	private String categoryTag;
	private Text description;
	private Text longDescription;

	
	//DEFAULT CONSTRUCTOR
	public Insight(){
		setId(randomString(8));
		setTimestamp(new Date());
		
		setProfileID("none");
		setCategoryTag("none");
		setDescription(new Text(""));
		setLongDescription(new Text(""));
	}
	
	//CONSTRUCTOR FROM ENTITY
	public Insight(Entity ent){
		setId(ent.getKey().getName());
		setTimestamp((Date)ent.getProperty("timestamp"));
		
		setProfileID((String)ent.getProperty("profileID"));
		setCategoryTag((String)ent.getProperty("categoryTag"));
		setDescription((Text)ent.getProperty("description"));
		setLongDescription((Text)ent.getProperty("longDescription"));

	}
	
	//ENTITY CONSTRUCTOR
	public Entity createEntityVersion(){
		Entity i = new Entity("Insight", getId());
		i.setProperty("timestamp", getTimestamp());
		
		i.setProperty("profileID", getProfileID());
		i.setProperty("categoryTag", getCategoryTag());
		i.setProperty("description", getDescription());
		i.setProperty("longDescription", getLongDescription());
		
		
		return i;
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
		if(getCategoryTag().equals("none"))
			summary.put("categoryTag", "");
		else
			summary.put("categoryTag", getCategoryTag());
		//DESCRIPTION
		if(getDescription().getValue().equals("none"))
			summary.put("description", "");
		else
			summary.put("description", getDescription().getValue());
		
		if(getLongDescription().getValue().equals("none"))
			summary.put("longDescription", "");
		else
			summary.put("longDescription", getLongDescription().getValue());
	
		return summary;
	}
	
	//UPDATE FROM JSON-SUMMARY
	public void update(JSONObject json) throws JSONException {
		if (json.has("profile")){
			String pID = json.getString("profile");
			if (pID.length() > 0)
				setProfileID(pID);
		}

		if (json.has("category")){
			String ct = json.getString("category");
			if (ct.length() > 0)
				setCategoryTag(ct);
		}
		
		if (json.has("description"))
			setDescription(new Text(json.getString("description")));
		
		if (json.has("longDescription"))
			setLongDescription(new Text(json.getString("longDescription")));
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
	
	//GETTERS AND SETTERS
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

	public String getCategoryTag() {
		return categoryTag;
	}

	public void setCategoryTag(String categoryTag) {
		this.categoryTag = categoryTag;
	}

	public Text getDescription() {
		return description;
	}

	public void setDescription(Text description) {
		this.description = description;
	}
	
	public Text getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(Text longDescription) {
		this.longDescription = longDescription;
	}


//- - - - - - - - - - - - - - - - - - - - - - QUERIES - - - - - - - - - - - - - - - - - - - - - -//
	
	//EXECUTE QUERY
	private static ArrayList<Insight> executeQuery(DatastoreService datastore, Query q, int limit){
		PreparedQuery pq = datastore.prepare(q);
				
		FetchOptions options = (limit==0) ? FetchOptions.Builder.withDefaults() : FetchOptions.Builder.withLimit(limit);
		QueryResultList<Entity> results = pq.asQueryResultList(options); //THE REQUEST
		ArrayList<Insight> insights = new ArrayList<Insight>();
		for(Entity e : results){
			insights.add(new Insight(e));
		}
		return insights;
	}
	
	//FETCH SINGLE INSIGHT (ID)
	public static Insight fetchInsight(DatastoreService datastore, String insightID){
		Insight i = null;
		try {
			Entity ent = datastore.get(KeyFactory.createKey("Insight", insightID));
			i = new Insight(ent);
		}
		catch(EntityNotFoundException e){
		}
		return i;
	}
	
	//FETCH ALL INSIGHTS
	public static ArrayList<Insight> fetchInsights(DatastoreService datastore, int limit){
		Query q = new Query("Insight").addSort("timestamp", Query.SortDirection.DESCENDING);
		ArrayList<Insight> insights = executeQuery(datastore, q, limit);
		return insights;
	}
	
	//FETCH INSIGHTS (profileID)
	public static ArrayList<Insight> fetchInsightsWithProfileID(DatastoreService datastore, String profileID,  int limit){
		Query q = new Query("Insight").addSort("timestamp", Query.SortDirection.DESCENDING);
		
		Filter profileIDFilter = new FilterPredicate("profileID", FilterOperator.EQUAL, profileID);
		q.setFilter(profileIDFilter);
		
		ArrayList<Insight> insights = executeQuery(datastore, q, limit);
		return insights;
	}
	
	//FETCH INSIGHTS (categoryTag)
	public static ArrayList<Insight> fetchInsightsWithCategoryTag(DatastoreService datastore, String categoryTag,  int limit){
		Query q = new Query("Insight").addSort("timestamp", Query.SortDirection.DESCENDING);
		
		Filter categoryTagFilter = new FilterPredicate("categoryTag", FilterOperator.EQUAL, categoryTag);
		q.setFilter(categoryTagFilter);
		
		ArrayList<Insight> insights = executeQuery(datastore, q, limit);
		return insights;
	}

}