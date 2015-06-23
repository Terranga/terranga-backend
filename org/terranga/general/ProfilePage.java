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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class ProfilePage {
	//AUTO GENERATED
	private String id;
	private Date timestamp;
				
	//USER INPUTED
	private String page;

	//DEFAULT CONSTRUCTOR
	public ProfilePage(){
		setId(randomString(8));
		setTimestamp(new Date());	
		setPage("none");
	}
	
	//CONSTRUCTOR FROM ENTITY
	public ProfilePage(Entity ent){
		setId(ent.getKey().getName());
		setTimestamp((Date)ent.getProperty("timestamp"));
		setPage((String)ent.getProperty("page"));
	}
	
	//ENTITY CONSTRUCTOR
	public Entity createEntityVersion(){
		Entity e = new Entity("ProfilePage", getId());
		e.setProperty("timestamp", getTimestamp());
		e.setProperty("page", getPage());
		
		return e;
	}	
	
	//GET SUMMARY FOR JSON USE
	public Map<String, Object> getSummary(){
		Map<String, Object> summary = new HashMap<String, Object>();
		
		//AUTO GENERATED
		summary.put("id", getId());
		summary.put("timestamp", getTimestamp().toString());
		
		//REVIEWED ID
		if(getPage().equals("none"))
			summary.put("page", "");
		else
			summary.put("page", getPage());
		
		return summary;
	}
	
	//UPDATE FROM JSON-SUMMARY
	public void update(JSONObject json) throws JSONException {
		if (json.has("page")){
			String p = json.getString("page");
			if (p.length() > 0)
				setPage(p);
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

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
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

	
//- - - - - - - - - - - - - - - - - - - - - - QUERIES - - - - - - - - - - - - - - - - - - - - - -//
	
	//EXECUTE QUERY
	private static ArrayList<ProfilePage> executeQuery(DatastoreService datastore, Query q, int limit){
		PreparedQuery pq = datastore.prepare(q);
						
		FetchOptions options = (limit==0) ? FetchOptions.Builder.withDefaults() : FetchOptions.Builder.withLimit(limit);
		QueryResultList<Entity> results = pq.asQueryResultList(options); //THE REQUEST
		ArrayList<ProfilePage> pages = new ArrayList<ProfilePage>();
		for(Entity e : results){
			pages.add(new ProfilePage(e));
		}
		return pages;
	}
				
	//FETCH SINGLE REVIEW (ID)
	public static ProfilePage fetchProfilePage(DatastoreService datastore, String pageID){
		ProfilePage search = null;
		try {
			Entity ent = datastore.get(KeyFactory.createKey("ProfilePage", pageID));
			search = new ProfilePage(ent);
		}
		catch(EntityNotFoundException e){
		}
		return search;
	}
			
	//FETCH ALL REVIEWS
	public static ArrayList<ProfilePage> fetchProfilePages(DatastoreService datastore, int limit){
		Query q = new Query("ProfilePage").addSort("timestamp", Query.SortDirection.DESCENDING);
		ArrayList<ProfilePage> pages = executeQuery(datastore, q, limit);
		return pages;
	}	
}
