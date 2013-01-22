/**
 * Copyright (c) 2011 Yahoo! Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See accompanying LICENSE file.
 */
package com.yahoo.omid.notifications.client;

import java.util.HashMap;
import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import com.google.common.util.concurrent.AbstractIdleService;
import com.yahoo.omid.notifications.Interest;

public class ObserverRegistrationService extends AbstractIdleService {
    
    private ActorSystem observersSystem = ActorSystem.create("ObserversSystem");
    
    // The main structure shared by the InterestRecorder and NotificiationListener services in order to register and
    // notify observers
    // Key: The name of a registered observer
    // Value: The TransactionalObserver infrastructure that delegates on the implementation of the ObserverBehaviour
    final Map<String, ActorRef> registeredObservers = new HashMap<String, ActorRef>();
    
    final InterestRecorder interestRecorder = new InterestRecorder(observersSystem, registeredObservers);
    final NotificationManager notificationManager = new NotificationManager(registeredObservers);
    

    public void registerObserverInterest(String obsName, ObserverBehaviour obsBehaviour, Interest interest) throws Exception {
        interestRecorder.registerObserverInterest(obsName, obsBehaviour, interest); // Delegate
    }
    
    public void deregisterObserverInterest(String obsName, Interest interest) throws Exception {
        interestRecorder.deregisterObserverInterest(obsName, interest); // Delegate
    }
    
    /* (non-Javadoc)
     * @see com.google.common.util.concurrent.AbstractIdleService#startUp()
     */
    @Override
    protected void startUp() throws Exception {
        notificationManager.startAndWait();
        interestRecorder.startAndWait();
    }

    /* (non-Javadoc)
     * @see com.google.common.util.concurrent.AbstractIdleService#shutDown()
     */
    @Override
    protected void shutDown() throws Exception {
        interestRecorder.stopAndWait();
        notificationManager.stopAndWait();
    }

}
