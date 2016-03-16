'use strict';

var
    express = require('express'),
    mongo = require('../mongo.js'),
    log = require('log4js').getLogger('travel'),
    ObjectId = require('mongodb').ObjectID;

function get_categories(req, resp) {
    var cursor = mongo.db.collection('armor_categories').find();
    var result = [];
    cursor.each(function(err, category) {
        if(category) {
            result.push(category);
        } else {
            resp.send(result);
        }
    });    
}

function add_category(req, resp) {
    mongo.db.collection('armor_categories')
    .insertOne(req.body, function(err, res) {
        if(!err){
            resp.send({status: 'ok'})
        } else {
            resp.send({status: 'fail', err: err});
        }
    })
}

function del_category(req, resp) {
    mongo.db.collection('armor_categories')
    .deleteOne({_id: new ObjectId(req.body.id)},
        function(err, res){
            if(!err) {
                resp.send({status: 'ok'});
            } else {
                resp.send({status: 'fail', err: err});
            }
        })
}

function get_armors(req, resp) {
    var cursor = mongo.db.collection('armors')
    .find({category: req.body.category});
    var result = [];
    cursor.each(function(err, armor) {
        if(armor) {
            result.push(armor);
        } else {
            resp.send(result);
        }
    });    
}

function add_armor(req, resp) {
    mongo.db.collection('armors')
    .insertOne(req.body, function(err, res){
        mongo.db.collection('armor_categories')
        .updateOne({_id: new ObjectId(req.body.category)}, {
            $inc: {itemCount: 1}
        }, function(err, res){
            resp.send({status:'ok'});
        })
    })
}

function update_armor(req, resp) {
    var id = new ObjectId(req.body._id);
    delete req.body._id;
    mongo.db.collection('armors')
    .update({_id: id}, req.body, function(err, res) {
        if(err) {
            resp.send({status: 'fail', err: err});
        } else {
            resp.send({status: 'ok'});
        }
    });
}

function del_armor(req, resp) {
    mongo.db.collection('armors')
    .findAndRemove({_id: new ObjectId(req.body._id)})
    .then((err, doc) => {
        if(doc){
            mongo.db.collection('armor_categories')
            .updateOne({_id: new ObjectId(doc.category)}, {$inc: {itemCount: -1}})
            .then(err, res => {
                resp.send({status: 'ok'});
            })
        } else {
            resp.send({status: 'fail', err: 'no such armor'});
        }
    })
}


module.exports = {
    init: function(app) {      
    },
    router: function(auth) {
        var router = express.Router();
        router.get('/api/armor/categories', get_categories);
        router.post('/api/armor/add_category', auth.user, add_category);
        router.post('/api/armor/del_category', auth.user, del_category);
        router.post('/api/armor/armors', get_armors);
        router.post('/api/armor/add_armor', auth.user, add_armor);
        router.post('/api/armor/del_armor', auth.user, del_armor);
        router.post('/api/armor/update_armor', auth.user, update_armor);
        return router;
    }    
}