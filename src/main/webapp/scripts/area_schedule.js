
  $('.date-select').hide();
  $('.activity-info').hide();
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

    //get the hour
    Date.prototype.myGetHour = function(){
        var utcDate = this.toUTCString(); //returns correct date as Day, Date Month Year time
        utcDate = utcDate.slice(17, 19);
        return utcDate;
    }

    var container1 = document.getElementById("select-date");
    var content1 = container1.innerHTML;
    console.log("content1");
    console.log(content1);
    //reload part of page
     var container2 = document.getElementById("schedule");
     var content2 = container2.innerHTML;
    $('#select-area').on('change', function(){
             var areaName = $(this).val();
             var display= [];
             $('.activity').remove();

            //find all the date that have area scheduled
            container1.innerHTML= content1;
            container2.innerHTML= content2;
            $.get('/api/activities/area/'+ areaName, function(activities){
                activities.forEach(function(activity){
                    var element = $("<option>");
                    var date = new Date (activity.time);

                    if ($.inArray(date.myToString(), display)== -1 )
                    {    display.push(date.myToString());
                        element.val(date.myToString());
                        element.text(date.myToString());
                        $('#select-date').append(element);
                    }
                });
                $('.date-select').show();

                //reload part of page
                var container2 = document.getElementById("schedule");
                var content2 = container2.innerHTML;

                $('#select-date').on('change', function(){
                    var select = $(this).val();
                    $('.activity').remove();
                    //reload
                    container2.innerHTML= content2;
                    $('#schedule').show();
                     console.log(activities);
                     //print the activities for that selected date
                     activities.forEach(function(activity){
                        var time = new Date (activity.time).myToString();
                        var hour = new Date (activity.time).myGetHour();

                        //add activity & session to html
                        if (time == select){
                            var row = document.getElementById(hour);

                            var new2 = document.createElement('td');
                            console.log(activity);
                            $(new2).text(activity.session).data('employees', activity.employees).addClass('activity');

                            row.appendChild(new2);
                        }
                    });

                    $('#schedule').show();
                    $('.activity-info').show();

                    //set up click handler to display activity info
                    $('table').on('click', '.activity', function(){
                        var employees = $(this).data('employees');
                        console.log("this is the employees: ");
                        console.log(employees);
                        $('#employees-working').empty();

                        if(employees.length){
                            //get employee objects working from ids of activity.employees array
                            employees.forEach(function(employee){

                                $.ajax({
                                    url: '/api/employees/'+employee,
                                    type: "GET",
                                    contentType: 'application/JSON',
                                    async:false
                                }).done(function(employee, textStatus, jqXHR){
                                        //attach names to employees working
                                    $('.activity-info #employees-working').append($('<li>').text(employee.name));
                                    $('.activity-info #employees-working').append($("<br>"));
                                })
                            });
                        }
                    });
                });
             });



    });

        $('#cancel').on('click', function(){
            window.location.replace('home_page_test.html');
        });
};

$(document).ready(function(){
    $.get('/api/campsessions/all', function(camp_sessions){
        console.log(camp_sessions.length);
        main(camp_sessions);
    });
});