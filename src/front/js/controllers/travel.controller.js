'use strict';

var load_levels = ['None', 'Light', 'Medium', 'Heavy', 'Extra Heavy'];

angular.module('GurpsCombatHelper')
.controller('TravelCalculatorCtrl', ['$scope', '$uibModal', 'dices',
function($scope, $uibModal, dices) {

    $scope.travelers = []
    $scope.travel_time = 0;
    $scope.encounter = 0;
    $scope.hot_day = false;

    $scope.terrains = [
        "Horrible",
        "Bad",
        "Average",
        "Earth Road",
        "Good Road",
        "Excellent"
    ];

    var terrain_modifiers = {
        "Horrible": 0.2,
        "Bad": 0.5,
        "Average": 1,
        "Earth Road": 1,
        "Good Road": 1.25,
        "Good": 1.25  
    }

    $scope.terrain = "Average";

    $scope.weathers = [
        "Good",
        "Rain",
        "Snow",
        "Ice"
    ];

    $scope.weather = "Good";

    $scope.have_leader = false;

    $scope.hot_day = false;

    $scope.add_traveler = function() {
        var d = $uibModal.open({
            templateUrl: 'templates/add_traveler.html',
            controller: 'TravelCalculatorAddTravelerCtrl'
        }).result.then(function(traveler){
            $scope.travelers.push(traveler);
        });
    }

    $scope.travel_results = null;

    $scope.travel = function() {
        var hikingTime = $scope.travel_time;     
        var encounterHappend = false;   
        //check for encounter
        for(var i = 0; i < hikingTime; ++i) {
            if(dices.rollPercent() < $scope.encounter) {
                hikingTime = i + 1;
                encounterHappend = true;
                break;
            }   
        }

        //get max move
        var speed = $scope.travelers.reduce(function(current, t) {
            var move = t.move - load_levels.indexOf(t.load);
            return current < t.move ? current : t.move;
        }, 1000);

        //take hiking into account
        if($scope.have_leader) { //party has a leader, so checking average hiking
            var average_hiking = $scope.travelers.reduce(
                    function(current, t) { 
                        return current + t.hiking
                    }, 0) / $scope.travelers.length;
            if(dices.roll3d6() < average_hiking) {
                speed *= 1.2
            }
        } else { //each check his hking indiviually
            var allgood = true;
            $scope.travelers.forEach(function(t){
                if(dices.roll3d6() > t.hiking) allgood = false;
            })
            if(allgood) {
                speed *= 1.2;
            }
        }

        //terrain and weather
        var terrain_modifier = terrain_modifiers[$scope.terrain];
        if($scope.weather != "Good") {
            terrain_modifier /= 2;
        }

        if($scope.weather == "Rain" && $scope.terrain == "Earth Road") {
            terrain_modifier /= 5;
        }

        speed *= terrain_modifier;
        var fp_lose = $scope.travelers.map(function(person) {
            var fp_lose = (1 + load_levels.indexOf(person.load)) * hikingTime;
            if($scope.hot_day) fp_lose += 1;
            return {
                name: person.name, 
                fp_lost: fp_lose,
                fp_left: person.fp - fp_lose
            }
        });
        $uibModal.open({
            templateUrl: "templates/travel_results.html",
            controller: 'TravelCalculatorTravelResultsCtrl',
            resolve: {
                result: function() {
                    return {
                        encounter: encounterHappend,
                        time: hikingTime,
                        range: hikingTime * speed,
                        fp_lose: fp_lose
                    }
                }
            }
        })
    }
}]);

angular.module('GurpsCombatHelper')
.controller('TravelCalculatorAddTravelerCtrl', ['$scope', '$uibModalInstance',
function($scope, $uibModalInstance) {

    $scope.load_levels = load_levels;
    $scope.traveler = {
        name: '',
        move: 5,
        fp: 10,
        load: 'None',
        hiking: 5
    }
    
    $scope.ok = function () {
        $uibModalInstance.close($scope.traveler);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}]);

angular.module('GurpsCombatHelper')
.controller('TravelCalculatorTravelResultsCtrl', ['$scope', '$uibModalInstance', 'result',
function($scope, $uibModalInstance, result) {

    $scope.result = result;

    $scope.ok = function () {
        $uibModalInstance.close(null);
    };
}]);
