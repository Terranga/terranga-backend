package org.terranga.general;

import java.util.Date;
import java.util.UUID;

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
	
	
	

}
