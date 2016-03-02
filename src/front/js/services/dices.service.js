'use strict';

angular.module('GurpsCombatHelper')
.service('dices', function(){
    return {
        roll3d6: function() {           
            var result = 0; 
            for(var i = 0; i < 3; ++i) {                
                result += parseInt(Math.random()*6) + 1;
            }
            console.log('3d6 roll', result);
            return result;
        },
        rollPercent: function() {
            var result = parseInt(Math.random()*100);
            console.log('percent roll', result);
            return result;
        }
    }
})