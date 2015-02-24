var main = function(campers){

        var autocomplete_source = campers.map(function(camper){
            return {
                value: camper.name,
                ID: camper._id
            };
        });

        $('#camper-name').autocomplete({
            source: autocomplete_source, //set possible options
            autoFocus: true, //automatically select closest match
//            select: uponSelect,
            messages: {//get rid of helper text that jqueryUI inserts
                noResults: '',
                results: function(){}//usually appends selected result text to bottom
            }
        });

        $('#delete-camper').on('click', function(){
            var data = {
                camperID: $('#camper-name option:selected').data('ID'),
            };
            console.log('submit clicked');
            console.log(data.camperID);

            $.ajax({
                type: 'DELETE',
                url: '/api/campers/' + data.camperID,
                data: JSON.stringify(data),
                contentType: 'application/JSON',
                success: function(data){
                    alert('Successfully deleted camper account');
                    window.location.replace('home_page_test.html');
                },
                error: function(request, status, error){
                    alert(error);
                }
            });

        }); //end submit-registration click handler

        $('#cancel-camper').on('click', function(){
            window.location.replace('home_page_test.html');
        });
};

$(document).ready(function(){
    $.get('api/campers', function(campers){
        main(campers);
    });
});