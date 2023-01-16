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

public class MyConstants {
	static final String IMAP_POSITION = "position";
	static final String IMAP_PROFILE = "profile";
	
	static final List<String> IMAPS = List.of(
			IMAP_POSITION, IMAP_PROFILE
			);

	static final String JSON_FIELD_AVATAR = "avatar";
	static final String JSON_FIELD_CREATED_BY = "created_by";
	static final String JSON_FIELD_CREATED_TIME = "created_time";
	static final String JSON_FIELD_LATITUDE = "latitude";
	static final String JSON_FIELD_LONGITUDE = "longitude";
	static final String JSON_FIELD_NAME = "name";
	static final String JSON_FIELD_UPDATED_BY = "updated_by";
	static final String JSON_FIELD_UPDATED_TIME = "updated_time";
	
	static final long DATA_FEED_WRITE_INTERVAL_MS = 3_000L;
	
	static final int LOG_EVERY = 100;

    static final String STOMP_QUEUE_PREFIX = "queue";
    static final String STOMP_QUEUE_SUFFIX = "location";

    public static final String WEBSOCKET_ENDPOINT = "hazelcast";

}
