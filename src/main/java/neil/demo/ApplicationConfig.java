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

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientConnectionStrategyConfig.ReconnectMode;
import com.hazelcast.client.config.ConnectionRetryConfig;

@Configuration
public class ApplicationConfig {

	@Bean
	public ClientConfig clientConfig() {
		ClientConfig clientConfig = new ClientConfig();
		
		Set<String> labels = new TreeSet<>(List.of("neil"));
		clientConfig.setLabels(labels);
		
		clientConfig.getNetworkConfig().addAddress("10.128.0.240").addAddress("10.128.0.241").addAddress("10.128.0.242");
		
        clientConfig.getConnectionStrategyConfig()
        .setReconnectMode(ReconnectMode.OFF)
        .setConnectionRetryConfig(new ConnectionRetryConfig().setClusterConnectTimeoutMillis(5000));

        clientConfig.getMetricsConfig().setEnabled(true);
        
		return clientConfig;
	}

}
