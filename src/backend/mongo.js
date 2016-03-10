'use strict';

var 
    client = require('mongodb').MongoClient,
    config = require('getconfig'),
    promise = require('promise');

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
    }
}

module.exports = exports;
