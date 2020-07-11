package com.sanket.springboot.service;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.NotFoundException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;



@Service
public class PlanService {

	@Autowired
    private RedisTemplate<String, Object> planTemplate;
	
	private static final String REDIS_PREFIX_PLANS= "plans";

	private static final String REDIS_KEYS_SEPARATOR = ":";
	
	@Autowired
    private RedisTemplate<String, Object> objTemplate;
	
	public List<Object> findByPattern(final String pattern) {
		return getValueOperations().multiGet(objTemplate.keys(getRedisKey(pattern)));
	}
	public Object findById(final String planId) {
		final Object plan = getValueOperations().get(planId);
		if(plan == null) {
			throw new NotFoundException("Plan does not exist in the DB");
		}
		return plan;
	}
	
	public void save(final Object obj) {
//		JSONObject json = new JSONObject();
//		json.
//		obj.setId(UUID.randomUUID().toString());
		String json = new Gson().toJson(obj);
		try {
	        json = new JSONTokener(json).nextValue().toString();
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }		
		JSONObject jsonObj = new JSONObject(json);
		getValueOperations().set(jsonObj.getString("objectId"), obj);
	}
	
	
	public void delete(final String planId) {
		if(!planTemplate.delete(getRedisKey(UUID.fromString(planId).toString()))) {
			throw new NotFoundException("Plan does not exist in the DB");
		}
	}
	
	
	private ValueOperations<String, Object> getValueOperations() {
		return objTemplate.opsForValue();
	}
	
	private String getRedisKey(final String planId) {
        return REDIS_PREFIX_PLANS + REDIS_KEYS_SEPARATOR + planId;
    }

}
