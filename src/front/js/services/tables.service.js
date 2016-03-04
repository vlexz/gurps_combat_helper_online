angular.module('GurpsCombatHelper')
.service('LookupTables', ['dices', 
function(dices){

    var damage_types = {
        cr: 1,
        cut: 1.5,
        imp: 2,
        'pi-': 0.5,
        pi: 1,
        'pi+': 1.5,
        'pi++': 2,
        tox: 1,
        corr: 1,
        burn: 1
    };

    var locations = [
        'eyes',
        'skull',        
        'face',
        'right_leg',
        'right_arm',
        'torso',
        'groin',
        'left_arm',
        'left_leg',
        'hand',
        'foot',
        'neck',
        'vitals'
    ];

    locations.random = function() {
        switch(dices.roll3d6()) {
            case 3:
            case 4: return this[1];
            case 5: return this[2];
            case 6:
            case 7: return this[3];
            case 8: return this[4];
            case 9:
            case 10: return this[5];
            case 11: return this[6];
            case 12: return this[7];
            case 13: 
            case 14: return this[8];
            case 15: return this[9];
            case 16: return this[10];
            case 17:
            case 18: return this[11];
        }
    }



    return {
        locations: locations,
        damage_types: damage_types
    }
}]);