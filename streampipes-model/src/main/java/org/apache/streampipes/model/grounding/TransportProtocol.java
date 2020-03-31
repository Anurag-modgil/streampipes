/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.model.grounding;

import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.util.Cloner;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

@RdfsClass(StreamPipes.TRANSPORT_PROTOCOL)
@Entity
@MappedSuperclass
public abstract class TransportProtocol extends UnnamedStreamPipesEntity {
	
	private static final long serialVersionUID = 7625791395504335184L;

	@RdfProperty(StreamPipes.BROKER_HOSTNAME)
	private String brokerHostname;

	@OneToOne(fetch = FetchType.EAGER,cascade = {CascadeType.ALL})
	@RdfProperty(StreamPipes.TOPIC)
	private TopicDefinition topicDefinition;
	
	public TransportProtocol() {
		super();
	}
	
	public TransportProtocol(String hostname, TopicDefinition topicDefinition)
	{
		super();
		this.brokerHostname = hostname;
		this.topicDefinition = topicDefinition;
	}

	public TransportProtocol(TransportProtocol other) {
		super(other);
		this.brokerHostname = other.getBrokerHostname();
		if (other.getTopicDefinition() != null) {
			this.topicDefinition = new Cloner().topicDefinition(other.getTopicDefinition());
		}
	}

	public String getBrokerHostname() {
		return brokerHostname;
	}

	public void setBrokerHostname(String uri) {
		this.brokerHostname = uri;
	}
	
	public TopicDefinition getTopicDefinition() {
		return topicDefinition;
	}

	public void setTopicDefinition(TopicDefinition topicDefinition) {
		this.topicDefinition = topicDefinition;
	}

//	// TODO only kept for backwards compatibility, remove later
//	@Deprecated
//	public String getTopicName() {
//		return topicDefinition.getActualTopicName();
//	}
//
//	@Deprecated
//	public void setTopicName(String topicName) {
//		if (this.topicDefinition == null) {
//			this.topicDefinition = new SimpleTopicDefinition();
//		}
//		this.topicDefinition.setActualTopicName(topicName);
//	}

}
