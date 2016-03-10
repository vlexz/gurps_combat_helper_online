'use strict';

var
    express = require('express'),
    passport = require('passport'),
    localStrategy = require('passport-local').Strategy,
    config = require('getconfig'),
    log = require('log4js').getLogger('users'),
    db = require('../database.js'),
    cryp = require('crypto');

passport.use(new localStrategy(function(username, password, done){
    db.users.findOne({
        where: {
            name: username
        }
    }).then(user => {
        if(user) {
            var hash = cryp.createHash('sha256');
            hash.update(password);
            var d = hash.digest('hex');
            log.info(username, user.password, d, password);
            if(d == user.password) {
                return done(null, user.id);
            }
        }
        log.info('Incorrect password');
        done(null, false, {message: 'Incorrect username or password!'});
    })
}));

passport.serializeUser(function(id, done) {
    done(null, id);
})

passport.deserializeUser(function(id, done) {
    db.users.findOne({
        where: {
            id: id
        }
    }).then(user => {
        done(null, {
            id: user.id,
            name: user.name
        });
    })
})

function check_user(req, resp, next) {
    if(req.isAuthenticated()) {
        next();
    } else {
        resp.send({status: 'fail', message: 'invalid user or not authenticated'});
    }
}

function check_root(req, resp, next) {
    if(req.isAuthenticated() && req.username == 'root') {
        next();
    } else {
        resp.send({status: 'fail', message: 'invalid user or not authenticated'});
    }
}

function register(req, resp, next) {
    db.users.findOne({
        where: {
            name: req.body.username
        }
    }).then(user => {
        if(user) {
            return resp.send({status: 'fail', message: 'User already exists'});
        } else {
            var hash = cryp.createHash('sha256');
            hash.update(req.body.password);
            var d = hash.digest('hex');
            log.info('register with', req.body.password, d);
            db.users.create({                
                name: req.body.username,                
                password: d
            }).then(() => {
                resp.send({status: 'ok'});
            })
        }
    })
}

function check_username(req, resp) {
    db.users.findOne({
        where: {
            name: req.body.username
        }
    }).then(user => {
        if(user) {
            resp.send({exists: true});
        } else {
            resp.send({exists:false});
        }
    })
}

function getUser(req, resp, next) {
    resp.send(req.user);
}

function logout(req, resp) {
    req.logout();
    resp.send({status: 'ok'});
}

module.exports = {
    init: function(app) {    
        app.use(passport.initialize());
        app.use(passport.session());
        app.auth = {
            user: check_user,
            root: check_root
        }
    },
    router: function(auth) {
        // router.post('/api/login', )        
        var router = express.Router();        
        router.post('/api/users/register', register);
        router.post('/api/users/auth', passport.authenticate('local', {
            successRedirect: '/api/users/get_user',
            failureRedirect: '/api/users/get_user'
        }));
        router.get('/api/users/get_user', getUser);
        router.post('/api/users/check_username', check_username);
        router.get('/api/users/logout', logout);
        return router;
    }    
}