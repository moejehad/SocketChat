var socketIO = require('socket.io'),
    http = require('http'),
    port = process.env.PORT || 6000,
    ip = process.env.IP || '10.10.10.18', //My IP address. I try to "127.0.0.1" but it the same => don't run
    server = http.createServer().listen(port, ip, function() {
        console.log("IP = " , ip);
        console.log("start socket successfully");
});

io = socketIO.listen(server);
//io.set('match origin protocol', true);
io.set('origins', '*:*');

var usersGroupsArray = []; 
var userList = [];

var run = function(socket){
    // socket.broadcast.emit("message", "hello");

    socket.on("register", function(id,value) {
        userList.push(value);
        io.emit("register",id, value);
        console.log(value);
    });

    socket.on("login", function(id,bool,value) {
        for(let i = 0; i < userList.length; i++){
            if(userList[i]['email'] == value['LoginEmail'] && userList[i]['password'] == value['LoginPass']){
                io.emit("login",id,true ,userList[i]);
                console.log(userList[i]);
                break;
            }else if(i == userList.length-1){
                io.emit("login",id,false );
            }
        }
    });


    socket.on("allUsers",function(value){
        io.emit("allUsers",userList);
    });
    
    socket.on("msg",function(id,value){
        io.emit("msg",id,value);
    });

    socket.on("groupChat",function(id,value){
        io.emit("groupChat",id,value);
    });

    socket.on("AddGroup", function(value) {
        usersGroupsArray.push(value);
        console.log(usersGroupsArray);
    });

    socket.on("AllGroup",function(value){
        io.emit("AllGroup",usersGroupsArray);
    });

    socket.on("updateOnline", function(value) {
        for(let i = 0; i < userList.length; i++){
            if(userList[i]['id'] == value['id']){
                userList[i]['isOnline'] = value['isOnline'];
                io.emit("allUsers", usersGroupsArray);
                break;
            }
        }
    });

    

}

io.on('connection', run);