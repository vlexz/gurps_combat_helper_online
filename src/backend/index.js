
var 
    express = require('express'),
    bodyParser = require('body-parser'),
    session = require('express-session'),
    path = require('path'),
    morgan = require('morgan'),
    log = require('log4js').getLogger('app'),
    mongo = require('./mongo.js');


var app = express();

app.use(bodyParser.json({limit: '1mb'}));
app.use(session({
    secret: 'aslkjdeirewjbckjb;cjdabekfvwef',
    resave: true,
    saveUninitialized: true
}));
app.use(morgan('dev'));

var modules = [
    './services/users.service.js',
    './services/travelers.service.js',
    './services/armor.service.js'
];

app.auth = null;

modules.forEach(path => {
    var m = require(path);
    m.init(app);
    app.use(m.router(app.auth));
})

mongo.connect()
.then(function(){
    app.listen(3000);
    log.info('Listening 3000');    
})


