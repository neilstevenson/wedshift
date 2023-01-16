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
import java.util.stream.Collectors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws Exception {
		Set<String> keys = System.getProperties().keySet()
				.stream()
				.map(key -> key.toString())
				.collect(Collectors.toCollection(TreeSet::new));

		System.out.println("====================");
		keys.forEach(key -> System.out.println(key + "==" + System.getProperty(key)));
		System.out.println("====================");

		SpringApplication.run(Application.class, args);
	}

}
