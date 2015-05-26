package org.terranga.general;

import java.util.HashMap;
import java.util.Map;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

import org.terranga.general.*;


public class Image {
	
	private String uniqueId;
	private String name;
	private String key; // blob key
	private String address;

	
	public Image(){
		setUniqueId("none");
		setName("none");
		setKey("none");
		setAddress("none");
	}

	
	public Image(BlobInfo info, BlobKey blobKey){
    	String keyString = blobKey.getKeyString();

    	setUniqueId(keyString.substring(keyString.length()-8));
    	setKey(keyString);
    	setName(info.getFilename());

	    ImagesService imagesService = ImagesServiceFactory.getImagesService();
	    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(info.getBlobKey());
	    setAddress(imagesService.getServingUrl(options));
	}

	
	
	public Image(Entity imageEntity){
		setUniqueId(imageEntity.getKey().getName());
		setName((String)imageEntity.getProperty("name"));
		setKey((String)imageEntity.getProperty("key"));
		setAddress((String)imageEntity.getProperty("address"));
	}
	
	public Entity createEntityVersion(){
        Entity imageEntity = new Entity("Image", getUniqueId());
        imageEntity.setProperty("key", getKey());
        imageEntity.setProperty("name", getName());
        imageEntity.setProperty("address", getAddress());
		return imageEntity;
	}

	
	public Map<String, Object> getSummary(){
		Map<String, Object> summary = new HashMap<String, Object>();
		summary.put("name", getName());
		summary.put("key", getKey());
		summary.put("address", getAddress());
		summary.put("id", getUniqueId());
		return summary;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}


	public String getUniqueId() {
		return uniqueId;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getName() {
		return name;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public String getKey() {
		return key;
	}

	public void setAddress(String address) {
		address = address.replace("http:", "https:"); // always use ssl
		this.address = address;
	}

	public String getAddress() {
		return address;
	}
	
	public void save(DatastoreService datastore){
		datastore.put(createEntityVersion());
	}
	
	public void save(){
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(createEntityVersion());
	}

	
	
	
	public static Image fetchImage(DatastoreService datastore, String imageId){
		Image image= null;
		try{
        	Entity imgEntity = datastore.get(KeyFactory.createKey("Image", imageId));
        	image = new Image(imgEntity);
		}
		catch(EntityNotFoundException e){
			
		}
		
		return image;
	}

	

}
