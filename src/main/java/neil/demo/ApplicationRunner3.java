/*
 * Copyright (c) 2008-2022, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neil.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.map.IMap;
import com.hazelcast.org.json.JSONObject;

@Configuration
@Order(value = 3)
public class ApplicationRunner3 implements CommandLineRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRunner3.class);
	private static final String PRINCIPAL = System.getProperty("user.name");
	
	@Autowired
	private FileUtils fileUtils;
	@Autowired
	private HazelcastInstance hazelcastInstance;

	@Override
	public void run(String... args) throws Exception {
		List<Resource> resources = this.fileUtils.getResources();
		long initialDelay = MyConstants.DATA_FEED_WRITE_INTERVAL_MS;
		ApplicationRunner3Runnable applicationRunner3Runnable
			= new ApplicationRunner3Runnable(this.hazelcastInstance, resources.get(0), initialDelay);

		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(applicationRunner3Runnable);
		TimeUnit.SECONDS.sleep(2L);
		LOGGER.info("Done");
	}

	public class ApplicationRunner3Runnable implements Runnable {

		private final HazelcastInstance hazelcastInstance;
		private final Resource resource;
		private final long initialDelay;

		ApplicationRunner3Runnable(HazelcastInstance arg0, Resource arg1, long arg2) {
			this.hazelcastInstance = arg0;
			this.resource = arg1;
			this.initialDelay = arg2;
		}
		
		@Override
		public void run() {
			int count = 0;
			String key = this.resource.getFilename().split("\\.")[0];
            String line;
			IMap<String, HazelcastJsonValue> profileMap = this.hazelcastInstance.getMap(MyConstants.IMAP_PROFILE);
			try {
		        try (BufferedReader bufferedReader =
		                new BufferedReader(
		                        new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
		        	TimeUnit.MILLISECONDS.sleep(initialDelay);
		        	while ((line = bufferedReader.readLine()) != null) {
		            	// Latitude, Longitude
		            	String[] tokens = line.split(",");

		                try {
		                	JSONObject json = new JSONObject(profileMap.get(key).toString());
		                	json.put(MyConstants.JSON_FIELD_LATITUDE, Double.parseDouble(tokens[0]));
	                        json.put(MyConstants.JSON_FIELD_LONGITUDE, Double.parseDouble(tokens[1]));
	                        json.put(MyConstants.JSON_FIELD_UPDATED_BY, PRINCIPAL);
	                        json.put(MyConstants.JSON_FIELD_UPDATED_TIME, MyUtils.getNow());
	                        
		                	profileMap.set(key, new HazelcastJsonValue(json.toString()));
		                	
		                	if (count % MyConstants.LOG_EVERY == 0) {
		                		String message = String.format("Write %6d for '%s'", count, key);
		                		LOGGER.info(message);
		                	}

		                	TimeUnit.MILLISECONDS.sleep(MyConstants.DATA_FEED_WRITE_INTERVAL_MS);

		                } catch (Exception e) {
	                		String message = String.format("Line %6d for '%s'", count, key);
		                	LOGGER.error(message, e);
		                	break;
		                }
		                
		            	count++;
		            }
		        }
			} catch (Exception e2) {
				LOGGER.error("e2", e2);
			}
	        LOGGER.info("End {}", this);
		}
	}

}
