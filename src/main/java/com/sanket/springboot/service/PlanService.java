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

		String json = new Gson().toJson(obj);
		try {
	        json = new JSONTokener(json).nextValue().toString();
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }		
		JSONObject jsonObj = new JSONObject(json);
		getValueOperations().set(jsonObj.getString("objectId"), obj);
	}
	
	public void update(final Object obj, String planId) {
		String json = new Gson().toJson(obj);
		try {
	        json = new JSONTokener(json).nextValue().toString();
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }		
		JSONObject jsonObj = new JSONObject(json);
		getValueOperations().set(planId, obj);
		
	}
	
	
//	 public static boolean patchUpdatePlan(JSONObject jsonObject){
//	        try {
//	            Jedis jedis = redisConnection.getConnection();
//	            String uuid = jsonObject.getString("objectType") + DEL + jsonObject.getString("objectId");
//	            Map<String, String> simpleMap = jedis.hgetAll(uuid);
//	            if(simpleMap.isEmpty()) {
//	                simpleMap = new HashMap<>();
//	            }
//
//	            for(Object key : jsonObject.keySet()) {
//	                String attributeKey = String.valueOf(key);
//	                Object attributeVal = jsonObject.get(String.valueOf(key));
//	                String edge = attributeKey;
//
//	                if(attributeVal instanceof JSONObject) {
//	                    JSONObject embdObject = (JSONObject) attributeVal;
//	                    String setKey = uuid + DEL + edge;
//	                    String embd_uuid = embdObject.get("objectType") + DEL + embdObject.getString("objectId");
//	                    jedis.sadd(setKey, embd_uuid);
//	                    patchUpdatePlan(embdObject);
//
//	                } else if (attributeVal instanceof JSONArray) {
//
//	                    JSONArray jsonArray = (JSONArray) attributeVal;
//	                    Iterator<Object> jsonIterator = jsonArray.iterator();
//	                    String setKey = uuid + DEL + edge;
//
//	                    while(jsonIterator.hasNext()) {
//	                        JSONObject embdObject = (JSONObject) jsonIterator.next();
//	                        String embd_uuid = embdObject.get("objectType") + DEL + embdObject.getString("objectId");
//	                        jedis.sadd(setKey, embd_uuid);
//	                        patchUpdatePlan(embdObject);
//	                    }
//
//	                } else {
//	                    simpleMap.put(attributeKey, String.valueOf(attributeVal));
//	                }
//	            }
//	            jedis.hmset(uuid, simpleMap);
//	            jedis.close();
//	            return true;
//
//	        }catch(JedisException e) {
//	            e.printStackTrace();
//	            return false;
//	        }
//
//	    }
	
	public void delete(final String planId) {
		if(!planTemplate.delete(planId)) {
			throw new NotFoundException("Plan does not exist in the DB");
		}
	}
	
	
	private ValueOperations<String, Object> getValueOperations() {
		return objTemplate.opsForValue();
	}
	
	private String getRedisKey(final String planId) {
        return REDIS_PREFIX_PLANS + REDIS_KEYS_SEPARATOR + planId;
    }
	public void save(final Object body, String planId) {
		getValueOperations().set(planId, body);		
	}

}
