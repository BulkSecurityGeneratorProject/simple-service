(function() {
    'use strict';
    angular
        .module('simpleServiceApp')
        .factory('StudentClassLogDailyReport', StudentClassLogDailyReport);

    StudentClassLogDailyReport.$inject = ['$resource', 'DateUtils'];

    function StudentClassLogDailyReport ($resource, DateUtils) {
        var resourceUrl =  'api/student-class-log-daily-reports/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'getMonthlyStudentClassLogDailyReport': { url: 'api/student-class-log-daily-reports/monthly', method: 'POST', isArray: true},
            'getStudentClassLogDailyReportToday': { method: 'POST', url: 'api/student-class-log-daily-reports/by-date'},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.logDate = DateUtils.convertDateTimeFromServer(data.logDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'saveLogDailyReport': { method: 'POST', url: "api/student-class-log-daily-reports/save-daily-report"}
        });
    }
})();
