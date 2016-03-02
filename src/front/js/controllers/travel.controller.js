'use strict';

var load_levels = ['None', 'Light', 'Medium', 'Heavy', 'Extra Heavy'];

angular.module('GurpsCombatHelper')
.controller('TravelCalculatorCtrl', ['$scope', '$uibModal', '$sce', 'dices',
function($scope, $uibModal, $sce, dices) {

    $scope.travelers = []
    $scope.travel_time = 2;
    $scope.encounter = 0;
    $scope.hot_day = false;

    var travel_count = 0;
    var total_passed = 0;

    $scope.terrains = [
        "Horrible",
        "Bad",
        "Average",
        "Earth Road",
        "Good Road",
        "Good"
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

    $scope.remove_traveler = function(index) {
        $scope.travelers.splice(index, 1);
    }

    $scope.save_party = function(){
        $uibModal.open({
            templateUrl: 'templates/save_load_travelers.html',
            controller: 'TravelCalculatorSaveLoadCtrl',
            resolve: {
                params: function() {
                    return {
                        title: 'Save party',
                        party: JSON.stringify($scope.travelers)
                    }
                }
            }
        })
    }

    $scope.load_party = function(){
        $uibModal.open({
            templateUrl: 'templates/save_load_travelers.html',
            controller: 'TravelCalculatorSaveLoadCtrl',
            resolve: {
                params: function() {
                    return {
                        title: 'Load party',
                        party: null
                    }
                }
            }
        }).result.then(function(party) {
            $scope.travelers = JSON.parse(party);
        })
    }

    $scope.travel_results_text = '';
    $scope.travel_results = null;

    $scope.travel = function() {

        function fplose(traveler, hikingTime) {
            return (1 + load_levels.indexOf(traveler.load)) * hikingTime;
        }

        var hikingTime = $scope.travel_time;     
        var encounterHappend = false;   

        var tired;

        for(var i = 0; i < hikingTime; ++i) {
            tired = $scope.travelers.find(function(t) {
                return Math.ceil(parseFloat(t.fp) / 3) <= fplose(t, i + 1);
            });
            if(tired) {
                hikingTime = Math.min(i + 2, hikingTime);
                break;
            }
        }

        //check for encounter
        for(var i = 0; i < hikingTime; ++i) {
            if(dices.rollPercent() < $scope.encounter) {
                tired = null;
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
        total_passed += hikingTime * speed;
        var d = $uibModal.open({
            templateUrl: "templates/travel_results.html",
            controller: 'TravelCalculatorTravelResultsCtrl',
            resolve: {
                result: function() {
                    return {
                        encounter: encounterHappend,
                        time: hikingTime,
                        range: hikingTime * speed,
                        fp_lose: fp_lose,
                        tired: tired
                    }
                }
            }
        }).result.then(function(log_piece) {
            console.log('result:', log_piece);
            $scope.travel_results_text = '<p>Travel ' + ++travel_count + ' total passed ' + total_passed + 'km' + '</p>'
                                        + log_piece + '<p><hr/></p>' + $scope.travel_results_text;
            
            $scope.travel_results = $sce.trustAsHtml($scope.travel_results_text);
        });
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
.controller('TravelCalculatorSaveLoadCtrl', ['$scope', '$uibModalInstance', 'params',
function($scope, $uibModalInstance, params) {

    $scope.title = params.title;
    $scope.party = params.party;
    
    $scope.ok = function () {
        $uibModalInstance.close($scope.party);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}]);

angular.module('GurpsCombatHelper')
.controller('TravelCalculatorTravelResultsCtrl', ['$scope', '$uibModalInstance', '$sce', 'result', 'dices',
function($scope, $uibModalInstance, $sce, result, dices) {

    $scope.result = result;

    var res = '';
    res += '<p> You have traveled for an ' + result.time + ' hours and passed ' + result.range + ' kilometers</p>';
    if(result.tired) {
        res += '<p> You can\'t go further, because ' + result.tired.name + ' has tired and require rest </p>';
    }
    if(result.encounter) 
        res += '<p> You have met enemies on your way, situation roll ' + dices.roll3d6() + '</p>';
    else 
        res += '<p> You haven\'t met any danger on your way </p>';
    result.fp_lose.forEach(function(t) {
        res += '<p> ' + t.name + ' have lost ' + t.fp_lost + ' fatigue points and left in ' + t.fp_left + '</p>';
    });



    $scope.message = $sce.trustAsHtml(res);

    $scope.ok = function () {
        console.log('exit from result dialog');
        $uibModalInstance.close(res);
    };
}]);
