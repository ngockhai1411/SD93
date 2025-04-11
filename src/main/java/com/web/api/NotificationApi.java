package com.web.api;

import com.web.dto.request.PushNotificationRequest;
import com.web.firebase.PushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@CrossOrigin
public class NotificationApi {

    @Autowired
    private PushNotificationService pushNotificationService;

    @PostMapping("/send")
    public void send(@RequestBody PushNotificationRequest request){
        pushNotificationService.sendPushNotificationToToken(request);
        System.out.println("success");
    }
}
