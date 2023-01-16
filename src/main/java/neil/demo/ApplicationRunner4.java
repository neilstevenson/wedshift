/*
 * Copyright (c) 2008-2023, Hazelcast, Inc. All Rights Reserved.
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

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.hazelcast.core.HazelcastInstance;

@Configuration
@Order(value = 4)
public class ApplicationRunner4 implements CommandLineRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRunner4.class);

    @Autowired
    private HazelcastInstance hazelcastInstance;

	@Override
	public void run(String... args) throws Exception {
        String mapName = "neil";
        
        String mapping = "CREATE OR REPLACE MAPPING " + mapName
                        + " TYPE IMap "
                        + " OPTIONS ( "
                        + " 'keyFormat' = 'java',"
                        + " 'keyJavaClass' = '" + String.class.getName() + "',"                         
                        + " 'valueFormat' = 'java',"
                        + " 'valueJavaClass' = '" + Date.class.getName() + "'"                          
                        + " )";
        LOGGER.info(mapping);

        hazelcastInstance.getSql().execute(mapping);
        
        hazelcastInstance.getMap(mapName).put("hello", new Date());
        
        String query = "SELECT * FROM " + mapName;
        LOGGER.info(query);

        hazelcastInstance.getSql().execute(query).forEach(sqlRow -> LOGGER.info("SqlRow: {}", sqlRow));
		TimeUnit.SECONDS.sleep(2L);
		LOGGER.info("Done");
    }

}
