$(document).ready(function(){
    $("#dateOfBirth").datepicker({
        dateFormat:$.datepicker.ISO_8601,
        changeMonth: true,
        changeYear: true,
        yearRange: "-110:+0"
    });
    $.validator.addMethod(
            "dateFormat",
            function(value, element) {
                return value.match(/^\d\d\d\d-\d\d-\d\d$/);
            },
            "Please enter a date in the format 'yyyy-mm-dd'."
    );
    $("#registration-form").validate({
        rules: {
            dateOfBirth: {
                dateFormat: true
            }
        }
    });
});
