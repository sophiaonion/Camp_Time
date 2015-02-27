$('.view-schedule').hide();
$('.customer-options').hide();
$('.counselor-options').hide();
var main = function(){

    console.log("try to get current role");

    $.get('/api/role', function(role){
         role = role.replace(/\s+/g, ''); //eliminate unnessacery whitespace, from stackoverflow
         if(role == "counselor"){
            console.log("first");
            $('.admin-options').hide();
            $('.admin-view-schedule').hide();
            $('.spec-schedule').hide();
            $('.trigger-update').hide();
            $('.view-schedule').show();
            $('.counselor-options').show();
         }

         if (role == "specialty"){
            console.log("second");
             $('.admin-options').hide();
             $('.admin-view-schedule').hide();
             $('.counselor-schedule').hide();
             $('.trigger-update').hide();
         }

        if (role == "customer"){
            console.log("third");
             $('.admin-options').hide();
             $('.admin-view-schedule').hide();
             $('.trigger-update').hide();
             $('.spec-schedule').hide();
             $('.view-schedule').show();
             $('.customer-options').show();
        }

    });

}

$(document).ready(main);