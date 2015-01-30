var main = function(){
  //initialize ordinary text input into JQueryUI datepicker objects
  //can set restrictions on user input as well
  $('#datepicker-start').datepicker();
  $('#datepicker-end').datepicker();

  $('#submit-activity').click(function(){
    console.log($('#datepicker-start').val());

     var newAct = JSON.stringify({
        title:

     });

    $.ajax({
        url: '/api/activities',



    });
  });

};

$(document).ready(main);