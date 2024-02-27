package com.noken29.svrbe.api;

import com.noken29.svrbe.domain.RoutingSession;
import com.noken29.svrbe.domain.view.RoutingSessionInfo;
import com.noken29.svrbe.domain.bean.RoutingSessionBean;
import com.noken29.svrbe.domain.view.RoutingSessionView;

import java.util.List;

public interface RoutingSessionAPI {

    RoutingSessionView getById(Long id);

    RoutingSessionView create(RoutingSessionBean bean);

    RoutingSessionView update(Long routingSessionId, RoutingSessionBean bean);

    List<RoutingSessionInfo> getInfo();

}
