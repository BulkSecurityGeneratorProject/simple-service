(function() {
    'use strict';

    angular
        .module('simpleServiceApp')
        .controller('NewOrderDialogController', NewOrderDialogController);

    NewOrderDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance',  'FreeClassRecord', 'MarketChannelCategory', 'ClassCategory', 'User', 'NewOrderResourceLocation'];

    function NewOrderDialogController ($timeout, $scope, $stateParams, $uibModalInstance, FreeClassRecord, MarketChannelCategory, ClassCategory, User, NewOrderResourceLocation) {
        var vm = this;

        vm.freeClassRecord = {};
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;         vm.datePickerOptions = {             showMeridian: false         };
        vm.save = save;
        vm.marketchannelcategories = MarketChannelCategory.query({ page: 0,  size: 1000 });
        vm.classcategories = ClassCategory.query({ page: 0,  size: 1000 });
        vm.users = User.query({ page: 0,  size: 1000 });
        vm.locations = NewOrderResourceLocation.query({ page: 0,  size: 1000 });

        vm.searchPersonWithKeyword = function (keyword) {

            console.log("searching people with keyword " + keyword);
        };

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

        vm.datePickerOpenStatus.createdDate = false;
        vm.datePickerOpenStatus.lastModifiedDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
