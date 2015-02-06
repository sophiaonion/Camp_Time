var main = function(camp_sessions){
    //make schedule designer only visible after submit dates
    $("#schedule").hide();
    $('#enter-dates').on('click', function(){
        $("#schedule").show();
            //show only available dates here?
    });



    //add autocomplete for the stuff
    $('#schedule tbody').children(':gt(0)').each(function( i1, l1 ){
        $(this).children(':gt(0)').each(function(i2, l2){
            $('#'+i1+i2).autocomplete({
                  source: [ "pool", "art", "meal", "sports",
                      "counselor time", "canoeing", "archery", "creek hopping", "check in/out", "unit"
                      , "other"],
                  autoFocus: true,
                  });
        });
    });

    //add/remove session curriculum
    $('#add-activity').click(function(){
            $('#select-from option:selected').each( function() {
                    $('#required-activities').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
            });
        });
    $('#remove-activity').click(function(){
            $('#select-to option:selected').each( function() {
                $('#select-from').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
                $(this).remove();
                         });
                     });


    //on submission, create fixed-time activities (and also required activities?)
    $('#create-session').on('click', function(){

        console.log('create session clicked');
//        var activity = function(day, time, activity){
//            this.day = day;
//            this.time = time;
//            this.activity = activity;
//             console.log(this.day, this.time, this.activity);
//        };
//
//        var activities = [];
//
//        var getCalendar = function(){
//            var days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']
//            //select rows of table body after first and then log time element
//            $('#schedule tbody').children(':gt(0)').each(function(){
//                console.log($('td:first', $(this)).text());
//
//                var time = $('td:first', $(this)).text();
//                //get time of row and then iterate over row for activities
//                $(this).children(':gt(0)').each(function(){
//                    //construct activity with day string from position of element in row, time from first column
//                    //and text of area -- might have to be .val() method for input fields
//                    activities.push(new activity(days[$(this).index() - 1], time, $(this).text()));
//                });
//            });
//        };


        //activity object to hold deets
        var activity = function(name, day, time){
            this.name = name;
            this.day = day;
            this.time = time;
        };

        //list of activities
        var activities = [];

        //puts given activities into list
        var getCalendar = function(){

            //for fixed time activities
            //loops through each row
            $('#schedule tbody').children(':gt(0)').each(function( i1, l1 ){

            //retrieves time from first column
            var time = $('td:first', $(this)).text();

                    //loop through each column in row
                    $(this).children(':gt(0)').each(function(i2, l2){

                       //idea:  make it so only looks at items in table after and before check in and check out?

                       //go through all items and make into activities
                      // if( ($(this).val().length()) != 0) {   //not working for now
                       activities.push(new activity($(this).text(), i1, time));
                      // }
                    });
            });

            //for required activities w/o fixed times
            $( "required-activities" ).each(function( index ) {
                    activities.push(new activity($(this).text(), null, null));
            });
        };

        getCalendar();

        var data = {
            startDate: $('#start-date').val(),
            endDate: $('#end-date').val(),
            activities: activities,
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