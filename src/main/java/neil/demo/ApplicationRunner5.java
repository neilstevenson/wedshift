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

import java.util.Collection;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.jet.Job;
import com.hazelcast.jet.Observable;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.core.JobStatus;
import com.hazelcast.jet.pipeline.Pipeline;

@Configuration
@Order(value = 5)
public class ApplicationRunner5 implements CommandLineRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRunner5.class);

    private static final String DESTINATION =
            "/" + MyConstants.STOMP_QUEUE_PREFIX
            + "/" + MyConstants.STOMP_QUEUE_SUFFIX;

    @Autowired
    private HazelcastInstance hazelcastInstance;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

	@Override
	public void run(String... args) throws Exception {
        this.launchProfileToPosition();
        this.launchPositionToWebsocket();
        try {
            while (true) {
                    TimeUnit.MINUTES.sleep(1L);
                    this.logJobs();
            }
        } catch (Exception e) {
            LOGGER.error("Shutting down", e);
            this.hazelcastInstance.shutdown();
        }
    }

	private void launchProfileToPosition() {
		String jobName = MyConstants.IMAP_PROFILE + "_TO_" + MyConstants.IMAP_POSITION + "_" + MyUtils.getNow();

		Pipeline pipeline = ProfileToPosition.build();

		JobConfig jobConfig = new JobConfig();
		jobConfig.addClass(ProfileToPosition.class);
		jobConfig.setName(jobName);

		this.checkedSubmit(pipeline, jobConfig);
	}

	private Observable<HazelcastJsonValue> observableToWebsocket() {
		Observable<HazelcastJsonValue> observable = this.hazelcastInstance.getJet().newObservable();
		AtomicInteger count = new AtomicInteger(0);

		observable.addObserver(event -> {
			int i = count.getAndIncrement();
			if (i % MyConstants.LOG_EVERY == 0) {
				String message = String.format("%6d : Observed '%s'", i, event.toString());
                LOGGER.info(message);
			}
        
			this.simpMessagingTemplate.convertAndSend(DESTINATION, event.toString());
		});

		return observable;
	}

	private void launchPositionToWebsocket() {
		String jobName = MyConstants.IMAP_POSITION + "_FOR_" + this.hazelcastInstance.getName() + "_" + MyUtils.getNow();

		Observable<HazelcastJsonValue> observable = this.observableToWebsocket();

		Pipeline pipeline = PositionObserver.build(observable);

		JobConfig jobConfig = new JobConfig();
		jobConfig.addClass(PositionObserver.class);
		jobConfig.setName(jobName);

		this.checkedSubmit(pipeline, jobConfig);
	}

	private void checkedSubmit(Pipeline pipeline, JobConfig jobConfig) {
		jobConfig.setName(jobConfig.getName() + System.currentTimeMillis());
		jobConfig.setMetricsEnabled(true);
		LOGGER.info("Submitting '{}'", jobConfig.getName());
		Collection<Job> jobs = this.hazelcastInstance.getJet().getJobs();

		for (Job job : jobs) {
			if (jobConfig.getName().equals(job.getName())) {
				LOGGER.error("Not submitting '{}', job already exists '{}' with status: {}", jobConfig.getName(), job, job.getStatus());
				return;
			}
		};

		Job job = this.hazelcastInstance.getJet().newJobIfAbsent(pipeline, jobConfig);
		LOGGER.info("Submitted '{}'", job);
	}

	private void logJobs() {
		LOGGER.info("----- JOBS:");
		Collection<Job> jobs = this.hazelcastInstance.getJet().getJobs();
		try {
			Collection<String> names = jobs.stream()
                        .map(Job::getName)
                        .collect(Collectors.toCollection(TreeSet::new));
        
			names.forEach(name -> {
                jobs.stream()
                .filter(job -> job.getName().equals(name))
                .forEach(job -> {
                        JobStatus jobStatus = job.getStatus();
                        LOGGER.info("{} {}", name, jobStatus);
                });
			});
        
		} catch (Exception e) {
			String message = String.format("logJobs(): %s", Objects.toString(e.getMessage()));
			LOGGER.error(message);
		}
		LOGGER.info("-----");
	}
}
