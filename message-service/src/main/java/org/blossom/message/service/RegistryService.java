package org.blossom.message.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
public class RegistryService {
    @Autowired
    private SimpUserRegistry userRegistry;

    public boolean checkIfUserHasOpenConnection(String username) {
        SimpUser user = userRegistry.getUser(username);
        if (user == null) {
            return false;
        }
        return user.hasSessions();
    }
}
