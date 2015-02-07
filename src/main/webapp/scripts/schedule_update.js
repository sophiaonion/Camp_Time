var main = function(activities){

    $('#approve-update').hide();
    $('#trigger-update').on('click', function(){

        //do stuff - idk how this works, just needs to return viable updated schedule
        $.ajax({
            type: 'GET',
            url: '/api/activities',
//            data: JSON.stringify(data),
//            contentType: 'application/JSON',
//            success: function(data){
//                alert('camper registered for this session');
//                window.location.replace('home_page_test.html');
//            }

        }



        $('#approve-update').show();
    });


    $('#approve-update').on('click', function(){



    });

    $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });

};

$(document).ready(main);