package com.web.api;

import com.web.entity.Material;
import com.web.entity.Trademark;
import com.web.repository.MaterialRepository;
import com.web.repository.TrademarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/material")
@CrossOrigin
public class MaterialApi {

    @Autowired
    private MaterialRepository materialRepository;

    @GetMapping("/public/all")
    public ResponseEntity<?> findAll() {
        List<Material> result = materialRepository.findAll();
        return new ResponseEntity(result, HttpStatus.CREATED);
    }


    @PostMapping("/admin/create")
    public ResponseEntity<?> save(@RequestBody Material material){
        Material result = materialRepository.save(material);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/delete")
    public ResponseEntity<?> delete(@RequestParam("id") Long id){
        materialRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/public/findById")
    public ResponseEntity<?> findById(@RequestParam("id") Long id){
        Material result = materialRepository.findById(id).get();
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
