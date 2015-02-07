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


    }; //end buildTableSchedule
};

$(document).ready(function(){
    $.get('/api/campsessions', function(camp_sessions){
        console.log(camp_sessions.length);
        main(camp_sessions);
    });
});