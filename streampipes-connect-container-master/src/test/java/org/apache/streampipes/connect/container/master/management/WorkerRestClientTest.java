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

package org.apache.streampipes.connect.container.master.management;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.model.connect.adapter.GenericAdapterSetDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ WorkerRestClient.class })
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
public class WorkerRestClientTest {

    /**
     * Notes: In this class I tested how powermock could be used to mok static methods
     * One problem is to mock static methods that return void
     */

    @Before
    public void before() {
        PowerMockito.mockStatic(WorkerRestClient.class);
    }

    @Test
    public void invokeStreamAdapterSuccess() throws Exception {
        doNothing().when(WorkerRestClient.class, "startAdapter", anyString(), any());
        when(WorkerRestClient.class, "invokeStreamAdapter", anyString(), any()).thenCallRealMethod();

        WorkerRestClient.invokeStreamAdapter("", null);

        verifyStatic(WorkerRestClient.class, times(1));
        WorkerRestClient.startAdapter(eq("worker/stream/invoke"), any());
    }

    @Test(expected = AdapterException.class)
    public void invokeStreamAdapterFail() throws Exception {
        doThrow(new AdapterException()).when(WorkerRestClient.class, "startAdapter", anyString(), any());
        when(WorkerRestClient.class, "invokeStreamAdapter", anyString(), any()).thenCallRealMethod();

        WorkerRestClient.invokeStreamAdapter("", null);
    }

    @Test
    public void stopStreamAdapterSuccess() throws Exception {

        doNothing().when(WorkerRestClient.class, "stopAdapter", anyString(), any(), anyString());
        when(WorkerRestClient.class, "stopStreamAdapter", anyString(), any()).thenCallRealMethod();
        GenericAdapterStreamDescription description = new GenericAdapterStreamDescription();
        description.setId("id1");

        WorkerRestClient.stopStreamAdapter("", description);

        verifyStatic(WorkerRestClient.class, times(1));
        WorkerRestClient.stopAdapter(anyString(), any(), eq("worker/stream/stop"));

    }

    @Test(expected = AdapterException.class)
    public void stopStreamAdapterFail() throws Exception {
        doThrow(new AdapterException()).when(WorkerRestClient.class, "stopAdapter", anyString(), any(), anyString());
        when(WorkerRestClient.class, "stopStreamAdapter", anyString(), any()).thenCallRealMethod();

        GenericAdapterStreamDescription description = new GenericAdapterStreamDescription();
        description.setId("id1");

        WorkerRestClient.stopStreamAdapter("", description);

    }

     @Test
    public void invokeSetAdapterSuccess() throws Exception {

        doNothing().when(WorkerRestClient.class, "startAdapter", anyString(), any());
        when(WorkerRestClient.class, "invokeSetAdapter", anyString(), any()).thenCallRealMethod();

        GenericAdapterSetDescription description = new GenericAdapterSetDescription();
        description.setId("id1");
        WorkerRestClient.invokeSetAdapter("", description);

        verifyStatic(WorkerRestClient.class, times(1));
        WorkerRestClient.startAdapter(eq("worker/set/invoke"), any());

    }

    @Test(expected = AdapterException.class)
    public void invokeSetAdapterFail() throws Exception {
        doThrow(new AdapterException()).when(WorkerRestClient.class, "startAdapter", anyString(), any());
        when(WorkerRestClient.class, "invokeSetAdapter", anyString(), any()).thenCallRealMethod();

        WorkerRestClient.invokeSetAdapter("", null);
    }

    @Test
    public void stopSetAdapterSuccess() throws Exception {

        doNothing().when(WorkerRestClient.class, "stopAdapter", anyString(), any(), anyString());
        when(WorkerRestClient.class, "stopSetAdapter", anyString(), any()).thenCallRealMethod();

        GenericAdapterSetDescription description = new GenericAdapterSetDescription();
        description.setId("id1");
        WorkerRestClient.stopSetAdapter("", description);

        verifyStatic(WorkerRestClient.class, times(1));
        WorkerRestClient.stopAdapter(anyString(), any(), eq("worker/set/stop"));

    }

    @Test(expected = AdapterException.class)
    public void stopSetAdapterFail() throws Exception {
        doThrow(new AdapterException()).when(WorkerRestClient.class, "stopAdapter", anyString(), any(), anyString());
        when(WorkerRestClient.class, "stopSetAdapter", anyString(), any()).thenCallRealMethod();

        GenericAdapterSetDescription description = new GenericAdapterSetDescription();
        description.setId("id1");
        WorkerRestClient.stopSetAdapter("", description);

    }

}