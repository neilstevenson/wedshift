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

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Application {

	public static void main(String[] args) throws Exception {
		Set<String> keys = System.getProperties().keySet()
				.stream()
				.map(key -> key.toString())
				.filter(key -> key.startsWith("hazelcast"))
				.collect(Collectors.toCollection(TreeSet::new));

		if (keys.size()==0) {
			System.out.println("No hazelcast properties, exiting");
			System.exit(0);
		}

		System.out.println("====================");
		keys.forEach(key -> System.out.println(key + " :: value len==" + System.getProperty(key).length()));
		System.out.println("====================");

		TimeUnit.MINUTES.sleep(20L);
	}

}
