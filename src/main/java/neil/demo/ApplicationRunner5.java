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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.hazelcast.core.HazelcastInstance;

@Configuration
@Order(value = 5)
public class ApplicationRunner5 implements CommandLineRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRunner5.class);

    private static final String DESTINATION =
            "/" + MyConstants.STOMP_QUEUE_PREFIX
            + "/" + MyConstants.STOMP_QUEUE_SUFFIX;
	private static final String RUNTIME = MyUtils.getNow();

    @Autowired
    private HazelcastInstance hazelcastInstance;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

	@Override
	public void run(String... args) throws Exception {
    }

}
