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

package org.apache.streampipes.connect.container.master;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.connect.container.master.management.AdapterMasterManagement;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterStreamDescription;
import org.apache.streampipes.storage.couchdb.impl.AdapterStorageImpl;

import java.util.Arrays;
import java.util.List;

public class AdapterMasterManagementTest {

    @Test(expected = AdapterException.class)
    public void getAdapterFailNull() throws AdapterException {
        AdapterStorageImpl adapterStorage = mock(AdapterStorageImpl.class);
        when(adapterStorage.getAllAdapters()).thenReturn(null);

        AdapterMasterManagement adapterMasterManagement = new AdapterMasterManagement();

        adapterMasterManagement.getAdapter("id2", adapterStorage);
    }

    @Test(expected = AdapterException.class)
    public void getAdapterFail() throws AdapterException {
        List<AdapterDescription> adapterDescriptions = Arrays.asList(new GenericAdapterStreamDescription());
        AdapterStorageImpl adapterStorage = mock(AdapterStorageImpl.class);
        when(adapterStorage.getAllAdapters()).thenReturn(adapterDescriptions);

        String id = "http://t.id";
        AdapterMasterManagement adapterMasterManagement = new AdapterMasterManagement();

        adapterMasterManagement.getAdapter("id2", adapterStorage);
    }

    @Test
    public void getAllAdaptersSuccess() throws AdapterException {
        List<AdapterDescription> adapterDescriptions = Arrays.asList(new GenericAdapterStreamDescription());
        AdapterStorageImpl adapterStorage = mock(AdapterStorageImpl.class);
        when(adapterStorage.getAllAdapters()).thenReturn(adapterDescriptions);

        AdapterMasterManagement adapterMasterManagement = new AdapterMasterManagement();

        List<AdapterDescription> result = adapterMasterManagement.getAllAdapters(adapterStorage);

        assertEquals(1, result.size());
        assertEquals(GenericAdapterStreamDescription.ID, result.get(0).getUri());
    }

    @Test(expected = AdapterException.class)
    public void getAllAdaptersFail() throws AdapterException {
        AdapterStorageImpl adapterStorage = mock(AdapterStorageImpl.class);
        when(adapterStorage.getAllAdapters()).thenReturn(null);

        AdapterMasterManagement adapterMasterManagement = new AdapterMasterManagement();

        adapterMasterManagement.getAllAdapters(adapterStorage);

    }
}