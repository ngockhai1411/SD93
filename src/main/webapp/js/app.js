firebase.initializeApp({
    apiKey: "AIzaSyDnWo-5gTffzWzrejrFLHMOeEd2iQ-mocQ",
    authDomain: "spring-boot-notification-7710e.firebaseapp.com",
    projectId: "spring-boot-notification-7710e",
    storageBucket: "spring-boot-notification-7710e.appspot.com",
    messagingSenderId: "610062305282",
    appId: "1:610062305282:web:ad54d7272a4ea8b6d97f1b",
    measurementId: "G-WH7YNTDKRJ"
});

const messaging = firebase.messaging();

messaging.requestPermission();

messaging.getToken(messaging, { vapidKey: 'AIzaSyDnWo-5gTffzWzrejrFLHMOeEd2iQ-mocQ' }).then((currentToken) => {
    if (currentToken) {
        tokenFcm = currentToken;
        console.log("token you: "+currentToken)
    } else {
        console.log('No registration token available. Request permission to generate one.');
    }
}).catch((err) => {
    console.log('An error occurred while retrieving token. ', err);
});
console.log(messaging)
messaging.onMessage(function(payload) {
    alert("Foreground message fired!")
    console.log("Message received. ", payload);
});