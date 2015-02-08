var main = function(activities){

    $('#approve-update').hide();
    $('#trigger-update').on('click', function(){


        //do the stuff here - put activities into a viewable schedule


        $('#approve-update').show();
    });


    $('#approve-update').on('click', function(){
        //post activities copy as new activities?
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