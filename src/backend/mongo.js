'use strict';

var 
    client = require('mongodb').MongoClient,
    ObjectId = require('mongodb').ObjectID,
    config = require('getconfig'),
    promise = require('promise'),
    log = require('log4js').getLogger('mongo');


var exports = {
    db: null,
    connect: function() {
        return new promise(function(resolve, reject){
            client.connect(config.mongodb, function(err, db){
                if(err) {
                    console.log(err);
                    reject(err);
                } else {
                    exports.db = db;
                    resolve();
                }
            })
        })
    },
    all_from: function(collection, query) {
        return new promise(function(resolve, reject){
            var cursor = exports.db.collection(collection).find(query);
            var result = [];
            cursor.each(function(err, doc){
                if(err) log.info(err);
                if(doc) {
                    log.info('got object', doc._id);
                    result.push(doc);
                } else {
                    resolve(result);
                }
            });
        });        
    },
    add: function(collection, object) {
        return new promise(function(resolve, reject){
            exports.db.collection(collection)
            .insertOne(object, function(err, result){
                if (err) {
                    resolve({status: 'fail', err: err});
                } else {
                    object._id = result.insertedId;
                    resolve(object);
                }
            })
        })
    },
    del: function(collection, id) {
        return new promise(function(resolve, reject){
            exports.db.collection(collection)
            .deleteOne({_id: new ObjectId(id)},
                function(err, res){
                    resolve({status:'ok'});
                })
        })
    },
    replace: function(collection, object) {
        return new promise(function(resolve, reject){            
            var id = object._id;
            delete object._id;
            exports.db.collection(collection)
            .updateOne({_id: new ObjectId(id)}, object, 
                function(err, res){
                    if(err){
                        resolve({status: 'fail', err: err});
                    } else {
                        resovle({status: 'ok'});
                    }
                })
        })
    },
    // add_handler: function(collection, user_specific) {
    //     return function(req, resp) {
    //         object = req.body;
    //         if(user_specific) {
    //             object.user = req.user.id;
    //         }
    //         exports.add(collection, object)
    //         .then()
    //     }
    // }
}

module.exports = exports;
