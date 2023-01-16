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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;

@Configuration
@Order(value = 1)
public class ApplicationRunner1 implements CommandLineRunner {

	@Autowired
	private HazelcastInstance hazelcastInstance;

	@Override
	public void run(String... args) throws Exception {
		EventJournalConfig eventJournalConfig = new EventJournalConfig().setEnabled(true);
		
        MapConfig positionMapConfig = new MapConfig(MyConstants.IMAP_POSITION);
        positionMapConfig.setEventJournalConfig(eventJournalConfig);
        this.hazelcastInstance.getConfig().addMapConfig(positionMapConfig);

        MapConfig profileMapConfig = new MapConfig(MyConstants.IMAP_PROFILE);
        profileMapConfig.setEventJournalConfig(eventJournalConfig);
        this.hazelcastInstance.getConfig().addMapConfig(profileMapConfig);

		this.addMappings();

		MyConstants.IMAPS.forEach(this.hazelcastInstance::getMap);
	}
	
	private void addMappings() {
		String profileMapping = "CREATE OR REPLACE MAPPING \"" + MyConstants.IMAP_PROFILE + "\""
				+ " ("
				+ "  \"__key\" VARCHAR,"
				+ "  \"" + MyConstants.JSON_FIELD_CREATED_BY + "\" VARCHAR,"
				+ "  \"" + MyConstants.JSON_FIELD_CREATED_TIME + "\" VARCHAR,"
				+ "  \"" + MyConstants.JSON_FIELD_LATITUDE + "\" DOUBLE,"
				+ "  \"" + MyConstants.JSON_FIELD_LONGITUDE + "\" DOUBLE,"
				+ "  \"" + MyConstants.JSON_FIELD_NAME + "\" VARCHAR,"
				+ "  \"" + MyConstants.JSON_FIELD_UPDATED_BY + "\" VARCHAR,"
				+ "  \"" + MyConstants.JSON_FIELD_UPDATED_TIME + "\" VARCHAR"
				+ " )"
				+ " TYPE IMap "
				+ " OPTIONS ( "
				+ " 'keyFormat' = 'java',"
				+ " 'keyJavaClass' = '" + String.class.getName() + "',"
				+ " 'valueFormat' = 'json-flat'"
				+ " )";
		
		String positionMapping = "CREATE OR REPLACE MAPPING \"" + MyConstants.IMAP_POSITION + "\""
				+ " ("
				+ "  \"__key\" VARCHAR,"
				+ "  \"" + MyConstants.JSON_FIELD_LATITUDE + "\" DOUBLE,"
				+ "  \"" + MyConstants.JSON_FIELD_LONGITUDE + "\" DOUBLE,"
				+ "  \"" + MyConstants.JSON_FIELD_NAME + "\" VARCHAR,"
				+ "  \"" + MyConstants.JSON_FIELD_UPDATED_TIME + "\" VARCHAR"
				+ " )"
				+ " TYPE IMap "
				+ " OPTIONS ( "
				+ " 'keyFormat' = 'java',"
				+ " 'keyJavaClass' = '" + String.class.getName() + "',"
				+ " 'valueFormat' = 'json-flat'"
				+ " )";
		
		List.of(positionMapping, profileMapping).forEach(this.hazelcastInstance.getSql()::execute);
	}

}
