package com.sanket.springboot.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ProcessingException;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.sanket.springboot.service.PlanService;

import io.micrometer.core.instrument.config.validate.ValidationException;




@RestController
public class MainController {
	
	private static final String CLIENT_ID = "47999546863-kkllstfekqupjhisl6medh092m8nvbmc.apps.googleusercontent.com";
	@Autowired
	private PlanService planService;
	
	Stack<String> eTagStack = new Stack();

	
//    Jedis jedis = redisConnection.getConnection();

	@GetMapping(value = "/plan", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Object>> getPlans() {
		final List<Object> plans = planService.findByPattern("*");
		return new ResponseEntity<>(plans, HttpStatus.OK);
//		return ResponseEntity.ok().body("getresponse");
	}

	 @GetMapping("/plan/{planId}")
	    public ResponseEntity <Object> getPlanById(@RequestHeader HttpHeaders reqHeader, @PathVariable String planId, HttpServletResponse response)  {

	        String ifNoneMatchValue = reqHeader.getFirst("if-none-match");
	        if(ifNoneMatchValue != null && ifNoneMatchValue.length()>0) {
	            if (eTagStack.size() > 0 && (eTagStack.peek().equals(ifNoneMatchValue)))
	                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("");
	        }

	        Object responseObj = planService.findById(planId);
	        if (responseObj != null) {
	            UUID uuid = UUID.randomUUID();
	            String randomUUIDString = uuid.toString();
	            System.out.println(randomUUIDString);
	            eTagStack.push(randomUUIDString);
	                return  ResponseEntity.ok().eTag(randomUUIDString).body(responseObj);
	        } else {
//	            JsonParser parser = new JsonParser();
//	            JsonObject errRes = parser.parse("{\"error\": \"Plan with this planId is not found\"}").getAsJsonObject();
	            return new ResponseEntity < > ("{\\\"error\\\": \\\"Plan with this planId is not found\\\"}", HttpStatus.NOT_FOUND);
	        }
	    }
	 
	 @PostMapping(path = "/plan", consumes = "application/json")
	    public ResponseEntity addPlan(@RequestHeader Map<String,String> reqHeader, @RequestBody String body, HttpServletResponse response) throws IOException, ProcessingException, Exception {
//idtoken : eyJhbGciOiJSUzI1NiIsImtpZCI6IjY1YjNmZWFhZDlkYjBmMzhiMWI0YWI5NDU1M2ZmMTdkZTRkZDRkNDkiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiNDc5OTk1NDY4NjMta2tsbHN0ZmVrcXVwamhpc2w2bWVkaDA5Mm04bnZibWMuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI0Nzk5OTU0Njg2My1ra2xsc3RmZWtxdXBqaGlzbDZtZWRoMDkybThudmJtYy5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjEwOTU4NTMxNDMyMDc2NTY0MTg1MCIsImF0X2hhc2giOiJGMDdFd3IydG1UOWc0cGZmODZndUlRIiwiaWF0IjoxNTk0NDUxNTQ5LCJleHAiOjE1OTQ0NTUxNDl9.B_On55MBbD7kKFGH-s3zFG7ufNFrST-u-20nJ71VawPFn7SA2ZT1-UCo1lfsSpD1yIj5l7Jm8-dBnorLwcvgm7-Z1Wa5psX9LEvFEYd0yURF9POr_Qvx37V8Um6zTxvqrl6Z6poj0AqTOrulCSGNNRMOLCRf-ajqyVT7UdxphK-G70ZrSCQkHMB1XFR-Y-AdJdjnJ7ErzMZ4O3qKfb_DJ_1WSD6d_HCKCsfePPX5d3sjBFSbY0CwjuO47Q9ZQjVqWTU3kTJHFWCb7YeNtfnRLES0RH-OCj4yblaGXvC8yPKN50XDsq0Bf7RQYPtJ70i-u6961ZmjwU4y2a3e47UssQ
//		 Header : [content-type:"application/json", 
//		 if-none-match:"94335f9c-c616-4b9b-bd26-39c321e407cb",
//		 authorization:"Bearer ya29.a0AfH6SMChoLLe1REbmiG8OisEqLR8kCmpBC2rvjA3fAn88d4ueFlzZEE4bemcljuH9rmzEK0
//		 OYQ1pkVgCbZqNNt727qhzDHxihMQJl2r9IaMT0PLAksQqFgnRBVA_SwI8Rix-7jIXZj2r8pD5K-OtNyn7GVP0AXxPBD8", 
//		 user-agent:"PostmanRuntime/7.25.0", 
//		 accept:"*/*", 
//		 cache-control:"no-cache", 
//		 postman-token:"56b4bd79-48ee-482c-8377-dc494ddf87b8",
//		 host:"localhost:8080", accept-encoding:"gzip, deflate, br", connection:"keep-alive", content-length:"1219"]
		 
		/* OAuth 2.0 validation */
		 String idTokenString = reqHeader.get("authorization");
		 if(idTokenString!=null) {
			 idTokenString = idTokenString.split(" ").length>1 ? idTokenString.split(" ")[1] : idTokenString;
			 idTokenString = idTokenString.trim();
		 }
		 
//		 idTokenString = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjY1YjNmZWFhZDlkYjBmMzhiMWI0YWI5NDU1M2ZmMTdkZTRkZDRkNDkiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiNDc5OTk1NDY4NjMta2tsbHN0ZmVrcXVwamhpc2w2bWVkaDA5Mm04bnZibWMuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI0Nzk5OTU0Njg2My1ra2xsc3RmZWtxdXBqaGlzbDZtZWRoMDkybThudmJtYy5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjEwOTU4NTMxNDMyMDc2NTY0MTg1MCIsImF0X2hhc2giOiJGMDdFd3IydG1UOWc0cGZmODZndUlRIiwiaWF0IjoxNTk0NDUxNTQ5LCJleHAiOjE1OTQ0NTUxNDl9.B_On55MBbD7kKFGH-s3zFG7ufNFrST-u-20nJ71VawPFn7SA2ZT1-UCo1lfsSpD1yIj5l7Jm8-dBnorLwcvgm7-Z1Wa5psX9LEvFEYd0yURF9POr_Qvx37V8Um6zTxvqrl6Z6poj0AqTOrulCSGNNRMOLCRf-ajqyVT7UdxphK-G70ZrSCQkHMB1XFR-Y-AdJdjnJ7ErzMZ4O3qKfb_DJ_1WSD6d_HCKCsfePPX5d3sjBFSbY0CwjuO47Q9ZQjVqWTU3kTJHFWCb7YeNtfnRLES0RH-OCj4yblaGXvC8yPKN50XDsq0Bf7RQYPtJ70i-u6961ZmjwU4y2a3e47UssQ";
		 GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance())
				    // Specify the CLIENT_ID of the app that accesses the backend:
				    .setAudience(Collections.singletonList(CLIENT_ID))
				    // Or, if multiple clients access the backend:
				    //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
				    .build();

				// (Receive idTokenString by HTTPS POST)

		 GoogleIdToken idToken=null;
		 try {
				 idToken = verifier.verify(idTokenString);
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
				if (idToken != null) {
				  Payload payload = idToken.getPayload();

				  // Print user identifier
				  String userId = payload.getSubject();
				  System.out.println("User ID: " + userId);
				} else {
					System.out.println("Invalid ID token.");
					return new ResponseEntity<String>("{"
        	    			+ "\"error\":\""+"Unable to verify token"+"\"}", HttpStatus.UNAUTHORIZED);
				}
		 
		/*--------------------------*/
	        Boolean isSchemaValid = false;
//	        String jsonValueString = body;
	        File schemaFile = new ClassPathResource("./static/schema.json").getFile();
	          	            	    
	            	    try {
	            	    	
	            	    	JSONObject jsonSchema = new JSONObject(
	      	            	      new JSONTokener(new FileInputStream(schemaFile)));
	      	            	    JSONObject jsonSubject =new JSONObject(body);
	      	            	    Schema schema = SchemaLoader.load(jsonSchema);
	      	            	    schema.validate(jsonSubject);
	            	    }catch(ValidationException ve) {
	            	    	return new ResponseEntity<String>("{"
	            	    			+ "\"error\":\""+ve.getMessage()+"\"}", HttpStatus.BAD_REQUEST);
	            	    }
	            	    
	            planService.save(body);
	            return new ResponseEntity<Object>(body, HttpStatus.CREATED);

	    }
	 
	 @PutMapping("/plan/{planId}")
	    public ResponseEntity<String> update(@RequestBody String body, @RequestHeader Map<String,String> reqHeaderMap, @RequestHeader HttpHeaders reqHeader, @PathVariable String planId) throws IOException, ProcessingException {

		 String idTokenString = reqHeaderMap.get("authorization");
		 if(idTokenString!=null) {
			 idTokenString = idTokenString.split(" ").length>1 ? idTokenString.split(" ")[1] : idTokenString;
			 idTokenString = idTokenString.trim();
		 }
		 
//		 idTokenString = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjY1YjNmZWFhZDlkYjBmMzhiMWI0YWI5NDU1M2ZmMTdkZTRkZDRkNDkiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiNDc5OTk1NDY4NjMta2tsbHN0ZmVrcXVwamhpc2w2bWVkaDA5Mm04bnZibWMuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiI0Nzk5OTU0Njg2My1ra2xsc3RmZWtxdXBqaGlzbDZtZWRoMDkybThudmJtYy5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjEwOTU4NTMxNDMyMDc2NTY0MTg1MCIsImF0X2hhc2giOiJGMDdFd3IydG1UOWc0cGZmODZndUlRIiwiaWF0IjoxNTk0NDUxNTQ5LCJleHAiOjE1OTQ0NTUxNDl9.B_On55MBbD7kKFGH-s3zFG7ufNFrST-u-20nJ71VawPFn7SA2ZT1-UCo1lfsSpD1yIj5l7Jm8-dBnorLwcvgm7-Z1Wa5psX9LEvFEYd0yURF9POr_Qvx37V8Um6zTxvqrl6Z6poj0AqTOrulCSGNNRMOLCRf-ajqyVT7UdxphK-G70ZrSCQkHMB1XFR-Y-AdJdjnJ7ErzMZ4O3qKfb_DJ_1WSD6d_HCKCsfePPX5d3sjBFSbY0CwjuO47Q9ZQjVqWTU3kTJHFWCb7YeNtfnRLES0RH-OCj4yblaGXvC8yPKN50XDsq0Bf7RQYPtJ70i-u6961ZmjwU4y2a3e47UssQ";
		 GoogleIdTokenVerifier verifier = null;
		try {
			verifier = new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance())
					    // Specify the CLIENT_ID of the app that accesses the backend:
					    .setAudience(Collections.singletonList(CLIENT_ID))
					    // Or, if multiple clients access the backend:
					    //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
					    .build();
		} catch (GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

				// (Receive idTokenString by HTTPS POST)

		 GoogleIdToken idToken=null;
		 try {
				 idToken = verifier.verify(idTokenString);
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
				if (idToken != null) {
				  Payload payload = idToken.getPayload();

				  // Print user identifier
				  String userId = payload.getSubject();
				  System.out.println("User ID: " + userId);
				} else {
					System.out.println("Invalid ID token.");
					return new ResponseEntity<String>("{"
        	    			+ "\"error\":\""+"Unable to verify token"+"\"}", HttpStatus.UNAUTHORIZED);
				}
		 
		/*--------------------------*/
	     
	        String ifMatchValue = reqHeader.getFirst("if-match");

	        if(ifMatchValue != null && ifMatchValue.length()>0) {
	            if (eTagStack.size() > 0 && !(eTagStack.peek().equals(ifMatchValue)))
	                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Resource has been modified please GET first for new Etag");
	        }

	        //If token is verified retrieve json schema
	        Boolean isSchemaValid = false;
	        String jsonValueString = body;
	        File schemaFile = new ClassPathResource("./static/schema.json").getFile();

	          	            	    
	            	    try {
	            	    	
	            	    	JSONObject jsonSchema = new JSONObject(
	      	            	      new JSONTokener(new FileInputStream(schemaFile)));
	      	            	    JSONObject jsonSubject =new JSONObject(body);
	      	            	    Schema schema = SchemaLoader.load(jsonSchema);
	      	            	    schema.validate(jsonSubject);
	            	    }catch(ValidationException ve) {
	            	    	return new ResponseEntity<String>("{"
	            	    			+ "\"error\":\""+ve.getMessage()+"\"}", HttpStatus.BAD_REQUEST);
	            	    }
	            	    
	            
	            UUID uuid = UUID.randomUUID();
	            String randomUUIDString = uuid.toString();
	            System.out.println(randomUUIDString);
	            eTagStack.push("\""+randomUUIDString + "\"");
	            planService.save(body);
	             return new ResponseEntity<String>("{"
    	    			+ "\"msg\":\"JSON instance updated in redis\"}", HttpStatus.OK);
//	            return  ResponseEntity.status(HttpStatus.NO_CONTENT).eTag(randomUUIDString).body("\"Updated\":\"JSON instance updated in redis\"");
	            //return new ResponseEntity<String>("JSON data updated in redis", HttpStatus.NO_CONTENT);

//	        }else
//	            throw new InvalidInputExceptions("JSON Schema is invalid");
	    }
	 
	 @DeleteMapping("/plan/{planId}")
	    public ResponseEntity < String > deletePlan(@RequestHeader HttpHeaders reqHeader, @PathVariable String planId) throws IOException {

	        planService.delete(planId);
//	        if (successDelete) {
//	            JsonParser parser = new JsonParser();
//	            JsonObject errRes = parser.parse("{\"Success\": \"Deleted the record with plan Id\"}").getAsJsonObject();
	            return new ResponseEntity < String > ("{\"Success\": \"Deleted the record with plan Id\"}", HttpStatus.NO_CONTENT);
//
//	        } else {
//	            JsonParser parser = new JsonParser();
//	            JsonObject errRes = parser.parse("{\"Error\": \"Plan with provided ID doesn't exist\"}").getAsJsonObject();
//	            return new ResponseEntity < > (errRes.toString(), HttpStatus.NOT_FOUND);
//	        }
	    }
	 
}
