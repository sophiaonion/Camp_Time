var main = function(employees){//get the format we want and correct time zone for date String

    //counselor selection
    employees.forEach(function(value){
        if(value.job == "counselor") {
        var emp = $('<option>').text(value.name).val(value._id);
        $('.available-emp').append(emp);
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

//set build table function to trigger when both end and start dates are set
var tableBuilt = false;
(function(){
    var startDate, endDate; //following functions will be able to directly access

    var buildTable = function(){ //constructs schedule table
        $('.remove').remove();
        sDate = new Date(startDate);
        var daysOfSession = dateDiffInDays(sDate, new Date(endDate)) + 1; //+1 include last day in for loop then
        var i = 0;

        for(i; i<daysOfSession; i++){
            var dateHead = $('<td>').text(sDate.myToString()); //create date cell to add
            dateHead.attr('id', '0' + i.toString()); //id will always be 0dayofcamp
            dateHead.addClass('remove');
            $('#dates').append(dateHead);
            var toClone = $('.clone'); //get clone column
            console.log('loop');
            toClone.each(function(index){
                var next = $(this).clone(true);
                next.removeClass('clone'); //otherwise it will be included in elements to clone next for loop iteration
                var cloneId = $('input', this).attr('id');
                next.attr('id', cloneId + i.toString());
                next.addClass('remove');
                $(this).parent().append(next);
            }); //end forEach cloning loop
            sDate.setDate(sDate.getDate() + 1);
        } //end for loop through dates

        //set up autocomplete for activites
        $('#schedule input').each(function(){
            $(this).autocomplete({
                                       source: [ "pool", "art", "meal", "sports",
                                       "counselor", "canoeing", "archery", "creek", "check in/out", "unit"
                                        , "other"],
                                       autoFocus: true,
                                       //select: uponSelect
                                  });
        }); //end autocomplete creation
        tableBuilt = true;
    }; //end build table function


    $('#start-date').on('change', function(){
        startDate = $(this).val();
        if (startDate && endDate){//if startDate or endDate is null will evaluate to false
            buildTable();
        }
    });

    $('#end-date').on('change', function(){
        endDate = $(this).val();
        if (startDate && endDate){
            buildTable();
        }

    });
}()); //end closure, keeps endDate, startDate variables contained within the scope of function

    $('#enter-dates').on('click', function(){
        //print out schedule decider for fixed time activities
        console.log(tableBuilt);
        if(tableBuilt){
            $("#schedule").show();
        }
    });

    //add/remove session curriculum
    $('#add-activity').click(function(){
            $('#select-from option:selected').each( function() {
                    $('#required-activities').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
            });
        });

        $('#remove-activity').click(function(){
            $('#required-activities option:selected').each( function() {
                $(this).remove();
             });
        });

    //add counselors to session
    $('.add-counselor').click(function(){
            $('.available-emp option:selected').each( function() {
                    $('.working-emp').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
            });
        });

    $('.remove-counselor').click(function(){
        $('.working-emp option:selected').each( function() {
            $(this).remove();
        });
    });


    //on submission, create fixed-time activities (and also required activities?)
    $('#create-session').on('click', function(){

        console.log('create session clicked');


        //activity object to hold deets
        var activity = function(name, day, time){
            this.title = name;
            this.day = day;
            this.time = time;
            this.employees = []
        };

        //list of activities
        var activities = [];
        var counselors = [];

        //puts given activities into list
        var getCalendar = function(){

            //for fixed time activities
            //loops through each row
            //parameters of callback index, value/element(copy
            $('#schedule tbody').children(':gt(0)').each(function( i1, l1 ){

            //retrieves time from first column
            var time = $('td:first', $(this)).text();

                    //loop through each column in row...:gt(1) moves past times and clone columns
                    $(this).children(':gt(1)').each(function(i2, l2){
                        console.log($(this).index());
                       //idea:  make it so only looks at items in table after and before check in and check out?

                       //go through all items and make into activities
                      if($('input', $(this)).val() !== "") {   //not working for now
//                        console.log('name: ' + $('input', $(this).val()));
//                        console.log('activity time: ' + time);
//                        console.log('index: ' + i2.toString() );//day offset from startDate
                        activities.push(new activity($('input', $(this)).val(), i2.toString(), time));
                        }
                    });
            });

            //for required activities w/o fixed times
            $( "#required-activities option" ).each(function( index ) {
                    activities.push(new activity($(this).text(), null, null));
            });

            $( ".working-emp option" ).each(function( index ) {
                   counselors.push($(this).val());
            });
        };

        getCalendar();



        var data = {
            startDate: $('#start-date').val(),
            endDate: $('#end-date').val(),
            activities: activities,
            counselors: counselors,
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
                 if (confirm("Session Created: Update schedule now?") == true) {
                     window.location.replace('trigger_update.html');
                 } else {
                     window.location.replace('home_page_test.html');
                 }
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
       $.get('api/employees', function(employees){
            main(employees);
        });
});