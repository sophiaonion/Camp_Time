
$('.date-select').hide();
$('.staff-select-con').hide();
var main = function(){

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

    console.log("try to get current role");
    $.get('/api/login/role', function(role){
         console.log(role);
         role = role.replace(/\s+/g, '');
         if (role == "admin"){
            $('.staff-select-con').show();
            //get the list of employees
            $.get('/api/employees', function(employees){
             //append as option elements for campsession collect
                employees.forEach(function(employee){
                    var element = $("<option>");
                    element.val(employee.name);
                    element.text(employee.name);
                    element.data('employeeID', employee._id);
                     $('#select-staff').append(element);
             });
            });
         }

    });

    var container1 = document.getElementById("select-date");
    var content1 = container1.innerHTML;
    select_staff_dropdown(content1, container1);

     $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });
};


$(document).ready(main);

function select_staff_dropdown(content1, container1){
        $('#select-staff').on('change', function(){
                 var staffId = $(this).data('employeeID');
                 console.log(staffId);
                 var display= [];

                //find all the date that have area scheduled
                container1.innerHTML= content1;
                $.get('/api/employees/'+ staffId, function(activities){
                    activities.forEach(function(activity){
                        var element = $("<option>");
                        var date = new Date (activity.time);

                        //test if there's already an activity happening on particular day
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

                                var new1 = document.createElement('td');
                                new1.innerHTML=activity.title;

                                var new2 = document.createElement('td');
                                new2.innerHTML=activity.session;

                                row.appendChild(new1);
                                row.appendChild(new2);
                            }
                        });
                    });
                 });



        });

}