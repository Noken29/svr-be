package com.noken29.svrbe.api;

import com.noken29.svrbe.domain.RoutingSession;
import com.noken29.svrbe.domain.RoutingSessionInfo;
import com.noken29.svrbe.domain.bean.RoutingSessionBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routing-session")
public class RoutingSessionController {

    @Autowired
    private RoutingSessionAPI routingSessionAPI;

    @GetMapping("/{id}")
    public ResponseEntity<RoutingSession> get(@PathVariable Long id) {
        return new ResponseEntity<>(routingSessionAPI.getById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RoutingSession> create(@RequestBody RoutingSessionBean routingSessionBean) {
        return new ResponseEntity<>(routingSessionAPI.create(routingSessionBean), HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    public ResponseEntity<RoutingSession> update(@PathVariable Long id, @RequestBody RoutingSessionBean routingSessionBean) {
        return new ResponseEntity<>(routingSessionAPI.update(id, routingSessionBean), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoutingSessionInfo>> all() {
        return new ResponseEntity<>(routingSessionAPI.getInfo(), HttpStatus.OK);
    }

}
