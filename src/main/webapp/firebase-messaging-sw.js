importScripts('https://www.gstatic.com/firebasejs/8.1.1/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/8.1.1/firebase-messaging.js');

const firebaseConfig = {
    apiKey: "AIzaSyDnWo-5gTffzWzrejrFLHMOeEd2iQ-mocQ",
    authDomain: "spring-boot-notification-7710e.firebaseapp.com",
    projectId: "spring-boot-notification-7710e",
    storageBucket: "spring-boot-notification-7710e.appspot.com",
    messagingSenderId: "610062305282",
    appId: "1:610062305282:web:ad54d7272a4ea8b6d97f1b",
    measurementId: "G-WH7YNTDKRJ"
};
firebase.initializeApp(firebaseConfig);

firebase.messaging();

firebase.messaging().onBackgroundMessage(function (payload) {
    console.log('Received background message ', payload);
    const notificationTitle = payload.notification.title;
    const notificationOptions = {
        body: payload.notification.body,
        icon: 'http://localhost:8080/image/logo.png',
    };
    return self.registration.showNotification(notificationTitle, notificationOptions);
});



