var main = function(employees){

    //add/remove session curriculum
    $('#add-certification').click(function(){
            $('#select-from option:selected').each( function() {
                    $('#certifications').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
            });
        });

        $('#remove-certification').click(function(){
            $('#select-to option:selected').each( function() {
                $('#select-from').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
                $(this).remove();
                         });
                     });



$('#add-employee').on('click', function(){

    //choose gender (man/woman)
    var gender;
    if ($('#man').is(':checked')) {
        gender = "man";
    }
    else {//generally assume will be woman
        gender = "woman";
    }

    //put certifications into list
    var certifications = [];
    $( "certifications" ).each(function( index ) {
        certifications.push($(this).text());
    });

    //save all info into data
    var data = {
        name: $('#employee-name').val(),
        age: $('employee-age').val(),
        gender: gender,
        job: $('#job').val(),
        certifications: certifications
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

});

     $('#cancel').on('click', function(){
        window.location.replace('home_page_test.html');
    });
}
//eventually get employees and pass to main function
$(document).ready(function(){
       $.get('/api/employees', function(employees){
                main(campers);
       });
});

$(document).ready(main);
