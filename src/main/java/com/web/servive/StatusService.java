package com.web.servive;

import com.web.entity.Status;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StatusService {

    public List<Status> findAll();
}
