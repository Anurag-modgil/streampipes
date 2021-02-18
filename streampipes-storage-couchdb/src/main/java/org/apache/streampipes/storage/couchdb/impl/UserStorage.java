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

package org.apache.streampipes.storage.couchdb.impl;

import org.apache.streampipes.model.client.user.User;
import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.couchdb.dao.AbstractDao;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User Storage.
 * Handles operations on user including user-specified pipelines.
 *
 *
 */
public class UserStorage extends AbstractDao<User> implements IUserStorage {

    Logger LOG = LoggerFactory.getLogger(UserStorage.class);

    public UserStorage() {
        super(Utils::getCouchDbUserClient, User.class);
    }

    @Override
    public List<User> getAllUsers()
    {
      List<User> users = findAll();
    	return new ArrayList<>(users);
    }

    @Override
    public User getUser(String email) {
        // TODO improve
        CouchDbClient couchDbClient = couchDbClientSupplier.get();
        List<User> users = couchDbClient.view("users/username").key(email).includeDocs(true).query(User.class);
        if (users.size() != 1) {
            LOG.error("None or to many users with matching username");
        }
        return users.get(0);
    }

    @Override
    public void storeUser(User user) {
        persist(user);
    }

    @Override
    public void updateUser(User user) {
        update(user);
    }

    @Override
    public boolean emailExists(String email)
    {
    	List<User> users = findAll();
    	return users
                .stream()
                .filter(u -> u.getEmail() != null)
                .anyMatch(u -> u.getEmail().equals(email));
    }

    /**
    *
    * @param username
    * @return True if user exists exactly once, false otherwise
    */
   @Override
   public boolean checkUser(String username) {
       List<User> users = couchDbClientSupplier
               .get()
               .view("users/username")
               .key(username)
               .includeDocs(true)
               .query(User.class);

       return users.size() == 1;
   }

}
