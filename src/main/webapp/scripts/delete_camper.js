var main = function(campers){

        campers.forEach(function(camper){

                        var element = $("<option>");
                         element.text(camper.name);
                         $('.campers-list').append(element);

                });

        var autocomplete_source = campers.map(function(camper){
            console.log(camper);
            return {
                value: camper.name,
                ID: camper._id
            };
        });

        var selected_camperID;
            var uponSelect = function(event, ui){ //upon selection set camperID to send when submitting registration
                 //ui.item is selected item, has fields set in source
                 selected_camperID = ui.item.ID;
                 //get the list of registered sessions for this particular camper

                 console.log(ui.item.ID);
             };

        $('#camper-name').autocomplete({
            source: autocomplete_source, //set possible options
            autoFocus: true, //automatically select closest match
            select: uponSelect,
            messages: {//get rid of helper text that jqueryUI inserts
                noResults: '',
                results: function(){}//usually appends selected result text to bottom
            }
        });

        $('#delete-camper').on('click', function(){
            console.log('submit clicked');
            console.log(selected_camperID);

            $.ajax({
                type: 'DELETE',
                url: '/api/campers/' + selected_camperID,
                contentType: 'application/JSON',
                success: function(data){
                 if (confirm("Deleted Camper Account: Update schedule now?") == true) {
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

        $('#cancel-camper').on('click', function(){
            window.location.replace('home_page_test.html');
        });
};

$(document).ready(function(){
    $.get('api/campers/all', function(campers){
        main(campers);
    });
});