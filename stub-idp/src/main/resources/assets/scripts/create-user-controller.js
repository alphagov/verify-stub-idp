IDAP = IDAP || {};
IDAP.stubIdp = IDAP.stubIdp || {};
IDAP.stubIdp.createUserController = {
    create: function (view, dataPoster) {

        return {
            submitForm: function (targetUri) {
                var submitData = [view.user()];
                dataPoster.post(
                    submitData,
                    targetUri,
                    function (message) {
                        window.scroll(0, 0);
                        view.setValidationSuccess(message);
                    },
                    function (errorMessages) {
                        view.setValidationErrors(errorMessages);
                    }
                );
            }
        }
    }
};