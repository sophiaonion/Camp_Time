var main = function(camp_sessions){
    //add option to be selected for each session and also tack on index data to access session in camp_sessions array
    //once selected

    Date.prototype.myToString = function(){
        var utcDate = this.toUTCString(); //returns correct date as Day, Date Month Year time
        utcDate = utcDate.slice(0, utcDate.indexOf('2015') - 1);
        utcDate = utcDate.replace(',', '');
        var pieces = utcDate.split(' '); //Day, Date, Month
        return pieces[0] + ' ' + pieces[2] + ' ' + pieces[1]
    }

    Date.prototype.myTimeString = function(){
        var hours = this.getUTCHours();
        if(Number(hours) > 12){
            return hours - 12 + ':00pm';
        } else {
            return hours + ':00am';
        }
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
        console.log(session._id);
        var deleteInfo = {_id: session._id, name: session.name};
        var ses_option = $('<option>').val(index).text(session.name).data('delete-info', deleteInfo);
        $('#session-select').append(ses_option);
    });

    $('#session-select').on('change', function(){
            camp_session = camp_sessions[$(this).val()];
            $.ajax({
                url: "/api/activities/campsession",
                data: JSON.stringify({activityIds: camp_session.activities}),
                type: "PUT",
                contentType: 'application/JSON'
            }).done(function(activities, textStatus, jxQHR){
                camp_session.activity_objects = activities;
                buildTableSchedule(camp_session);
            }).fail(alert.bind(null, 'error retrieving session activities'));
        });

    var buildTableSchedule = function(session){
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
            //add date header separately
            $('#dates').append($('<td>').text(currentDate.myToString()).addClass('empty date-header').data('date', currentDate));
            $('#schedule tr').not('#dates').each(function(){//loop through table rows adding input boxes
                //get first child of row text to get hour to append dateTime as data to be used for autocomplete options later
                var timeText = $(':first-child', this).text();
                var hours = Number($(this).data('hour'));
                var dateTime = new Date(currentDate.toString());
                dateTime.setUTCHours(hours);
                console.log(dateTime.toUTCString());
                var newCell = $('<td>').append($('<p>').attr('type', 'text').data('date-time', dateTime)).addClass('empty');
                $(this).append(newCell);

            }); //end each loop for table rows
            currentDate.setDate(currentDate.getDate() + 1);
        }; //end while loop building table

        //now need to iterate through activities putting them in proper place of schedule table table
        var required_activities = [];
        camp_session.activity_objects.forEach(function(activity){
        //if activity does not have a time field, it is a required activity
        //console.log(activity.time);
        if(!(activity.time)){
            required_activities.push(activity);
        } else {
            //find offset from startDate, will be column to put activity in
            //find hour, offset from nine will be row to put activity in
//            console.log('activity: ' + activity.title);
//            console.log('activity time string: ' + activity.time);
            var act_date = new Date(activity.time);
//            console.log('act_date: ');
//            console.log(act_date);
            var column = dateDiffInDays(new Date(session.startDate), new Date(act_date));
            console.log(column);
            //take difference of activity time in 24 hour format and 9(starting time) + 1 to get past date heading row
//            console.log('activity time: ' + act_date);
//            console.log('hour: ' + act_date.getHours());
//            console.log('minutes: should be 0: ' +act_date.getMinutes());
            var row_index = act_date.getUTCHours() - 9 + 1;
            var act_row = $('#schedule tbody tr:eq(' + row_index + ')');
            var act_cell = $('td:eq(' + column + ')', act_row);
//            console.log('row index: ' + row_index + ' column: ' + column);

            //turn input into <p> so activity is not editable
            var activity_to_place = $('<p>').text(activity.title).data('activity', activity).addClass('activity');
            $('p', $(act_cell)).replaceWith(activity_to_place);


        }

        });
        //display required activities
        //join turns array contents into string separated by parameter
        activity_titles = required_activities.map(function(act){
            return act.title;
        });
        $('#required-activities').text(activity_titles.join(', '));



        //set up autocomplete for activites
        var source = [ "pool", "art", "meal", "sports",
                    "counselor", "canoeing", "archery", "creek", "check in/out", "unit"
                     , "other"];

        //get available activities at that time
        var keyUpData;
        var previousValue = "";
        $('#schedule input').each(function(){
            var auto = this;
            $.ajax({
                url: '/api/activities/open',
                type: 'POST',
                contentType: 'application/JSON',
                data: JSON.stringify({dateTime: $(this).data('date-time')})
            }).done(function(data){
                console.log(data);
                keyUpData = data;
                $(auto).autocomplete({
                                           source: data,
                                           autoFocus: true,
                                           close: function(event, ui){
                                                if (($.inArray($(this).val(), source)) === -1){
                                                $(this).val("");
                                                }
                                           },
                                           minLength: 0
                                      }).on('keyup', function(event){
                var valid = false;
                //http://stackoverflow.com/questions/6373512/source-only-allowed-values-in-jquery-ui-autocomplete-plugin
                //for limiting allowed values
                for(index in source){
                    $(auto).val().toLowerCase();
                    if(keyUpData[index].toLowerCase().match($(auto).val().toLowerCase())){
                        valid = true;
                    }
                }
                if (!valid){
                    $(this).val(previousValue);
                } else {
                    previousValue = $(this).val()
                }
            }); //end autocomplete creation


            });//end build autocomplete/ajax done
        });//end each
    }; //end buildTableSchedule

    //set up click handler to display activity info
    $('table').on('click', '.activity', function(){
        var activity = $(this).data('activity');
        $('.activity-info #activity-title').text(activity.title);

        var act_date = new Date(activity.time);
        $('.activity-info #activity-time').text(act_date.myToString() + ' ' + act_date.myTimeString());
        $('#employees-working').empty();
        if(activity.employees.length){
            $('.activity-info #num-employees').text(activity.employees.length);
            //get employee objects working from ids of activity.employees array
            $.ajax({
                url: '/api/employees/ids',
                type: "PUT",
                data: JSON.stringify({employee_ids: activity.employees}),
                contentType: 'application/JSON'
            }).done(function(employees, textStatus, jqXHR){
                    //attach names to employees working

                    employees.forEach(function(employee){
                        $('#employees-working').append($('<li>').text(employee.name).
                            data('employee', employee)); //get employee name for later
                    });
            }).fail(alert.bind(null, 'error getting employee objects of activity'));
                } else { //no employees working activity
                    $('.activity-info #employees-working').hide();
                    $('.activity-info #num-employees').text('0');
                }
            $('.selected').removeClass('selected');
            $(this).addClass('selected');
            });

    $('#session-select').trigger('change');


     $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });
};

$(document).ready(function(){
    $.get('/api/campsessions', function(camp_sessions){
        console.log(camp_sessions.length);
        main(camp_sessions);
    });
});