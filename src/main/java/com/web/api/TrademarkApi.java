package com.web.api;

import com.web.entity.Trademark;
import com.web.repository.TrademarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trademark")
@CrossOrigin
public class TrademarkApi {

    @Autowired
    private TrademarkRepository trademarkRepository;

    @GetMapping("/public/all")
    public ResponseEntity<?> findAll() {
        List<Trademark> result = trademarkRepository.findAll();
        return new ResponseEntity(result, HttpStatus.CREATED);
    }


    @PostMapping("/admin/create")
    public ResponseEntity<?> save(@RequestBody Trademark trademark){
        Trademark result = trademarkRepository.save(trademark);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/delete")
    public ResponseEntity<?> delete(@RequestParam("id") Long id){
        trademarkRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/public/findById")
    public ResponseEntity<?> findById(@RequestParam("id") Long id){
        Trademark result = trademarkRepository.findById(id).get();
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
