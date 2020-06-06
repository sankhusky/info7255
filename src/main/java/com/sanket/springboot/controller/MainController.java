package com.sanket.springboot.controller;

import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sanket.springboot.service.PlanService;




@RestController
public class MainController {
	
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
	    public ResponseEntity addPlan(@RequestHeader HttpHeaders reqHeader, @RequestBody Object body, HttpServletResponse response) throws IOException, ProcessingException, Exception {

	        Boolean isSchemaValid = false;
//	        String jsonValueString = body;
//	        File schemaFile = new ClassPathResource("./static/applicationSchema.json").getFile();

	        //Validate if the incoming data is in sync with json schema
//	        final JsonSchema jsonSchema = ValidationsUtil.getSchemaNode(schemaFile);
//	        final JsonNode jsonNode = ValidationsUtil.getJsonNode(jsonValueString);

//	        if (ValidationsUtil.isJsonValid(jsonSchema, jsonNode)) {
	            isSchemaValid = true;
//	        }
	            planService.save(body);
	            return new ResponseEntity<Object>(body, HttpStatus.CREATED);
//
//	        //convert request body string to JSON Object
//	        JsonObject jsonObject = new JsonObject();	        
//	        Object KEY = jsonObject.get("objectId");
//
//	        if (isSchemaValid) {
//	            //Check if there is already a plan with same Key
//	            if (planService.findById(KEY.toString())!=null) {
////	                JsonParser parser = new JsonParser();
////	                JsonObject jOutput = parser.parse("{\"Duplicate\": \"Plan already Exist !! \"}").getAsJsonObject();
//	                return new ResponseEntity < Object > ("{\"Duplicate\": \"Plan already Exist !! \"}", HttpStatus.BAD_REQUEST);
//	            } else {
////	                JSONObject reObj = JedisBean.insertPlan(jsonObject);
//	            	planService.save(body);
////	                elasticSearch.indexingPlans();
////	                if (reObj != null) {
//	                    return new ResponseEntity<Object>(body, HttpStatus.CREATED);
////	                } else {
////	                    JsonParser parser = new JsonParser();
////	                    JsonObject errRes = parser.parse("{\"error\": \"Couldn't add a new plan\"}").getAsJsonObject();
////	                    return new ResponseEntity < > (errRes.toString(), HttpStatus.NOT_FOUND);
////	                }
//	            }
//	        } else {
//	            throw new Exception("JSON Schema is invalid");
//	        }
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
