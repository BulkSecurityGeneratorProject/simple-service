(function() {
    'use strict';
    angular
        .module('simpleServiceApp')
        .factory('ClassArrangement', ClassArrangement);

    ClassArrangement.$inject = ['$resource', 'DateUtils'];

    function ClassArrangement ($resource, DateUtils) {
        var resourceUrl =  'api/class-arrangements/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'loadArrangements': { url:'api/class-arrangements/by-class-id/:id', method: 'GET', isArray: true},
            'getClassArrangementsToday': { url:'api/class-arrangements/today', method: 'GET', isArray: true},
            'getAllClassSchedules': { url:'api/class-arrangements/all', method: 'GET', isArray: true},
            'searchSchedulesInRange': { url:'api/class-arrangements/get-by-range', method: 'POST', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.startDate = DateUtils.convertDateTimeFromServer(data.startDate);
                        data.startDateReal = data.startDate;
                        data.endDate = DateUtils.convertDateTimeFromServer(data.endDate);
                        data.endDateReal = data.endDate;
                        data.createdDate = DateUtils.convertDateTimeFromServer(data.createdDate);
                        data.lastModifiedDate = DateUtils.convertDateTimeFromServer(data.lastModifiedDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'generateClassSchedule': { url: 'api/class-arrangements/generate-by-rule/:id', method: 'GET'}
        });
    }
})();