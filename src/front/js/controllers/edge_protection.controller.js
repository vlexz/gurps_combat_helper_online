
angular.module('GurpsCombatHelper')
.controller('EdgeProtectionCtrl', ['$scope', '$uibModal', '$sce', 'dices', 'LookupTables', '$http',
function($scope, $uibModal, $sce, dices, LookupTables, $http) {

    $scope.combat = null;

    $scope.combatants = [];
    $scope.locations = ['random'].concat(LookupTables.locations);
    $scope.dmg_types = Object.keys(LookupTables.damage_types);

    $scope.options = {
        catastrophic_penetration: false
    }

    $scope.combat_log = [];

    $scope.create_combatant = function() {
        $uibModal.open({
            templateUrl: 'templates/ep_add_combatant.html',
            controller: 'EPAddCombatantCtrl',
            resolve: {
                combatant: function() {
                    return {
                        name: '',
                        hp: 10,
                        armor_set: []
                    }
                }
            }
        }).result.then(function(combatant){
            $http.post('/api/ep/add_combatant', combatant);
        })
    }

    $scope.add_combatant = function() {
        $uibModal.open({
            templateUrl: '/templates/load_list.html',
            controller: 'LoadListCtrl',
            resolve: {
                params: function() {
                    return {
                        title: 'Add Combatant',
                        geturl: '/api/ep/combatants',
                        delurl: '/api/ep/del_combatant'
                    }
                }
            }            
        }).result.then(function(combatant) {
            combatant.curr_hp = combatant.hp;
            combatant.location = 'random';
            combatant.dmg_type = 'cr';
            combatant.damage = 0;
            $scope.combatants.push(combatant);
        })
    }

    $scope.remove_combatant = function(index) {
        $scope.combatants.splice(index, 1);
    }

    $scope.edit_combatant = function(index) {
        $uibModal.open({
            templateUrl: 'templates/ep_add_combatant.html',
            controller: 'EPAddCombatantCtrl',
            resolve: {
                combatant: function() {
                    return $scope.combatants[index];
                }
            }
        }).result.then(function(combatant){
            console.log(combatant);
            combatant.curr_hp = combatant.hp;
            $scope.combatants[index] = combatant;
        })
    }

    $scope.save_combat = function(){
        $uibModal.open({
            templateUrl: '/templates/save.html',
            controller: 'SaveCtrl',
            resolve: {
                params: function() {
                    return {
                        title: 'Save Combat',
                        saveurl: '/api/ep/add_combat',
                        object: $scope.combatants
                    }
                }
            }
        })
    }

    $scope.load_combat = function() {
        $uibModal.open({
            templateUrl: '/templates/load_list.html',
            controller: 'LoadListCtrl',
            resolve: {
                params: {
                    title: 'Load Combat',
                    geturl: '/api/ep/combats',
                    delurl: '/api/ep/del_combat'
                }
            }
        }).result.then(function(combat){
            $scope.combat = combat;
            $scope.combatants = combat.object;
        })
    }

    $scope.save_current = function() {
        if($scope.combat)
            $http.post('/api/ep/update_combat', $scope.combat);
    }

    $scope.clear = function() {
        $scope.combatants = [];
        $scope.combat = null;
        $scope.combat_log = [];
    }

    $scope.strike = function(index) {        
        var c = $scope.combatants[index];
        var location = '';
        if(c.location == 'random') location = LookupTables.locations.random();
        else location = c.location;
        var armors = c.armor_set.filter(function(armor){
            return armor.locations.indexOf(location) != -1;
        })
        console.log(armors);        
        var armor = armors.reduce(function(curr, armor){
            return {dr: curr.dr + armor.dr, ep_cut: curr.ep_cut + armor.ep_cut, ep_imp: curr.ep_imp + armor.ep_imp};
        }, {dr: 0, ep_cut: 0, ep_imp: 0});
        console.log(armor);
        var message = c.name + ' was hit by ' + c.damage + '(' + c.dmg_type + ') to ' + location;
        if(armor.dr >= c.damage) {
            message += ' DR absorbs all damage.';
        } else {
            var dmg = c.damage - armor.dr;
            var cr_dmg = 0;
            var dmg_type = c.dmg_type;
            //characted damage
            var impdmg = ['imp', 'pi++', 'pi+', 'pi', 'pi-'];
            if(c.dmg_type == 'cut' && armors.length) {
                if(dmg < armor.ep_cut) {
                    dmg_type = 'cr';
                    message += ' EP converts damage type to crashing.';
                } else if(!$scope.options.catastrophic_penetration) {
                    message += ' EP converts part of the damage to crashing.';
                    cr_dmg = armor.ep_cut;
                    dmg -= armor.ep_cut;
                }
            }

            if(impdmg.indexOf(dmg_type) != -1 && armors.length) {
                if(dmg < armor.ep_imp) {
                    dmg_type = 'cr';
                    message += ' EP converts damage type to crashing.';
                } else if(!$scope.options.catastrophic_penetration) {
                    message += ' EP converts part of the damage to crashing.';
                    cr_dmg = armor.ep_imp;
                    dmg -= armor.ep_imp;
                }   
            }

            var modifier = LookupTables.damage_types[dmg_type];
            var cr_modifier = 1;
            //location effect
            if((location == 'skull' || location == 'eyes') && dmg_type != 'tox') {
                if(location != 'eyes')
                    dmg = Math.max(0, dmg - 2);
                modifier = 4;
                cr_modifier = 4;
            }
            if(location == 'face' && dmg_type == 'corr') {
                modifier = 1.5;
            }
            var limbs = ['left_arm', 'left_leg', 'right_arm', 'right_leg', 'hand', 'foot'];
            if(limbs.indexOf(location) != -1 && (dmg_type == 'imp' || dmg_type == 'pi+' || dmg_type == 'pi++')) {
                modifier = 1;
            }

            if (location == 'neck') {
                cr_modifier = 1.5;
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

            var total_damage = Math.floor(dmg*modifier) + Math.floor(cr_dmg*cr_modifier);
            if(armors.length) {
                message += ' After passing armor,'
            }
            message += ' attack inflicts ';
            if(cr_dmg > 0) {
                message += 'cr' + cr_dmg + '(' + Math.floor(cr_dmg*cr_modifier) +') and '
            }
            message += dmg + '(' + Math.floor(dmg*modifier) + ') damage, total ' + total_damage + ' hit points.';
            c.curr_hp -= total_damage;

        }

        if(c.dmg_type == 'corr' && armors.length > 0) {
            armors[0].curr_corr += c.damage;
            if(armors[0].curr_corr >= 5) {
                armors[0].dr = Math.max(armor.dr - Math.floor(armors[0].curr_corr / 5), 0);
                armor.curr_corr = armor.curr_corr % 5;
                message += ' Corrosion damage reduces DR of the ' + armors[0].name + ' to ' + armors[0].dr;
            }
            if(armors[0].dr == 0) {
                c.armor_set.splice(c.armor_set.indexOf(armors[0]), 1);
                message += ' Corrosion damage completely destroyed ' + armors[0].name;
                armors.shift();                
            }
        }

        //armor damage
        if(armors.length) {
            var armor = armors[0];
            var modifier = LookupTables.damage_types[c.dmg_type];
            if(c.dmg_type == 'imp' || c.dmg_type == 'pi++') {
                modifier = 0.5;
            } else if(c.dmg_type == 'pi+') {
                modifier = 0.3;
            } else if(c.dmg_type == 'pi') {
                modifier = 0.2;
            } else if(c.dmg_type == 'pi-') {
                modifier = 0.1;
            }
            var dmg = Math.min(Math.max(0, c.damage - armor.dr), armor.dr);
            dmg = Math.floor(dmg * modifier);
            armor.hp = Math.max(0, armor.hp - dmg);
            message += ' ' + armor.name + ' receives ' + dmg + ' damage itself';
            if(armor.hp == 0) {
                message += ' and completely destroyed';
                c.armor_set.splice(c.armor_set.indexOf(armor), 1);
            } else {
                message += ' and have ' + armor.hp + 'HP.';
            }
        };

        $scope.combat_log.unshift(message);
    }
}]);

angular.module('GurpsCombatHelper')
.controller('EPAddCombatantCtrl', ['$scope', '$uibModalInstance', 'LookupTables', 'combatant', '$http',
function($scope, $uibModalInstance, LookupTables, combatant, $http) {

    $scope.combatant = combatant;

    $http.get('/api/armor/categories')
    .then(function(response){
        $scope.armor_categories = response.data;
    })

    $scope.select_category = function(id) {
        console.log('Select category', id);
        $http.post('/api/armor/armors', {category: id})
        .then(function(response) {
            $scope.armors = response.data;
        })
    }

    $scope.add_armor = function(index) {
        var armor = JSON.parse(JSON.stringify($scope.armors[index]))
        armor.curr_corr = 0;
        $scope.combatant.armor_set.push(armor);
    }

    $scope.remove_armor = function(index) {
        $scope.combatant.armor_set.splice(index, 1);
    }


    $scope.ok = function () {
        $uibModalInstance.close($scope.combatant);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}]);