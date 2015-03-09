var main = function(employees){

    var userRole;
    $.get('/api/login/role', function(role){
        userRole=role.replace(/\s+/g, '');
     });

    //add/remove session curriculum
    $('#add-certification').click(function(){
            $('#select-from option:selected').each( function() {
                    $('#certifications').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
            });
        });

        $('#remove-certification').click(function(){
            $('#certifications option:selected').each( function() {
                $(this).remove();
            });
        });



$('#add-employee').on('click', function(){

    //choose gender (man/woman)
    var gender;
    if ($('#not-woman').is(':checked')) {
        gender = "notMan";
    }
    else {//generally assume will be woman
        gender = "woman";
    }

    //put certifications into list
    var certifications = [];
var cert;
    $( "#certifications option" ).each(function(index) {

        if($(this).text() == "First Aid") {cert="first-aid";}
        else if($(this).text() == "CPR") {cert="cpr";}
        else if($(this).text() == "Health Supervisor") {cert="health";}
        else if($(this).text() == "Lifeguard") {cert="lifeguard";}
        else if($(this).text() == "Art Director") {cert="art";}
        else if($(this).text() == "Nature Director") {cert="nature";}
        else if($(this).text() == "Archery") {cert="archery";}
        else if($(this).text() == "Camp Store") {cert="store";}
        else if($(this).text() == "Driving") {cert="drive";}
        certifications.push(new String(cert))

    });



    //save all info into data
    var data = {
        name: $('#employee-name').val(),
        age: $('#employee-age').val(),
        gender: gender,
        job: $('#job').val(),
        startBreak: $('#start-break').val(),
        intervalBreak: $('#interval-break').val(),
        certifications: certifications,
        activities: []
    };

    $.ajax({
        type: 'POST',
        url: '/api/employees',
        data: JSON.stringify(data),
        contentType: 'application/JSON',
        success: function(data){
             alert('Employee Added.');
             window.location.replace('home_page_test.html');
        },
        error: function(request, status, error){
             alert(error);
        }
    });

    var useraccount = {
            name: $('#user-name').val(),
            password: $('#password').val(),
            roles: [$('#job').val()]
        };


        $.ajax({
            type: 'POST',
            url: '/api/users',
            data: JSON.stringify(useraccount),
            contentType: 'application/JSON',
            success: function(data){
                 alert('User Created.');
                 window.location.replace('index.html');
            },
            error: function(request, status, error){
                 alert(error);
            }
        });

});

     $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });
};

$(document).ready(main);
