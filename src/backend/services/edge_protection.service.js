'use strict';
var
    express = require('express'),
    mongo = require('../mongo.js'),
    log = require('log4js').getLogger('travel'),
    ObjectId = require('mongodb').ObjectID;

function combatants(req, resp) {   
    mongo.all_from('combatants', {user: req.user.id})
    .then(result => resp.send(result));    
}

function add_combatant(req, resp){ 
    req.body.user = req.user._id;
    mongo.add('combatants', req.body)
    .then(status => { resp.send(status)});   
}

function del_combatant(req, resp) {    
    mongo.del('combatants', req.body.id)
    .then(status => { resp.send(status)})
}

function combats(req, resp) {    
    mongo.all_from('combats', {user: req.user._id})
    .then(result => resp.send(result));        
}

function add_combat(req, resp) {    
    req.body.user = req.user._id;
    mongo.add('combats', req.body)
    .then(status => {resp.send(status)})
}

function del_combat(req, resp) {    
    mongo.del('combats', req.body.id)
    .then(status => {resp.send(status)})
}

function update_combat(req, resp) {    
    mongo.replace('combats', req.body)
    .then(status => {resp.send(status)})   
}

module.exports = {
    init: function(app) {      
    },
    router: function(auth) {
        var router = express.Router();
        router.post('/api/ep/combatants', auth.user, combatants);
        router.post('/api/ep/add_combatant', auth.user, add_combatant);
        router.post('/api/ep/del_combatant', auth.user, del_combatant);
        router.post('/api/ep/combats', auth.user, combats);
        router.post('/api/ep/add_combat', auth.user, add_combat);
        router.post('/api/ep/del_combat', auth.user, del_combat);
        router.post('/api/ep/update_combat', auth.user, update_combat);
        return router;
    }    
}