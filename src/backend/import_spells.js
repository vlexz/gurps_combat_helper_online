'use strict';
var 
    mongo = require('./mongo.js'),
    parse = require('csv-parse'),
    async = require('async'),
    fs = require('fs');

console.log('hello', process.argv);

mongo.connect().then(function() {
    console.log('mongo connected, import spells from', process.argv[2]);
    fs.exists(process.argv[2], function(exists) {
        if(exists) {
            console.log('file exists, parsing...')
            fs.createReadStream(process.argv[2]).pipe(
                parse({delimiter: ';'}, function(err, data) {            
                    console.log('csv parsed');
                    data.shift();
                    async.eachSeries(data, function(line, next){
                        mongo.add('spells', {
                            name: line[0],
                            schools: line[1].replace(/\W/g, ' ').split(/\s+/).map(s => s.toLowerCase()),
                            keywords: line[2].replace(/\W/g, ' ').split(/\s+/).map(s => s.toLowerCase()),
                            sorcery_level: line[3],
                            casting_roll: line[4],
                            range: line[5],
                            duration: line[6],
                            description: line[7],
                            stats: line[8],
                            full_cost: line[10],
                            low_mana: line[11]
                        }).then(() => next(null))
                    }, function() {process.exit(); })
                })
            )
        } else {
            process.exit();
        }
    })
})