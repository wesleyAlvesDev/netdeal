
netdealApp.controller('colaboratorController', ['$scope', '$mdDialog', '$mdToast', '$sce', 'colaboratorService', ($scope, $mdDialog, $mdToast, $sce, colaboratorService) => {

    $scope.$watch('colaborators', function() {
        $scope.getAll();
    }, true); 

    $scope.getAll = () => {
        colaboratorService.getLinkedList().then(function successCallback(response) {
            $scope.colaborators = response.data;
        }, function errorCallback(response) {
            $scope.showToast("Ocorreu um erro.");
        });
    }

    $scope.openCreateColaborator = (event) => {
        $scope.clearForm();
        $mdDialog.show({
            controller: DialogController,
            templateUrl: 'app/src/views/colaborator/colaborator-create.component.html',
            parent: angular.element(document.body),
            clickOutsideToClose: true,
            scope: $scope,
            preserveScope: true,
            fullscreen: true
        });
    }

    function DialogController($scope, $mdDialog) {
        $scope.cancel = () => {
            $mdDialog.cancel();
        };
    }

    $scope.createColaborator = () => {
        colaboratorService.createColaborator($scope).then(function successCallback(response) {
            $scope.showToast('Colaborador Criado com Sucesso!!');
            $scope.getAll();
            $scope.cancel();
            $scope.clearForm();
        }, function errorCallback(response) {
            $scope.showToast("Ocorreu um erro!");
        });
    }

    $scope.openToCreateSubordinate = () => {
        $scope.clearForm();
        colaboratorService.getAll().then(function successCallback(response) {
            $scope.allColaborators = response.data;
            $mdDialog.show({
                controller: DialogController,
                templateUrl: 'app/src/views/colaborator/colaborator-create-subordinate.component.html',
                parent: angular.element(document.body),
                clickOutsideToClose: true,
                scope: $scope,
                preserveScope: true,
                fullscreen: true
            });
        }, function errorCallback(response) {
            $scope.showToast("Ocorreu um erro.");
        });
        
    }

    $scope.createSubordinate = () => {
        colaboratorService.createSubordinate($scope).then(function successCallback(response) {
            $scope.showToast('Hierarquia Criada com Sucesso!!');
            $scope.getAll();
            $scope.cancel();
            $scope.clearForm();
        }, function errorCallback(response) {
            $scope.showToast("Ocorreu um erro!");
        });
    }

    $scope.openToViewColaborator = (id) => {
        $scope.clearForm();
        colaboratorService.getOne(id).then(function successCallback(response) {
            $scope.id = response.data.id;
            $scope.fullName = response.data.fullName;
            $scope.scrorePassword = $scope.getStrengthPassword(response.data.scrorePassword);
            $scope.subordinates = response.data.subordinates;

            $mdDialog.show({
                controller: DialogController,
                templateUrl: 'app/src/views/colaborator/colaborator-reed.component.html',
                parent: angular.element(document.body),
                clickOutsideToClose: true,
                scope: $scope,
                preserveScope: true,
                fullscreen: true
            }).then(
                function () { },
            );

        }, function errorCallback(response) {
            $scope.showToast("Ocorreu um erro!");
        });
    }

    $scope.openUpdate = (id) => {
        colaboratorService.getOne(id).then(function successCallback(response) {
            $scope.id = response.data.id;
            $scope.fullName = response.data.fullName;
            $scope.scrorePassword = response.data.scrorePassword;
            $scope.password = response.data.password;
            $scope.subordinates = response.data.subordinates;

            $mdDialog.show({
                controller: DialogController,
                templateUrl: 'app/src/views/colaborator/colaborator-update.component.html',
                parent: angular.element(document.body),
                targetEvent: event,
                clickOutsideToClose: true,
                scope: $scope,
                preserveScope: true,
                fullscreen: true
            }).then(
                function () { },

                function () {
                    $scope.clearForm();
                }
            );

        }, function errorCallback(response) {
            $scope.showToast("Ocorreu um erro!");
        });
    }

    $scope.updateColaborator = () => {
        colaboratorService.updateColaborator($scope).then(function successCallback(response) {
            $scope.showToast('Colaborador atualizado com Sucesso!!');
            $scope.getAll();
            $scope.cancel();
            $scope.clearForm();
        },
            function errorCallback(response) {
                $scope.showToast("Unable to update record.");
            });

    }

    $scope.confirmDelete = function (event, id) {
        $scope.id = id;
        var confirm = $mdDialog.confirm()
            .title('Tem certeza?')
            .textContent('O Colaborador vai ser excluido!')
            .targetEvent(event)
            .ok('Sim')
            .cancel('Não');
        $mdDialog.show(confirm).then(
            function () {
                $scope.delete();
            },
            function () { }
        );
    }

    $scope.delete = () => {
        colaboratorService.delete($scope.id).then(function successCallback(response) {
            $scope.showToast('Colaborador Excluido com Sucesso!!');
            $scope.getAll();
        }, function errorCallback(response) {
            $scope.showToast("Unable to delete record.");
        });
    }

    $scope.getColor = function (score) {
        if (score < 30) {
            return 'red';
        } else if (score >= 30 && score < 70) {
            return 'orange';
        } else {
            return 'green';
        }
    };


    $scope.getStrengthPassword = function (score) {
        if (score < 30) {
            return score + '% - Ruim';
        } else if (score >= 30 && score < 70) {
            return score + '% - Bom';
        } else {
            return score + '% - Forte';
        }
    };


    $scope.clearForm = () => {
        $scope.id = "";
        $scope.fullName = "";
        $scope.password = "";
        $scope.scrorePassword = "";
        $scope.subordinates = [];
    }

    $scope.showToast = (message) => {
        $mdToast.show(
            $mdToast.simple()
                .textContent(message)
                .hideDelay(3000)
                .position("top right")
        );
    }

    $scope.expanded = false;

    $scope.toggle = (colaborator) => {
        let element = document.getElementById(colaborator.id);
        if (element) {
            if (!element.innerHTML) {
                $scope.expanded = true;
                element.innerHTML =  $scope.generateSubordinatesHTML(colaborator.subordinates);
            } else {
                $scope.expanded = false;
                element.innerHTML = '';
            }
        }
    };

    $scope.generateSubordinatesHTML = (subordinates, marginLeft = 25) => {
        var html = '<md-list>';
        subordinates.forEach(function(subordinate) {
            html += '<md-list-item style="margin-left:' + marginLeft + 'px;">';
            html += '<div class="row" style="width: 120%;">';
            html += '<div class="col-md-6">';
            html += '<p style="margin-bottom: 0; font-weight: 700;">Código (ID): ' + subordinate.id + '</p>';
            html += '<p style="margin-bottom: 0; font-weight: 700;">Nome: ' + subordinate.fullName + '</p>';
            html += '<p style="margin-bottom: 0; font-weight: 700;">Força da senha: <span style=" color:' +  $scope.getColor(subordinate.scrorePassword) + '">' + $scope.getStrengthPassword(subordinate.scrorePassword) + '</span></p>';
            html += '</div>';
            html += '</div>';
            html += '<md-divider inset></md-divider>';
            html += '</md-list-item>';
            if (subordinate.subordinates.length > 0) {
                html += $scope.generateSubordinatesHTML(subordinate.subordinates, marginLeft + 50);
            }
        });
        html += '</md-list>';
        return $sce.trustAsHtml(html);
    };

}]);