(function () {
    'use strict';

    angular
        .module('simpleServiceApp')
        .controller('ContractController', ContractController);

    ContractController.$inject = ['$rootScope', '$uibModal', '$scope', '$state', 'Contract', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams', 'Course', 'ContractStatus', 'Product', 'User'];

    function ContractController($rootScope, $uibModal, $scope, $state, Contract, ParseLinks, AlertService, paginationConstants, pagingParams, Course, ContractStatus, Product, User) {

        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.datePickerOpenStatus = {};
        vm.openCalendar =  function (date) {
            vm.datePickerOpenStatus[date] = true;
        };
        vm.datePickerOptions = {             showMeridian: false         };
        vm.clearConditions = function () {
            vm.searchCondition = {};
        };
        vm.searchCondition = {};
        $scope.pagination = {
            currentPageNumber: 1,
            totalItems: 0
        };
        vm.consultants = User.getAllCourseConsultant();
        vm.courses = Course.query({size: 100});
        vm.contractstatuses = ContractStatus.query({size: 100});
        vm.products = Product.query({size: 200});

        vm.loadAll = loadAll;

        $rootScope.$on("simpleServiceApp:createPackageContract", function (event, args) {
            vm.contracts = args.contracts;
            vm.queryCount = vm.contracts.length;
            vm.page = 0;
            AlertService.success("创建了" + vm.contracts.length + "条合同");
        });

        function loadAll() {

            var parameters = {
                page: $scope.pagination.currentPageNumber - 1,
                size: vm.itemsPerPage,
                sort: sort()
            };

            if (vm.searchCondition.customerName) {
                parameters["customerName"] = vm.searchCondition.customerName;
            }
            if (vm.searchCondition.studentName) {
                parameters["studentName"] = vm.searchCondition.studentName;
            }
            if (vm.searchCondition.customerContactPhoneNumber) {
                parameters["customerContactPhoneNumber"] = vm.searchCondition.customerContactPhoneNumber;
            }
            if (vm.searchCondition.contractNumber) {
                parameters["contractNumber.contains"] = vm.searchCondition.contractNumber;
            }
            if (vm.searchCondition.serialNumber) {
                parameters["serialNumber.equals"] = vm.searchCondition.serialNumber;
            }
            if (vm.searchCondition.product) {
                parameters["productId.equals"] = vm.searchCondition.product.id;
            }
            if (vm.searchCondition.course) {
                parameters["courseId.equals"] = vm.searchCondition.course.id;
            }
            if (vm.searchCondition.contractStatus) {
                parameters["contractStatusId.equals"] = vm.searchCondition.contractStatus.id;
            }
            if (vm.searchCondition.signStartDate) {
                parameters["signStartDate"] = vm.searchCondition.signStartDate;
            }
            if (vm.searchCondition.signEndDate) {
                parameters["signEndDate"] = vm.searchCondition.signEndDate;
            }
            if (vm.searchCondition.courseConsultant) {
                parameters["followerId"] = vm.searchCondition.courseConsultant.id;
            }

            Contract.query(parameters, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }

            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                $scope.pagination.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.contracts = data;
                vm.page = pagingParams.page;
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }

        vm.editContract = function (contract) {

        };

        vm.openTypedContractDialog = function (type) {

            $uibModal.open({
                templateUrl: 'app/entities/contract/contract-dialog.html',
                controller: 'ContractDialogController',
                controllerAs: 'vm',
                backdrop: 'static',
                size: 'lg',
                resolve: {
                    entity: function () {
                        return {
                            contractNumber: null,
                            serialNumber: null,
                            signDate: null,
                            startDate: null,
                            endDate: null,
                            totalMoneyAmount: null,
                            moneyShouldCollected: null,
                            moneyCollected: null,
                            promotionAmount: null,
                            totalHours: null,
                            hoursTaken: null,
                            comments: null,
                            createdBy: null,
                            createdDate: null,
                            lastModifiedBy: null,
                            lastModifiedDate: null,
                            id: null,
                            contractType: type
                        };
                    }
                }
            }).result.then(function() {
                $state.go('contract', null, { reload: 'contract' });
            }, function() {
                $state.go('contract');
            });
        }
    }
})();
