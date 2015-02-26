var edit_employees_setup = function() {
    //only string _id of each employee
    var remove_employees = [];
    var add_employees = [];
    console.log($('body').data('hi'));

    $('.edit-employees .remove').click(function(event){
        console.log('remove called');
        $('.working-emp option:selected').each(function(){
            var employee = $(this).data('employee');
            var _id = employee._id;
            $(this).detach();//detach from working container
            $('.available-emp').append($(this));

 //add to available

            var indexIfIn = $.inArray(_id, add_employees);
            if(indexIfIn !== -1){
                //detach one item starting at index given
                add_employees.splice(indexIfIn, -1);
            }
            remove_employees.push(_id);
        });
    });

    $('.edit-employees .add').click(function(event){
        console.log('add clicked');
        $('.available-emp option:selected').each(function(){
            var _id = $(this).data('employee')._id;
            console.log('from add: ' + _id);
            var indexIfIn = $.inArray(_id, remove_employees);
            $(this).detach();
            $('.working-emp').append($(this));

            if(indexIfIn !== -1){
                //detach one item starting at index given
                remove_employees.splice(indexIfIn, -1);
            }
            add_employees.push(_id);
        });
    });


    $('.ok').click(function(event){
        $('.edit-section').empty();
        $('.edit-section').hide();
        var activity = $('.selected').data('activity');
        console.log('activity id' + activity._id);

        $.ajax({
                type: 'PUT',
                url: '/api/employees/activities/add',
                data: JSON.stringify({
                    employees: add_employees,
                    activity_id: activity._id
                }),
                contentType: 'application/JSON'
                }).done(function(activity){
                    console.log(activity.name)
                });
            }); //end add_employees forEach


            $.ajax({
                type: 'PUT',
                url: '/api/employees/activities/remove',
                data: JSON.stringify({
                    employees: remove_employees,
                    activity_id: activity._id
                }),
                contentType: 'application/JSON'
                }).done(function(activity){
                    console.log(activity.name)
                });

        }); //end employee click

    $('.edit-employees .cancel').click(function(event){
        $('.edit-section').empty();
        $('.activity-info').show();
    });

};

$(document).ready(edit_employees_setup);