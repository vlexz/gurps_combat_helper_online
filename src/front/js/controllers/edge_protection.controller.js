
angular.module('GurpsCombatHelper')
.controller('EdgeProtectionCtrl', ['$scope', '$uibModal', '$sce', 'dices', 'LookupTables',
function($scope, $uibModal, $sce, dices, LookupTables) {

    $scope.combatants = [];
    $scope.locations = ['random'].concat(LookupTables.locations);
    $scope.dmg_types = Object.keys(LookupTables.damage_types);

    $scope.combat_log = [];

    $scope.add_combatant = function() {
        $uibModal.open({
            templateUrl: 'templates/ep_add_combatant.html',
            controller: 'EPAddCombatantCtrl'
        }).result.then(function(combatant){
            console.log(combatant);
            combatant.curr_hp = combatant.hp;
            $scope.combatants.push(combatant);
        })
    }

    $scope.strike = function(index) {        
        var c = $scope.combatants[index];
        var location = '';
        if(c.location == 'random') location = LookupTables.locations.random();
        else location = c.location;
        var armor = c.armor[location];
        var message = c.name + ' was hit by ' + c.damage + '(' + c.dmg_type + ') to ' + location;
        if(armor.dr > c.damage) {
            message += ' DR absorbs all damage.';
        } else {
            var dmg = c.damage - armor.dr;
            var dmg_type = c.dmg_type;
            //characted damage
            var impdmg = ['imp', 'pi++', 'pi+', 'pi', 'pi-'];
            if(c.dmg_type == 'cut' && dmg <= armor.ep.cut 
                || impdmg.indexOf(dmg_type) > 0 && dmg <= armor.ep.imp) {
                message += ' EP converts damage type to crashing.';
                dmg_type = 'cr';
            }
            var modifier = LookupTables.damage_types[dmg_type];
            //location effect
            if((location == 'skull' || location == 'eyes') && dmg_type != 'tox') {
                dmg = Math.max(0, dmg - 2);
                modifier = 4;
            }
            if(location == 'face' && dmg_type == 'corr') {
                modifier = 1.5;
            }
            var limbs = ['left_arm', 'left_leg', 'right_arm', 'right_leg', 'hand', 'foot'];
            if(limbs.indexOf(location) != -1 && (dmg_type == 'imp' || dmg_type == 'pi+' || dmg_type == 'pi++')) {
                modifier = 1;
            }

            if (location == 'neck') {
                if (dmg_type == 'cr') {
                    modifier = 1.5;
                } else if (dm_type == 'cut') {
                    modifier = 2;
                }
            }

            if (location == 'vitals') {
                if (dmg_type == 'imp' || dmg_type.indexOf('pi') != -1) {
                    modifier = 3;
                } else {
                    modifier = 2;
                }
            }

            message += ' After passing armor, attack inflicts ' + dmg + '(' + Math.floor(dmg*modifier) + ') damage.';
            c.curr_hp -= Math.floor(dmg*modifier);

        }
        if(c.dmg_type == 'corr' && armor.dr > 0) {
            armor.curr_corr += c.damage;
            if(armor.curr_corr >= 5) {
                armor.dr = Math.max(armor.dr - Math.floor(armor.curr_corr / 5), 0);
                armor.curr_corr = armor.curr_corr % 5;
                message += ' Corrosion damage reduces armor at ' + location + ' to ' + armor.dr;
            }
        }

        $scope.combat_log.unshift(message);
    }
}]);

angular.module('GurpsCombatHelper')
.controller('EPAddCombatantCtrl', ['$scope', '$uibModalInstance', 'LookupTables',
function($scope, $uibModalInstance, LookupTables) {

    function drset() {
        return {
            dr: 0,
            ep: {
                cut: 0,
                imp: 0,
            },
            hp: 0,
            curr_corr: 0
        }
    }

    $scope.combatant = {
        name: '',
        hp: '',
        armor: {}
    }

    LookupTables.locations.forEach(function(location) {
        $scope.combatant.armor[location] = drset();
    });


    $scope.ok = function () {
        $uibModalInstance.close($scope.combatant);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}]);