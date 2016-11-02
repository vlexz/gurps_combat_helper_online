'use strict';

var
    express = require('express'),
    mongo = require('../mongo.js'),
    log = require('log4js').getLogger('armor'),
    ObjectId = require('mongodb').ObjectID;

function get_spells(req, resp) {
    mongo.all_from('spells', {})
    .then(spells => resp.send(spells));
}

module.exports = {
    init: function(app) {      
    },
    router: function(auth) {
        var router = express.Router();
        router.get('/api/sorcery/spells', get_spells);
        return router;
    }    
}