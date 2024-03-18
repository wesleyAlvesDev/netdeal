
angular.module('netdealApp').factory("colaboratorService", function ($http) {


    var baseUrl = 'http://localhost:8080/api/colaborator';
    var factory = {};

    factory.getAll = () => $http({ method: 'GET', url: `${baseUrl}/all` });

    factory.getLinkedList = () => $http({ method: 'GET', url: `${baseUrl}/linked-list` });

    factory.getOne = (id) => $http({ method: 'GET', url: baseUrl, params: { colaboratorId: id } });

    factory.createColaborator = ($scope) => {

        console.log($scope);
        return $http({
            method: 'POST',
            data: {
                'fullName': $scope.fullName,
                'password': $scope.password,
            },
            url: baseUrl
        });
    };

    factory.updateColaborator = ($scope) => {
        return $http({
            method: 'PUT',
            data: {
                'id': $scope.id,
                'fullName': $scope.fullName,
                'password': $scope.password,
            },
            url: baseUrl
        });
    };

    factory.createSubordinate = ($scope) => {
        return $http({
            method: 'POST',
            data: {
                'managerId': $scope.manager.id,
                'subordinateId': $scope.subordinate.id,
            },
            url: `${baseUrl}/create-subordinate`
        });
    };

    factory.delete = (id) => $http({ method: 'DELETE', url: baseUrl, params: { colaboratorId: id } });

    return factory;
});
