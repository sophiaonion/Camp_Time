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
        console.log('create session clicked');
        var activity = function(day, time, activity){
                            this.day = day;
                            this.time = time;
                            this.activity = activity;
                            console.log(this.day, this.time, this.activity);
                       };
        var activities = [];
        var getCalendar = function(){
            var days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']
//select rows of table body after first and then log time element
            $('#schedule tbody').children(':gt(0)').each(function(){
                console.log($('td:first', $(this)).text());

                var time = $('td:first', $(this)).text();
                //get time of row and then iterate over row for activities
                $(this).children(':gt(0)').each(function(){
                    //construct activity with day string from position of element in row, time from first column
                    //and text of area -- might have to be .val() method for input fields
                    activities.push(new activity(days[$(this).index() - 1], time, $(this).text()));
                });
            });
        };

        getCalendar();



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

//        $.ajax({
//            type: 'POST',
//            url: '/api/campsessions',
//            data: JSON.stringify(data),
//            contentType: 'application/JSON',
//            success: function(data){
//                alert('Session Created');
//                window.location.replace('home_page_test.html');
//            },
//            error: function(request, status, error){
//                alert(error);
//            }
//        });

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