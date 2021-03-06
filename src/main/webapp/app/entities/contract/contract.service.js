(function() {
    'use strict';
    angular
        .module('simpleServiceApp')
        .factory('Contract', Contract);

    Contract.$inject = ['$resource', 'DateUtils'];

    function Contract ($resource, DateUtils) {
        var resourceUrl =  'api/contracts/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'getCourseConsultantWorkReport': { method: 'POST', url: 'api/contracts/consultant-work-report'},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.signDate = DateUtils.convertDateTimeFromServer(data.signDate);
                        data.startDate = DateUtils.convertDateTimeFromServer(data.startDate);
                        data.endDate = DateUtils.convertDateTimeFromServer(data.endDate);
                        data.createdDate = DateUtils.convertDateTimeFromServer(data.createdDate);
                        data.lastModifiedDate = DateUtils.convertDateTimeFromServer(data.lastModifiedDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'createFreeContract': { method:'POST', url: 'api/contracts/free' },
            'createTypedContract': { method:'POST', url: 'api/contracts/create-type-contract' },
            'updateContractBalance': { method:'POST', url: 'api/contracts/update-balance' },
            'createPackageContract': {
                url: 'api/contracts/package',
                method: 'POST',
                isArray: true
            }
        });
    }
})();
