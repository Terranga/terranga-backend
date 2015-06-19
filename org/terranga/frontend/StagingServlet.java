package org.terranga.frontend;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.terranga.general.*;


@SuppressWarnings("serial")
public class StagingServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		System.out.println("STAGING SERVLET: Get");
		resp.setContentType("text/html");
		
		RequestInfo request = new RequestInfo(req, resp);
		String page = request.getResource();
		page = page.concat(".html");
		
		String html = getFileText("staging/"+page);

		
		resp.getWriter().print(html);
		
		return;
	}
	
	
	
	public static String getFileText(String filepath){
		String text = null;
		try {
			FileReader fileReader = new FileReader(filepath);
			BufferedReader reader = new BufferedReader(fileReader);
			String stringFromFile = "";
			while( stringFromFile != null ){
				if (text ==null){ text = ""; }
				text = text+stringFromFile+"\n";
				stringFromFile = reader.readLine();  // read next line
			}
			reader.close();
		}
	    catch(FileNotFoundException e) { //route to 404 page
	    	text = "NOT FOUND";
	    	return text;
	    }
	    catch(IOException e) { e.printStackTrace(); } //route to error page, please try again
	    return text;
	}
	
	


}
