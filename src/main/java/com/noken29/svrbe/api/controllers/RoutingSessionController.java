package com.noken29.svrbe.api.controllers;

import com.noken29.svrbe.api.RoutingSessionAPI;
import com.noken29.svrbe.domain.view.RoutingSessionInfo;
import com.noken29.svrbe.domain.bean.RoutingSessionBean;
import com.noken29.svrbe.domain.view.RoutingSessionView;
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
    public ResponseEntity<RoutingSessionView> get(@PathVariable Long id) {
        return new ResponseEntity<>(routingSessionAPI.getViewById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RoutingSessionView> create(@RequestBody RoutingSessionBean routingSessionBean) {
        return new ResponseEntity<>(routingSessionAPI.create(routingSessionBean), HttpStatus.CREATED);
    }

    @PostMapping("/{id}")
    public ResponseEntity<RoutingSessionView> update(@PathVariable Long id, @RequestBody RoutingSessionBean routingSessionBean) {
        return new ResponseEntity<>(routingSessionAPI.update(id, routingSessionBean), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoutingSessionInfo>> all() {
        return new ResponseEntity<>(routingSessionAPI.getInfo(), HttpStatus.OK);
    }

    @GetMapping("/{id}/routes")
    public ResponseEntity<Boolean> makeRoutes(@PathVariable Long id) {
        return new ResponseEntity<>(routingSessionAPI.makeRoutes(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/routes/status")
    public ResponseEntity<Boolean> jobIsFinished(@PathVariable Long id) {
        return new ResponseEntity<>(routingSessionAPI.jobIsFinished(id), HttpStatus.OK);
    }

}
