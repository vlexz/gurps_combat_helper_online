'use strict';

var load_levels = ['None', 'Light', 'Medium', 'Heavy', 'Extra Heavy'];

angular.module('GurpsCombatHelper')
.controller('TravelCalculatorCtrl', ['$scope', '$uibModal', '$sce', 'dices', 'UsersService', '$http',
function($scope, $uibModal, $sce, dices, UsersService, $http) {

    $scope.$on('user_changed', function(event, user) {
        $scope.user = user;
        if(user.currents.travel) {
            $http.post('/api/travel/get_party', {id: user.currents.travel})
            .then(function(response){
                $scope.party_name = response.data.name;
                $scope.travelers = response.data.travelers;
            })
        }
    });

    $scope.party_name = null;


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
            controller: 'TravelCalculatorAddTravelerCtrl',
            resolve: {
                traveler: function() { return {
                    name: '',
                    move: 5,
                    fp: 10,
                    load: 'None',
                    hiking: 5,
                    carrier: false,
                    fit: 'Average',
                } }
            }
        }).result.then(function(traveler){
            $scope.travelers.push(traveler);
        });
    }

    $scope.remove_traveler = function(index) {
        $scope.travelers.splice(index, 1);
    }

    $scope.edit_traveler = function(index) {
        $uibModal.open({
            templateUrl: 'templates/add_traveler.html',
            controller: 'TravelCalculatorAddTravelerCtrl',
            resolve: {
                traveler: function() { return $scope.travelers[index]}
            }
        }).result.then(function(traveler){
            $scope.travelers[index] = traveler;
        });
    }

    $scope.save_party = function(){
        $uibModal.open({
            templateUrl: 'templates/save_travel_party.html',
            controller: 'TravelCalculatorSavePartyCtrl',
            resolve: {
                params: function() {
                    return {
                        party: $scope.travelers
                    }
                }
            }
        }).result.then(function(saved_party){
            $scope.party_name = saved_party.name;
            UsersService.setCurrent('travel', saved_party._id);
        })
    }

    $scope.load_party = function(){
        $uibModal.open({
            templateUrl: 'templates/load_travel_party.html',
            controller: 'TravelCalculatorLoadPartyCtrl',
            resolve: {
                params: function() {
                    return {
                        title: 'Load party',
                        party: null
                    }
                }
            }
        }).result.then(function(party) {
            $scope.party_name = party.name;
            $scope.travelers = party.travelers;
            UsersService.setCurrent('travel', party._id);
        })
    }

    $scope.clear_party = function() {
        $scope.party_name = null;
        $scope.travelers = [];
        UsersService.setCurrent('travel', null);
        $scope.travel_results_text = '';
        $scope.travel_results = null;
    }

    $scope.travel_results_text = '';
    $scope.travel_results = [];

    $scope.travel = function() {

        function fplose(traveler, hikingTime) {
            return (1 + load_levels.indexOf(traveler.load)) * hikingTime;
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

        var hikingTime = $scope.travel_time;     
        var encounterHappend = false;   

        var tired = null;

        var max_travel_times = $scope.travelers.map(function(t){
            var max_spent_fp = t.fp - Math.ceil(parseFloat(t.fp) / 3);
            var lose_per_hour = fplose(t, 1);
            if(t.carrier) {
                lose_per_hour /= terrain_modifier;
            }
            return max_spent_fp / lose_per_hour;
        })

        console.log(max_travel_times);

        hikingTime = max_travel_times.reduce(function(max, t){
            return Math.min(max, t);
        }, hikingTime);

        console.log('hking time', hikingTime);

        if(hikingTime < $scope.travel_time) {
            tired = $scope.travelers[max_travel_times.indexOf(hikingTime)];
        }

        // for(var i = 0; i < hikingTime; ++i) {
        //     tired = $scope.travelers.find(function(t) {
        //         return Math.ceil(parseFloat(t.fp) / 3) <= fplose(t, i + 1) / terrain_modifier;
        //     });
        //     if(tired) {
        //         hikingTime = Math.min(i + 2, hikingTime);
        //         break;
        //     }
        // }

        //check for encounter
        for(var i = 0; i < hikingTime; ++i) {
            if(dices.rollPercent() < $scope.encounter) {
                tired = null;
                hikingTime = i + 1;
                encounterHappend = true;
                break;
            }   
        }

        var fp_lose = $scope.travelers.map(function(person) {
            var fp_lose = fplose(person, hikingTime);
            if($scope.hot_day) fp_lose += 1;
            if(person.carrier) fp_lose /= terrain_modifier;        
            fp_lose = Math.ceil(fp_lose);
            return {
                name: person.name, 
                fp_lost: fp_lose,
                fp_left: person.fp - fp_lose
            }
        });
        total_passed += hikingTime * speed;

        function appendLog(piece) {
            console.log('result:', piece);
            $scope.travel_results_text = '<div class="well well-travel-result"><p">Travel ' + ++travel_count + ' total passed ' + total_passed + 'km' + '</p>'
                                        + piece  + '</div>';// + $scope.travel_results_text;
            $scope.travel_results.unshift($sce.trustAsHtml($scope.travel_results_text));
            // $scope.travel_results = $sce.trustAsHtml($scope.travel_results_text);
        }

        var result = {
            encounter: encounterHappend,
            time: hikingTime,
            range: hikingTime * speed,
            fp_lose: fp_lose,
            tired: tired
        };

        if($scope.popup_result) {
            var d = $uibModal.open({
                templateUrl: "templates/travel_results.html",
                controller: 'TravelCalculatorTravelResultsCtrl',
                resolve: {
                    result: function() { return result } 
                }
            })
        } 

        appendLog(composeResult(result, dices));        
    }
}]);

