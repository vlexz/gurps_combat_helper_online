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



}]);

angular.module('GurpsCombatHelper')
.controller('UsersLoginCtrl', ['$scope', 'UsersService', '$uibModalInstance',
function($scope, UsersService, $uibModalInstance) {    

    $scope.username = '';
    $scope.password = '';

    $scope.ok = function () {      
        UsersService.login($scope.username, $scope.password)
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
    $scope.username = '';
    $scope.password = '';
    $scope.password_confirm = '';

    $scope.password_mismatch = false;
    $scope.user_exists = false;

    $scope.check_password = function() {
        if($scope.password != $scope.password_confirm) {
            $scope.password_mismatch = true;
        } else {
            $scope.password_mismatch = false;
        }
    }

    $scope.check_username = function() {
        UsersService.exists($scope.username)
        .then(function(exists) {
            $scope.user_exists = exists;
        });
    }

    $scope.ok = function () {       
        if (!$scope.password_mismatch && !$scope.user_exists) {
            UsersService.register($scope.username, $scope.password)
            .then(function(response) {
                console.log(response);
                if(response.status == 'ok') {
                    $uibModalInstance.close();
                } else {
                    alert(response.message);
                }
            })
        }
    };

    $scope.cancel = function() {
        $uibModalInstance.dismiss('cancel');
    }

}]);