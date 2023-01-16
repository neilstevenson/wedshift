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

@Configuration
@Order(value = 2)
public class ApplicationRunner2 implements CommandLineRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRunner2.class);
			
	@Autowired
	private FileUtils fileUtils;
	@Autowired
	private HazelcastInstance hazelcastInstance;

	@Override
	public void run(String... args) throws Exception {
		IMap<String, HazelcastJsonValue> profileMap = this.hazelcastInstance.getMap(MyConstants.IMAP_PROFILE);
		
		this.fileUtils.getResources()
		.stream()
		.map(Resource::getFilename)
		.map(fileName -> fileName.split("\\.")[0])
		.forEach(key -> {
			String now = MyUtils.getNow();
			
			HazelcastJsonValue value = new HazelcastJsonValue("{"
					+ " \"" + MyConstants.JSON_FIELD_CREATED_BY  + "\" : \"" + this.getClass().getSimpleName() + "\""
					+ ", \"" + MyConstants.JSON_FIELD_CREATED_TIME + "\" : \"" + now + "\""
					+ ", \"" + MyConstants.JSON_FIELD_LATITUDE + "\" : 0.0"
					+ ", \"" + MyConstants.JSON_FIELD_LONGITUDE + "\" : 0.0"
					+ ", \"" + MyConstants.JSON_FIELD_NAME + "\" : \"" + key + "\""
					+ ", \"" + MyConstants.JSON_FIELD_UPDATED_BY  + "\" : \"" + this.getClass().getSimpleName() + "\""
					+ ", \"" + MyConstants.JSON_FIELD_UPDATED_TIME  + "\" : \"" + now + "\""
					+ " }");
			
			profileMap.set(key, value);
			LOGGER.info("'{}'.set('{}', '{}')", profileMap.getName(), key, value);
		});
	}

}
