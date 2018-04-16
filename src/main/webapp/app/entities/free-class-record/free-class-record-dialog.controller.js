(function() {
    'use strict';

    angular
        .module('simpleServiceApp')
        .controller('FreeClassRecordDialogController', FreeClassRecordDialogController);

    FreeClassRecordDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'FreeClassRecord', 'MarketChannelCategory', 'ClassCategory'];

    function FreeClassRecordDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, FreeClassRecord, MarketChannelCategory, ClassCategory) {
        var vm = this;

        vm.freeClassRecord = entity;
        vm.clear = clear;
        vm.save = save;
        vm.marketchannelcategories = MarketChannelCategory.query();
        vm.classcategories = ClassCategory.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.freeClassRecord.id !== null) {
                FreeClassRecord.update(vm.freeClassRecord, onSaveSuccess, onSaveError);
            } else {
                FreeClassRecord.save(vm.freeClassRecord, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('simpleServiceApp:freeClassRecordUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
