package org.terranga.general;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.*;

public class RequestInfo {
	
	private boolean valid;
	private String resource;
	private String resourceIdentifier;
	private HttpServletRequest request;
	private HttpServletResponse response;

	
	public RequestInfo(HttpServletRequest req, HttpServletResponse resp){
		setValid(true);
		setResource(null);
		setResourceIdentifier(null);
		parseRequest(req);
		
		setRequest(req);
		setResponse(resp);
	}

	private void parseRequest(HttpServletRequest req){
		String[] uriParts = req.getRequestURI().split("/"); /* ".../site/admin" */
		
		if (uriParts.length<3){
			setResource("home");
			return;
		}

		
		String resource = uriParts[2];
		if (uriParts.length==3){ // .../site/profile
			setResource(resource); // even if the resource is not listed, we set it here because it may be a static page
			return;
		}
		
		if (uriParts.length==4){ // .../site/admin/1234
			setResource(resource);
			setResourceIdentifier(uriParts[3]);
			return;
		}
		
		System.out.println("INVALID REQUEST! !");
		// malformed URL, too many url sub-components (.../site/resource/123/awefawef...)
		setValid(false);
	}


	public static ArrayList<String> generateResourceList(){
		ArrayList<String> resources = new ArrayList<String>();
		ResourceBundle bundle = ResourceBundle.getBundle("resources.config");
		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()){
			String key = keys.nextElement();
			String value = bundle.getString(key);
			if (key.equals("resources")){
				String[] list = value.split(";");
				for (int i=0; i<list.length; i++){
					resources.add(list[i]);
				}
			}
		}
		
		resources.add("home");
		return resources;
	}


	public static ArrayList<String> generateAdminsList(){
		ArrayList<String> admins = new ArrayList<String>();
		ResourceBundle bundle = ResourceBundle.getBundle("resources.admins");
		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()){
			String key = keys.nextElement();
			String value = bundle.getString(key);
			if (key.equals("admins")){
				String[] list = value.split(";");
				for (int i=0; i<list.length; i++){
					admins.add(list[i]);
				}
			}
		}
		
		return admins;
	}

	
	
	public void setValid(boolean valid) {
		this.valid = valid;
	}


	public boolean isValid() {
		return valid;
	}


	public void setResource(String resource) {
		this.resource = resource;
	}


	public String getResource() {
		return resource;
	}


	public void setResourceIdentifier(String resourceIdentifier) {
		this.resourceIdentifier = resourceIdentifier;
	}


	public String getResourceIdentifier() {
		return resourceIdentifier;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public HttpServletResponse getResponse() {
		return response;
	}


}