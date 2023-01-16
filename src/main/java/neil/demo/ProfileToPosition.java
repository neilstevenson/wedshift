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

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.jet.pipeline.JournalInitialPosition;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import com.hazelcast.jet.pipeline.StreamStage;
import com.hazelcast.org.json.JSONObject;

@Configuration
public class ProfileToPosition {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileToPosition.class);
	
	static Pipeline build() {
		Pipeline pipeline = Pipeline.create();

		StreamStage<Entry<String, HazelcastJsonValue>> s = pipeline
		.readFrom(Sources.<String, HazelcastJsonValue>mapJournal(MyConstants.IMAP_PROFILE,
				JournalInitialPosition.START_FROM_OLDEST)).withoutTimestamps()
		.map(ProfileToPosition::reduce);
		
		s.writeTo(Sinks.map(MyConstants.IMAP_POSITION));
		s.writeTo(Sinks.logger());

		return pipeline;
	}

	public static Entry<String, HazelcastJsonValue> reduce(Entry<String, HazelcastJsonValue> input) {
		String key = input.getKey();
		try {
			JSONObject json = new JSONObject(input.getValue().toString());
			
			String output = "{"
					+ " \"" + MyConstants.JSON_FIELD_LATITUDE + "\" : " + json.getDouble(MyConstants.JSON_FIELD_LATITUDE)
					+ ", \"" + MyConstants.JSON_FIELD_LONGITUDE + "\" : " + json.getDouble(MyConstants.JSON_FIELD_LONGITUDE)
					+ ", \"" + MyConstants.JSON_FIELD_NAME + "\" : \"" + json.getString(MyConstants.JSON_FIELD_NAME) + "\""
					+ ", \"" + MyConstants.JSON_FIELD_UPDATED_TIME + "\" : \"" + json.getString(MyConstants.JSON_FIELD_UPDATED_TIME) + "\""
					+ "}";
			
			return new SimpleImmutableEntry<>(key, new HazelcastJsonValue(output));
		} catch (Exception e) {
			String message = String.format("Key '%s'", key);
			LOGGER.error(message, e);
			return null;
		}
	}

}
