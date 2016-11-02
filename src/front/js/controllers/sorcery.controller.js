angular.module('GurpsCombatHelper')
.controller('SorceryCtrl', ['$scope', '$uibModal', 'UsersService', '$http',
function($scope, $uibModal, UsersService, $http) {
    $scope.$on('user_changed', function(event, user) {
        $scope.user = user;
        console.log(user);
    });

    var schools = [];
    var keywords = [];

    $scope.spells = [];
    

    $scope.schools_filter = "";


    function update() {
        $http.get('/api/sorcery/spells')
        .then(function(response){
            console.log(response.data);
            $scope.spells = response.data;
            var sch_idx = {}, key_idx = {}
            response.data.forEach(function(spell) {
                spell.schools.forEach(function(item){
                    if(! (item in sch_idx)) {
                        schools.push(item);
                        sch_idx[item] = true;
                    }
                });
                spell.keywords.forEach(function(item){
                    if(! (item in sch_idx)) {
                        keywords.push(item);
                        key_idx[item] = true;
                    }
                })
            })
        })
    }

    $scope.filter = function(spells) {
        var haveSchoolFilter = $scope.schools_filter.length != 0 
                    && schools.indexOf($scope.schools_filter) != -1
        return spells.filter(function(spell) {
            if(!haveSchoolFilter) return true;
            return spell.schools.indexOf($scope.schools_filter) != -1;
        });
    }

    $scope.sort = function(field) {
        $scope.spells = $scope.spells.sort(function(s1, s2) {
            if(s1[field] < s2[field]) return 1;
            if(s1[field] > s2[field]) return 2;
            return 0;
        })        
    }

    update();
}])