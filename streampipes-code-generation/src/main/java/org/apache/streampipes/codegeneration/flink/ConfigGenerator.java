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

package org.apache.streampipes.codegeneration.flink;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.apache.streampipes.model.base.ConsumableStreamPipesEntity;
import org.apache.streampipes.codegeneration.Generator;
import org.apache.streampipes.codegeneration.utils.JFC;

import javax.lang.model.element.Modifier;

public class ConfigGenerator extends Generator {

	public ConfigGenerator(ConsumableStreamPipesEntity sepa, String name, String packageName) {
		super(sepa, name, packageName);
	}

	@Override
	public JavaFile build() {
		FieldSpec jar = FieldSpec.builder(JFC.STRING, "JAR_FILE")
			    .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
			    .initializer("$S" , "./" + name + "-0.66.0.jar")
			    .build();

		FieldSpec flinkHost = FieldSpec.builder(JFC.STRING, "FLINK_HOST")
			    .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
//			    .initializer("$T.INSTANCE.getFlinkHost()", JFC.CLIENT_CONFIGURATION)
			    .build();

		FieldSpec flinkPort = FieldSpec.builder(int.class, "FLINK_PORT")
			    .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
//			    .initializer("$T.INSTANCE.getFlinkPort()", JFC.CLIENT_CONFIGURATION)
			    .build();

		TypeSpec parameterClass = TypeSpec.classBuilder("Config").addModifiers(Modifier.PUBLIC)
				.addField(jar).addField(flinkHost).addField(flinkPort).build();

		return JavaFile.builder(packageName, parameterClass).build();
	}

}
