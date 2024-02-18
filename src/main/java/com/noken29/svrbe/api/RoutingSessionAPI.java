package com.noken29.svrbe.api;

import com.noken29.svrbe.domain.RoutingSession;
import com.noken29.svrbe.domain.RoutingSessionInfo;
import com.noken29.svrbe.domain.bean.RoutingSessionBean;

import java.util.List;

public interface RoutingSessionAPI {

    RoutingSession getById(Long id);

    RoutingSession create(RoutingSessionBean bean);

    RoutingSession update(Long routingSessionId, RoutingSessionBean bean);

    List<RoutingSessionInfo> getInfo();

}
