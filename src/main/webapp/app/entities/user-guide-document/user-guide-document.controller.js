(function() {
    'use strict';

    angular
        .module('simpleServiceApp')
        .controller('UserGuideDocumentController', UserGuideDocumentController);

    UserGuideDocumentController.$inject = ['$uibModal', '$state', 'UserGuideDocument', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function UserGuideDocumentController($uibModal, $state, UserGuideDocument, ParseLinks, AlertService, paginationConstants, pagingParams) {

        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;

        vm.editDocument = function (userGuideDocument) {

            $uibModal.open({
                templateUrl: 'app/entities/user-guide-document/user-guide-document-dialog.html',
                controller: 'UserGuideDocumentDialogController',
                controllerAs: 'vm',
                backdrop: 'static',
                size: 'lg',
                resolve: {
                    entity: ['UserGuideDocument', function(UserGuideDocument) {
                        return userGuideDocument;
                    }]
                }
            }).result.then(function() {
                // $state.go('user-guide-document', null, { reload: 'user-guide-document' });
            }, function() {
                // $state.go('^');
            });
        };
        loadAll();

        function loadAll () {
            UserGuideDocument.getAllUserGuideDocumentsWithAuthorities({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
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
                vm.queryCount = vm.totalItems;
                vm.userGuideDocuments = data;
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
    }
})();
