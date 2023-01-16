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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.core.HazelcastInstance;

@Configuration
public class ApplicationRunner implements CommandLineRunner {

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
        System.out.println(mapping);

        hazelcastInstance.getSql().execute(mapping);
        
        hazelcastInstance.getMap(mapName).put("hello", new Date());
        
        String query = "SELECT * FROM " + mapName;
        System.out.println(query);

        hazelcastInstance.getSql().execute(query).forEach(System.out::println);
    }

}
