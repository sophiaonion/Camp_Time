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

        $('#schedule').show();
        $('caption').text(session.name + ' Schedule');
        var currentDate = new Date(session.startDate); //keep adding columns until startDate === endDate + 1
        var stopDate = new Date(session.endDate); //date types returned as strings
        console.log(currentDate);
        console.log(stopDate);
        console.log('date type: ' + typeof session.startDate);

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
        if(!(activity.time)){
            required_activities.push(activity);
        } else {
            //find offset from startDate, will be column to put activity in
            //find hour, offset from nine will be row to put activity in
            var act_date = new Date(activity.time);
            var column = dateDiffInDays(session.startDate, act_date);
            //take difference of activity time in 24 hour format and 9 since that is
            var row = act_date.getHour() - 9;

        }

        });


    }; //end buildTableSchedule
};

$(document).ready(function(){
    $.get('/api/campsessions', function(camp_sessions){
        console.log(camp_sessions.length);
        main(camp_sessions);
    });
});