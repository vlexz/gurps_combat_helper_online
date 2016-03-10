'use strict';

var 
    sequelize = require('sequelize'),
    log = require('log4js').getLogger('database'),
    config = require('getconfig');

var db = new sequelize(config.db.database, config.db.user, config.db.password, {
    host: config.db.host,
    dialect: 'mysql',
    logging: function(message) {
        log.debug(message)
    }
})

var _users = db.define('users', {
    name: sequelize.STRING,
    password: sequelize.STRING
}, {
    tableName: 'users',
    underscored: true
});

db.sync();

module.exports = {
    db: db,
    users: _users
}