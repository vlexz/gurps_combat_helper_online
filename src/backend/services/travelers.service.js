'use strict';

var
    express = require('express'),
    mongo = require('../mongo.js'),
    log = require('log4js').getLogger('travel'),
    ObjectId = require('mongodb').ObjectID;

function save_party(req, resp)
{
    var party = {
        user: req.user._id,
        name: req.body.name,
        travelers: req.body.travelers
    }
    mongo.db.collection('travel_parties')
    .insertOne(party, function(err, result){
        party._id = result.insertedId;        
        if(err) {
            resp.send({
                status: 'fail',
                error: err
            })
        } else {
            resp.send(party);
        }
    });
}

function get_all_parties(req, resp) {
    var cursor = mongo.db.collection('travel_parties')
        .find({user: req.user._id})
    var result = [];
    cursor.each(function(err, party){
        if(party) {
            result.push(party);
        } else {
            resp.send(result);
        }
    })
}

function get_one(req, resp) {
    mongo.db.collection('travel_parties')
        .findOne({_id: new ObjectId(req.body.id)}, 
            function(err, party){
                resp.send(party);
            })
}

function remove_party(req, resp) {   
    log.info('remove party', req.body);
    mongo.db.collection('travel_parties')
        .deleteOne({_id: new ObjectId(req.body.id)}, 
        function(err, result){
            if(err) {
                resp.send({status:'fail', error: err});
            } else {
                resp.send({status:'ok'});
            }
        });
}

module.exports = {
    init: function(app) {        
    },
    router: function(auth) {
        var router = express.Router();
        router.post('/api/travel/save_party', auth.user, save_party);
        router.get('/api/travel/get_parties', auth.user, get_all_parties);
        router.post('/api/travel/remove_party', auth.user, remove_party);
        router.post('/api/travel/get_party', auth.user, get_one);
        return router;
    }
}