var button_handler = function(){

    $('#delete-session').on('click', function(event){
        $(this).hide();
        $('.confirm-delete').show().css('visibility', 'visible');
        $('#submit-delete').on('click', function(event){
            var sessionToDelete = $('#session-select option:selected').data('delete-info');
            $('#session-select option:selected').remove();
            $('#session-select').trigger('change');
            $.ajax({
                url: '/api/campsessions',
                type: 'DELETE',
                contentType: 'application/JSON',
                data: JSON.stringify(sessionToDelete)
            }).done(function(data){
                $('.confirm-delete').hide();
                var message = $('<h4>').text('Session deleted').addClass('message').css('display', 'inline-block');
                $('.session-select-con').append(message);

                setTimeout(function(){
                    message.remove();
                    $('#delete-session').show();
                }, 1000);
            });
        });
        $('#cancel-delete').on('click', function(event){
            $('.confirm-delete').hide();
            $('#delete-session').show();
        });

        //$('.confirm-delete').mouseout($('#cancel-delete').trigger('click'));
    });

    //handling activity changes
    $('#submit-schedule-changes').on('click', function(event){
        //activity object to hold deets
        console.log('submit activities called');
        var activity = function(name, session, time){
            this.title = name;
            this.session = session;
            this.time = time;
            this.employees = [];
            this.fixed = true;
            this.isSet = true;
        };
        var possibleActivities = {
            pool: "",
            art: "",
            meal: "",
            sports: "",
            counselor: "",
            canoeing: "",
            archery: "",
            creek: "",
            "check in/out": "",
            unit: "",
            other: ""
        };
        console.log('possible acts has pool');
        console.log(possibleActivities.hasOwnProperty('pool'));

        var newActs = $('#schedule input').filter(function(index){
            console.log($(this).val());
            console.log(possibleActivities.hasOwnProperty($(this).val()));
            if(possibleActivities.hasOwnProperty($(this).val()))
            {
            return true;
            } else {return false;}
        });

        console.log('newActs: ');
        console.log(newActs);
        var sessionName = $('#session-select option:selected').text();
        //map each input to activity object
        //using input's text, date-time data, session
        newActs = newActs.toArray().map(function(item, index){
            return new activity($(item).val(), sessionName, $(item).data('date-time'));
        });

        console.log('activities: ');
        console.log(newActs);
        if(!newActs.length) return; //no activities to add
        $.ajax({
            url: '/api/activities/multi',
            type: 'POST',
            contentType: 'application/JSON',
            data: JSON.stringify(newActs)
        }).done(function(campsession, textStatus, jqXHR){
            var message = $('<h4>').text('Activities added').addClass('message').css('display', 'inline-block');
            $('.activity-change-con').append(message);
            $('#session-select option:selected').data("campsession", campsession);
            setTimeout(function(){
                $('.activity-change-con .message').remove()}, 1000);
            $('#session-select').trigger('change');
        }).fail(alert.bind(null, 'Error adding activities to session'));

    });//end submit activity changes click

    $('#cancel-schedule-changes').on('click', function(event){
        $('#schedule input').val("");
    });

//get calls to populate employees
    $('table').on('populate:employee-select', '.activity', function(event){
        console.log('triggered');
        var activity = $('.selected').data('activity');
        if(!activity) return; //no activity selected
        //append employees of activity to working select element
        //working emps built from session schedule activity click handler
        $('.available-emp').empty();



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
                  var certString = "    ";
                  if(emp.certifications){
                      emp.certifications.slice(0, -1).forEach(function(cert){
                          certString += cert + ", ";
                      });
                      certString += emp.certifications.slice(-1);
                  }

                var nextEmp = $('<option>').text(emp.name + '     ' + certString).data('employee', emp);
                console.log(nextEmp.data('employee')._id);
                $('.available-emp').append(nextEmp);
              });
        }).fail(alert.bind(null, 'getting available employees failed'));




    });

    $('.cancel').click(function(event){
        $('.activity-control').show();
        $('.confirm-buttons').hide();
        $(this).trigger('populate:employee-select');
    });
};

$(document).ready(button_handler);