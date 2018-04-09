IDAP = IDAP || {};
IDAP.stubIdp = IDAP.stubIdp || {};
IDAP.stubIdp.dataPoster = {
    create: function () {
        return {
            post: function (data, resource, successCallback, failureCallback) {
                $.ajax({
                    url: resource,
                    type: "POST",
                    data: JSON.stringify(data),
                    dataType: "json",
                    contentType: "application/json; charset=UTF-8",
                    success: function (html) {
                        successCallback(html.message);
                    },
                    error: function (xhr) {
                        console.error(xhr.responseText);
                        var errorMessages = $.parseJSON(xhr.responseText).errors;
                        failureCallback(errorMessages);
                    }
                });
            }
        }
    }
};
