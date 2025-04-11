package com.web.api;

import com.web.entity.Material;
import com.web.entity.Sole;
import com.web.repository.MaterialRepository;
import com.web.repository.SoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sole")
@CrossOrigin
public class SoleApi {

    @Autowired
    private SoleRepository soleRepository;

    @GetMapping("/public/all")
    public ResponseEntity<?> findAll() {
        List<Sole> result = soleRepository.findAll();
        return new ResponseEntity(result, HttpStatus.CREATED);
    }


    @PostMapping("/admin/create")
    public ResponseEntity<?> save(@RequestBody Sole sole){
        Sole result = soleRepository.save(sole);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/delete")
    public ResponseEntity<?> delete(@RequestParam("id") Long id){
        soleRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/public/findById")
    public ResponseEntity<?> findById(@RequestParam("id") Long id){
        Sole result = soleRepository.findById(id).get();
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
