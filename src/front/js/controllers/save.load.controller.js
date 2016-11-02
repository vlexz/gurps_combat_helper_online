angular.module('GurpsCombatHelper')
.controller('LoadListCtrl', ['$scope', '$uibModalInstance', 'params', '$http',
function($scope, $uibModalInstance, params, $http) {

    $scope.title = params.title;

    $scope.items = [];

    function update() {
        $http.post(params.geturl)
        .then(function(response){
            $scope.items = response.data;
        });
    }

    $scope.load = function(index) {
        $uibModalInstance.close($scope.items[index]);
    }

    $scope.delete = function(index) {
        $http.post(params.delurl, {id: $scope.items[index]._id})
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
.controller('SaveCtrl', ['$scope', '$uibModalInstance', 'params', '$http',
function($scope, $uibModalInstance, params, $http) {
    
    $scope.title = params.title;

    $scope.party_name = '';
    
    $scope.ok = function () {
        $http.post(params.saveurl, {
            name: $scope.name,
            object: params.object
        }).then(function(response){
            if(response.data.status == 'fail') {
                console.log(response.error);
            } else {
                $uibModalInstance.close(response.data)
            }
            // if(response.data.status == 'ok') {
            //     console.log('party saved');
            //     $uibModalInstance.close();
            // } else {
                
            // }
        })
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
}]);