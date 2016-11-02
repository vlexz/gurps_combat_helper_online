angular.module('GurpsCombatHelper')
.controller('CharlistCtrl', ['$scope', '$uibModal', 'UsersService', '$http',
function($scope, $uibModal, UsersService, $http) {
    $scope.$on('user_changed', function(event, user) {
        $scope.user = user;
        console.log(user);
    });

    $scope.character = {
        primaryStats: {
            ST: {value: 10, base: 10, per_level: 10, points: 0, modifiers: []},
            DX: {value: 10, base: 10, per_level: 20, points: 0, modifiers: []},
            IQ: {value: 10, base: 10, per_level: 20, points: 0, modifiers: []},
            HT: {value: 10, base: 10, per_level: 10, points: 0, modifiers: []}
        },
        secondaryStats: {
            Will: {value: 10, baseStat: null, per_level: 5, points: 0},
            Per: {value: 10, baseStat: null, per_level: 5, points: 0},
            HP: {value: 10, baseStat: 'ST', per_level: 2, points: 0},
            FP: {value: 10, baseStat: 'HT', per_level: 3, points: 0}
        }
    }


    $scope.primaryStatChanged = function() {

    }
}])