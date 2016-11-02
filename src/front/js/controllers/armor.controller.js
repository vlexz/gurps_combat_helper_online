
angular.module('GurpsCombatHelper')
.controller('ArmorCtrl', ['$scope', '$uibModal', 'UsersService', '$http',
function($scope, $uibModal, UsersService, $http) {
    $scope.$on('user_changed', function(event, user) {
        $scope.user = user;
        console.log(user);
    });
    $scope.categories = [];

    $scope.current_category = null;
    $scope.current_category_index = -1;

    function update_categories() {
        console.log('update categories');
        $http.get('/api/armor/categories')
        .then(function(response){
            $scope.categories = response.data;
            if($scope.categories && !$scope.current_category) {
                console.log('Select default category');
                $scope.select_category(0);
            }
            console.log($scope.categories);
        })
    }

    $scope.add_category = function(name) {
        console.log('add_category', name);
        if(name.length > 0) {
            $http.post('/api/armor/add_category', {name: name, itemCount: 0})
            .then(function(response){
                console.log(response.data);
                update_categories();
            })
        }
    }

    $scope.select_category = function(index) {
        $scope.current_category = $scope.categories[index]._id;
        console.log($scope.current_category);
        $scope.current_category_index = index;  
        $http.post('/api/armor/armors', {category: $scope.current_category})
        .then(function(response) {
            $scope.armors = response.data;
        })
    }

    $scope.del_category = function(index) {
        if(index == $scope.current_category_index) {
            console.log('removing current category');
            $scope.current_category = null;
            $scope.current_category_index = -1;
        }
        $http.post('/api/armor/del_category', {id: $scope.categories[index]._id})
        .then(update_categories);
    }

    $scope.add_armor = function() {
        $uibModal.open({
            templateUrl: '/templates/add_armor.html',
            controller: 'ArmorAddCtrl',
            resolve: {
                params: function() {
                    return {
                        category: $scope.current_category,
                        locations: []
                    }
                }
            }
        }).result.then(function(armor) {
            $http.post('/api/armor/add_armor', armor)
            .then(function(response){
                console.log(response.data);        
                update_categories();
                $scope.select_category($scope.current_category_index);
            })
        })
    }

    $scope.remove_armor = function(index) {
        $http.post('/api/armor/del_armor', {_id: $scope.armors[index]._id})
        .then(function(response){
            console.log(response.data);
            update_categories();
            $scope.select_category($scope.current_category_index);
        })
    }

    $scope.edit_armor = function(index) {
        $uibModal.open({
            templateUrl: '/templates/add_armor.html',
            controller: 'ArmorAddCtrl',
            resolve: {
                params: function() {
                    return $scope.armors[index];
                }
            }
        }).result.then(function(armor){
            $http.post('/api/armor/update_armor', armor)
            .then(function(response){
                console.log(response);
                $scope.select_category($scope.current_category_index);
            })
        })
    }

    update_categories();
}]);

angular.module('GurpsCombatHelper')
.controller('ArmorAddCtrl', ['$scope', '$uibModalInstance', 'params', 'LookupTables',
function($scope, $uibModalInstance, params, LookupTables) {

    $scope.locations = LookupTables.locations;

    $scope.armor_loc = 'torso';

    $scope.armor = params;

    $scope.add_location = function(loc) {
        console.log(loc);
        if($scope.armor.locations.indexOf(loc) == -1){
            $scope.armor.locations.push(loc);
        }
    }

    $scope.remove_location = function(index) {
        $scope.armor.locations.splice(index, 1);
    }

    $scope.ok = function () {
        $uibModalInstance.close($scope.armor);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}]);