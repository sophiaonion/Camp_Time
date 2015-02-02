var main = function(camp_sessions){

    $('#create-session').on('click', function(){
        var data = {
            startDate: $('#start-date').val(),
            endDate: $('#end-date').val(),
            skeletonSchedule: $('#skeleton-schedule').val(),
            fixedTime: $('#fixed-time').val(),
            activities: $('#activities').val(),
            sessionName: $('#session-name').val(),
            age: $('#age').val(),
            enrollCap: $('#enroll-cap').val(),
        };

        $.ajax({
            type: 'POST',
            url: '/api/campsessions',
            data: JSON.stringify(data),
            contentType: 'application/JSON',
            success: function(data){
                alert('Session Created');
                window.location.replace('home_page_test.html');
            },
            error: function(request, status, error){
                alert(error);
            }
        });

    }); //end submit-registration click handler

    $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });

};
//eventually get campers and sessions and pass to main function
$(document).ready(function(){
   $.get('/api/campsessions', function(camp_sessions){
            main(camp_sessions);
   });
});