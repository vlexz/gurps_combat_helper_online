angular.module('GurpsCombatHelper')
.controller('CharactersCtrl', ['$scope', '$uibModal', 'UsersService', '$http',
function($scope, $uibModal, UsersService, $http) {
    $scope.$on('user_changed', function(event, user) {
        $scope.user = user;
        console.log(user);
    });

    $scope.characters = [];

    $scope.add_character = function(){
        $uibModal.open({
            templateUrl: '/templates/add_edit_character.html',
            controller: 'CharacterEditCtrl',
            resolve: {
                params: function() {
                    return {
                        title: 'Add Character',
                        character: {
                            st: 10,
                            dx: 10,
                            iq: 10,
                            ht: 10,
                            will: 10,
                            per: 10,
                            hp: 10,
                            fp: 10,
                            bs: 5,
                            move: 5,
                            dodge: 8,
                            parry: 0,
                            notes: ''                            
                        }
                    }
                }
            }
        }).result.then(function(char) {
            $scope.characters.push(char)
        });
    }

}]);

angular.module('GurpsCombatHelper')
.controller('CharacterEditCtrl', ['$scope', '$uibModalInstance', 'params', '$http',
function($scope, $uibModalInstance, params, $http) {
    
    $scope.title = params.title;

    $scope.character = params.character;

  
    $scope.ok = function () {
        $uibModalInstance.close($scope.character);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}]);