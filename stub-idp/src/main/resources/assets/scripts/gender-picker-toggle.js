$('#include-gender').on('click',function(){
    var checkbox = $(this),
        panel = $('#include-gender-questions');
    panel.toggleClass('hidden',!checkbox.is(':checked'));
});
