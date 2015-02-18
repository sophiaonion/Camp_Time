var main = function(activities){

    $('#trigger-update').on('click', function(){
        console.log(activities.toString());
        //do the stuff here - put activities into a viewable schedule
        activities.forEach(function(value){
              var emp = $("<li>").text(value.title + "  " + value.time + "  " + value.session+
               "  " + value.activityArea);
              $(".schedules").append(emp);
          });
    });


    $('#approve-update').on('click', function(){
        window.location.replace('session_schedule.html');
    });

    $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });

};



$(document).ready(function(){
       $.get('api/activities', function(activities){
            main(activities);
        });
});