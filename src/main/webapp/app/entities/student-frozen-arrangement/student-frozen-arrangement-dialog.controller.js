(function() {
    'use strict';

    angular
        .module('simpleServiceApp')
        .controller('StudentFrozenArrangementDialogController', StudentFrozenArrangementDialogController);

    StudentFrozenArrangementDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'StudentFrozenArrangement', 'Student', 'ClassArrangement', 'StudentFrozen'];

    function StudentFrozenArrangementDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, StudentFrozenArrangement, Student, ClassArrangement, StudentFrozen) {
        var vm = this;

        vm.studentFrozenArrangement = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.students = Student.query();
        vm.classarrangements = ClassArrangement.query();
        vm.studentfrozens = StudentFrozen.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.studentFrozenArrangement.id !== null) {
                StudentFrozenArrangement.update(vm.studentFrozenArrangement, onSaveSuccess, onSaveError);
            } else {
                StudentFrozenArrangement.save(vm.studentFrozenArrangement, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('simpleServiceApp:studentFrozenArrangementUpdate', result);
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
