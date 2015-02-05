var main = function(camp_sessions){
$("#schedule").hide();

    $('#enter-dates').on('click', function(){
        //print out schedule decider for fixed time activities
        $("#schedule").show();

        //enter skeleton schedule activities here?

    })

    $('#add-activity').click(function(){
            $('#select-from option:selected').each( function() {
                    $('#select-to').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
            });
        });

        $('#remove-activity').click(function(){
            $('#select-to option:selected').each( function() {
                $('#select-from').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
                $(this).remove();
                         });
                     });

    $('#create-session').on('click', function(){
        var data = {
            startDate: $('#start-date').val(),
            endDate: $('#end-date').val(),
            skeletonSchedule: $('#skeleton-schedule').val(),
            fixedTime: $('#fixed-time').val(),
            activities: $('#activities').val(),
            name: $('#session-name').val(),
            ageGroup: $('#age').val(),
            enrollmentCap: $('#enroll-cap').val(),
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