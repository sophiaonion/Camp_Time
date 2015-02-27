var main = function(camp_sessions){

    $.get('/api/role', function(role){
        role = role.replace(/\s+/g, '');

        if (role == "customer"){
            console.log("third");
             $('.activity-info').hide();
        }


    });

    Date.prototype.myToString = function(){
        var utcDate = this.toUTCString(); //returns correct date as Day, Date Month Year time
        utcDate = utcDate.slice(0, utcDate.indexOf('2015') - 1);
        utcDate = utcDate.replace(',', '');
        var pieces = utcDate.split(' '); //Day, Date, Month
        return pieces[0] + ' ' + pieces[2] + ' ' + pieces[1]
    }

    //from stack overflow utility function for date difference
    //http://stackoverflow.com/questions/3224834/get-difference-between-2-dates-in-javascript
    // a and b are javascript Date objects
    function dateDiffInDays(a, b) {
      // Discard the time and time-zone information.
      var _MS_PER_DAY = 1000 * 60 * 60 * 24; //milliseconds per day
      var utc1 = Date.UTC(a.getFullYear(), a.getMonth(), a.getDate()); //UTC must be used so timezones don't interfere...
      var utc2 = Date.UTC(b.getFullYear(), b.getMonth(), b.getDate());

      return Math.floor((utc2 - utc1) / _MS_PER_DAY);
    }

    camp_sessions.forEach(function(session, index){
        var ses_option = $('<option>').val(index).text(session.name);
        $('#session-select').append(ses_option);
    });

    $('#session-select').on('change', function(){
        buildTableSchedule(camp_sessions[$(this).val()]);
    });

    var buildTableSchedule = function(session){
        //console.log('buildTableSchedule called');
        $('#schedule').show();
        $('caption').text(session.name + ' Schedule');
        var currentDate = new Date(session.startDate); //keep adding columns until startDate === endDate + 1
        var stopDate = new Date(session.endDate); //date types returned as strings
        stopDate.setDate(stopDate.getDate() + 1);
        //console.log(currentDate);
        //console.log(stopDate);
        //console.log('date type: ' + typeof session.startDate);

        $('.empty').remove() //remove last selected session table
        while(currentDate.valueOf() !== stopDate.valueOf()){
            $('#dates').append($('<td>').text(currentDate.myToString()).addClass('empty')); //add date header separately
            $('#schedule tr').not('#dates').each(function(){//loop through table rows adding input boxes

                var newCell = $('<td>').append($('<input>').attr('type', 'text')).addClass('empty');
                $(this).append(newCell);

            }); //end each loop for table rows
            currentDate.setDate(currentDate.getDate() + 1);
        }; //end while loop building table

        //now need to iterate through activities putting them in proper place of schedule table table
        var required_activities = [];
        session.activities.forEach(function(activity){
        //if activity does not have a time field, it is a required activity
        //console.log(activity.time);
        if(!(activity.time)){
            required_activities.push(activity);
        } else {
            //find offset from startDate, will be column to put activity in
            //find hour, offset from nine will be row to put activity in
            var act_date = new Date(activity.time);
            var column = dateDiffInDays(new Date(session.startDate), new Date(act_date)) + 1;

            //take difference of activity time in 24 hour format and 9(starting time) + 1 to get past date heading row
            var row_index = act_date.getHours() - 9 + 1;
            var act_row = $('#schedule tbody tr:eq(' + row_index + ')');
            var act_cell = $('td:eq(' + column + ')', act_row);

            //turn input into <p> so activity is not editable
            var activity_to_place = $('<p>').text(activity.title).data('activity', activity).addClass('activity');
            $('input', $(act_cell)).replaceWith(activity_to_place);


        }

        });
        //display required activities
        //join turns array contents into string separated by parameter
        activity_titles = required_activities.map(function(act){
            return act.title;
        });
        $('#required-activities').text(activity_titles.join(', '));

         //set up click handller to display activity info
        $('.activity').on('click', function(){
            var activity = $(this).data('activity');
            $('.activity-info #activity-title').text(activity.title);
            var act_date = new Date(activity.time);
            $('.activity-info #activity-time').text(act_date.myToString() + ' ' + act_date.getHours() + ':00');
            console.log("activity is: ");
            console.log(activity);
            console.log(activity._id);

            if(activity.employees){
                $('.activity-info #num-employees').text(activity.employees.length);
                activity.employees.forEach(function(employee){
                    $('.activity-info #employees-working').append($('<li>').text(employee.name));
                });
            } else {
            $('.activity-info #employees-working').hide();
            $('.activity-info #num-employees').text('0');}
            $('.selected').removeClass('selected');
            $(this).addClass('selected');

        });
    }; //end buildTableSchedule
    //build schedule for default selected session
    buildTableSchedule(camp_sessions[$('#session-select').val()]);


     $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });
};

$(document).ready(function(){
    $.get('/api/campsessions', function(camp_sessions){
        main(camp_sessions);
    });
});