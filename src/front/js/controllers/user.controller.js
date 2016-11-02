angular.module('GurpsCombatHelper')
.controller('UsersCtrl', ['$scope', 'UsersService', '$uibModal',
function($scope, UsersService, $uibModal) {

    $scope.$on('user_changed', function(event, user) {
        $scope.user = user;
    });

    $scope.register = function() {
        $uibModal.open({
            templateUrl: '/templates/register.html',
            controller: 'UsersRegisterCtrl'
        })   
    }

    $scope.login = function() {
        $uibModal.open({
            templateUrl: '/templates/login.html',
            controller: 'UsersLoginCtrl'
        })
    }

    $scope.logout = function() {
        UsersService.logout();
    }

}]);

angular.module('GurpsCombatHelper')
.controller('UsersLoginCtrl', ['$scope', 'UsersService', '$uibModalInstance',
function($scope, UsersService, $uibModalInstance) {    

    $scope.user = {}

    $scope.user.username = '';
    $scope.user.password = '';

    $scope.ok = function () {      
        UsersService.login($scope.user.username, $scope.user.password)
        .then(function(){
            $uibModalInstance.close();
        })         
    };

    $scope.cancel = function() {
        $uibModalInstance.dismiss('cancel');
    }
}]);

angular.module('GurpsCombatHelper')
.controller('UsersRegisterCtrl', ['$scope', 'UsersService', '$uibModalInstance',
function($scope, UsersService, $uibModalInstance) {   
    $scope.user = {} 
    $scope.user.username = '';
    $scope.user.password = '';
    $scope.user.password_confirm = '';

    $scope.password_mismatch = false;
    $scope.user_exists = false;

    $scope.check_password = function() {
        if($scope.user.password != $scope.user.password_confirm) {
            $scope.password_mismatch = true;
        } else {
            $scope.password_mismatch = false;
        }
    }

    $scope.check_username = function() {
        UsersService.exists($scope.user.username)
        .then(function(exists) {
            $scope.user_exists = exists;
        });
    }

    $scope.ok = function () {       
        if (!$scope.password_mismatch && !$scope.user_exists) {
            UsersService.register($scope.user.username, $scope.user.password)
            .then(function(response) {
                console.log(response);
                if(response.status == 'ok') {
                    $uibModalInstance.close();
                } else {
                    alert(response.data.message);
                }
            })
        }
    };

    $scope.cancel = function() {
        $uibModalInstance.dismiss('cancel');
    }

}]);