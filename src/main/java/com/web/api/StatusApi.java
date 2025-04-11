package com.web.api;

import com.web.entity.Status;
import com.web.servive.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/status")
@CrossOrigin
public class StatusApi {

    @Autowired
    private StatusService statusService;

    @GetMapping("/admin/all")
    private ResponseEntity<?> findAll(){
        List<Status> result = statusService.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
