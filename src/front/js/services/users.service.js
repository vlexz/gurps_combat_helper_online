angular.module('GurpsCombatHelper')
.service('UsersService', ['$http', '$q', '$rootScope',
function($http, $q, $rootScope) {
    var user = {
        loggedIn: false
    }    

    function update_user() {
        $http.get('/api/users/get_user')
        .then(function(data) {
            if(data.data.id) {
                user.loggedIn = true;
                user.id = data.data.id;
            } else {
                user.loggedIn = false;
            }            
            $rootScope.$broadcast('user_changed', user);
        })
    }

    function check_username(username) {
        return $q(function(resolve, reject){
            $http.post('/api/users/check_username', {username: username})
            .then(function(response){
                resolve(response.data.exists);
            });
        });
    }

    function register(username, password) {
        return $q(function(resolve, reject){
            $http.post('/api/users/register', {username: username, password: password})
            .then(function(response) {
                resolve(response.data);
            })
        });
    }

    function login(username, password) {
        return $q(function(resolve, reject){
            console.log('loggin in');
            $http.post('/api/users/auth', {username: username, password: password})
            .then(function(response) {                
                console.log(response.data);
                resolve();
                update_user();
            });
        })
    }

    function logout() {
        return $q(function(resolve, reject){
            console.log('logout');
            $http.get('/api/users/logout')
            .then(function(){
                resolve();
                update_user();
            })
        })
    }

    update_user();


    return {
        user: user,
        exists: check_username,
        register: register,
        login: login,
        logout: logout
    }
}]);