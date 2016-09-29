
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
        $http.get('/api/armor/categories')
        .then(function(response){
            $scope.categories = response.data;
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

    $scope.ok = function () {
        $uibModalInstance.close($scope.armor);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}]);