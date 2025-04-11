package com.web.serviceImp;

import com.web.entity.Status;
import com.web.repository.StatusRepository;
import com.web.servive.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StatusServiceImp implements StatusService {

    @Autowired
    private StatusRepository statusRepository;

    @Override
    public List<Status> findAll() {
        return statusRepository.findAll();
    }
}
