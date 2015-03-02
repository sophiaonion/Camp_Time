var button_handler = function(){
    console.log('button handler hi');

    $('#delete-session').on('click', function(event){
        $(this).hide();
        $('.confirm-delete').show().css('visibility', 'visible');
        $('#submit-delete').on('click', function(event){
            var sessionToDelete = $('#session-select option:selected').data('delete-info');
            $.ajax({
                url: '/api/campsession',
                type: 'DELETE',
                contentType: 'application/JSON',
                data: JSON.stringify(sessionToDelete)
            }).done(function(data){
                $('confirm-delete').hide();
                var message = $('<h4>').text('Session deleted').id('message');
                $('.session-select-con').append(message);
                setTimeout(function(){
                    message.remove();
                    $('#delete-session').show();
                }, 2000);
            })
        });
        $('#cancel-delete').on('click', function(event){
            $('.confirm-delete').hide();
            $('#delete-session').show();
        });

        //$('.confirm-delete').mouseout($('#cancel-delete').trigger('click'));
    });

    $('#edit-employees').click(function(event){
        var activity = $('.selected').data('activity');
        if(!activity) return; //no activity selected
        //append employees of activity to working select element

        $('.edit-section').load('/scripts/sessionschedule/edit_employees.html');





        //get available employees at time and append to available select
        console.log('activity time: ' + activity.time)
        console.log(activity);
        $.ajax({
        url: '/api/employees/time',
        data: JSON.stringify(activity.time),
        type: 'PUT',
        contentType: 'application/JSON'
        }).done(function(employees){

            employees.forEach(function(emp){
                var nextEmp = $('<option>').text(emp.name).data('employee', emp);
                console.log(nextEmp.data('employee')._id);
                $('.available-emp').append(nextEmp);
            });

            if (activity.employees.length){//check if employees uninitialized or 0 will be false skip initialization
                $('#employees-working li').length;
                $('#employees-working li').each(function(){
                    var employee = $(this).data('employee');
                    console.log('in each');
                    console.log(employee.name);
                    console.log($('.working-emp'));
                    $('.working-emp').append($('<option>').text(employee.name).data('employee', employee));
                });
            }

        }).fail(alert.bind(null, 'getting available employees failed'))
        


    });

    $('.cancel').click(function(event){
        $('.activity-control').show();
        $('.confirm-buttons').hide();
    });
};

$(document).ready(button_handler);