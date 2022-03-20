# SocketChat

simple chat app using socket.io and Android. Created in 12-5-2021

### Project Features :
* MVVM architecture
* Single Activity 
* DataBinding
* Coroutines
* Navigation Componenet
* Kotlin
* Node Js
* Gson
* ssp && sdp

1.installing the Socket.IO Dependencies to build.gradle:

```
dependencies {
    ...
    implementation 'com.github.nkzawa:socket.io-client:0.6.0'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
}
```

2.add internet permission to AndroidManifest.xml.
```
<uses-permission android:name="android.permission.INTERNET"/>
```

3.Create Server using NodeJS
```
var socketIO = require('socket.io'),
    http = require('http'),
    port = process.env.PORT || 6000, // Port
    ip = process.env.IP || '10.10.10.18', // My IP address
    server = http.createServer().listen(port, ip, function() {
        console.log("IP = " , ip);
        console.log("start socket successfully");
});

io = socketIO.listen(server);
//io.set('match origin protocol', true);
io.set('origins', '*:*');
```

4. Run the server from cmd
```
npm start
```

5. To Know your IP address run this in the cmd 
```
ipconfig
```