angular.module('GurpsCombatHelper')
.controller('TravelCalculatorAddTravelerCtrl', ['$scope', '$uibModalInstance', 'traveler',
function($scope, $uibModalInstance, traveler) {

    $scope.load_levels = load_levels;
    $scope.traveler = traveler;

    $scope.fits = [
        'Very Unfit',
        'Unfit',
        'Average',
        'Fit',
        'Very Fit'
    ]
    
    $scope.ok = function () {
        $uibModalInstance.close($scope.traveler);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}]);


angular.module('GurpsCombatHelper')
.controller('TravelCalculatorSavePartyCtrl', ['$scope', '$uibModalInstance', 'params', '$http',
function($scope, $uibModalInstance, params, $http) {

    $scope.party_name = '';
    
    $scope.ok = function () {
        $http.post('/api/travel/save_party', {
            name: $scope.party_name,
            travelers: params.party

        }).then(function(response){
            if(response.data.status == 'fail') {
                console.log(response.data.err);
            } else {
                console.log('party saved');
                $uibModalInstance.close(response.data);
            }
        })
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}]);

angular.module('GurpsCombatHelper')
.controller('TravelCalculatorLoadPartyCtrl', ['$scope', '$uibModalInstance', 'params', '$http',
function($scope, $uibModalInstance, params, $http) {

    $scope.parties = [];

    function update() {
        $http.get('/api/travel/get_parties')
        .then(function(response){
            $scope.parties = response.data;
        });
    }

    $scope.load = function(index) {
        $uibModalInstance.close($scope.parties[index]);
    }

    $scope.delete = function(index) {
        $http.post('/api/travel/remove_party', {id: $scope.parties[index]._id})
        .then(function(result){
            console.log(result);
            update();
        })
    }

    update();
    
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}]);

angular.module('GurpsCombatHelper')
.controller('TravelCalculatorTravelResultsCtrl', ['$scope', '$uibModalInstance', '$sce', 'result', 'dices',
function($scope, $uibModalInstance, $sce, result, dices) {

    $scope.result = result;   

    var res = composeResult(result, dices);

    $scope.message = $sce.trustAsHtml(res);

    $scope.ok = function () {
        console.log('exit from result dialog');
        $uibModalInstance.close(res);
    };
}]);

function composeResult(result, dices) {
    var res = '';
    res += '<p> You have traveled for an ' + result.time.toFixed(1) + ' hours and passed ' + result.range.toFixed(1) + ' kilometers</p>';
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
    return res;
}


