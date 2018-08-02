IDAP = IDAP || {};
IDAP.stubIdp = IDAP.stubIdp || {};
IDAP.stubIdp.createUserView = {

    create: function() {
        var $errorMessageList = $("#error-message-list");
        var $successMessage = $("#success-message");

        function resetErrorList() {
            $errorMessageList.empty();
            $errorMessageList.hide();
        }

        function resetSuccessMessage() {
            $successMessage.empty();
            $successMessage.hide();
        }

        return {

            setValidationErrors: function(validationErrors) {
                resetErrorList();
                resetSuccessMessage();

                for (var i = 0; i < validationErrors.length; ++i) {
                    $errorMessageList.append($('<li>').text(validationErrors[i]));
                }

                $errorMessageList.show(400);
                window.scroll(0, 0);
            },

            setValidationSuccess: function(message) {
                resetErrorList();
                resetSuccessMessage();

                $successMessage.append('<span>').text(message);
                $successMessage.show(400);
            },

            user: function () {
                var $user = $("#createUserForm");
                var firstName = getMdsValueOrNull($user, "#firstName:first");
                var surname = getMdsValueOrNull($user, "#surname:first");
                var username = getValueOrNull($user, "#username:first");
                var password = getValueOrNull($user, "#password:first");

                var dateOfBirth = getMdsValueOrNull($user, "#dateOfBirth:first");

                var addressLine1 = getValueOrNull($user, "#addressLine1");
                var addressLine2 = getValueOrNull($user, "#addressLine2");
                var postCode = getValueOrNull($user, "#postCode");
                var address = null;
                if (addressLine1 != null || addressLine2 != null || postCode != null) {
                    address = {
                        lines: [addressLine1, addressLine2],
                        postCode: postCode,
                        verified: true
                    };
                }

                var levelOfAssurance = getValueOrNull($user, "#levelOfAssurance");

                return {firstName: firstName, surnames: [ surname ], address: address, dateOfBirth: dateOfBirth, username: username, password: password, levelOfAssurance: levelOfAssurance};

                function getValueOrNull($element, selector) {
                    var value = $element.find(selector).val();
                    if (value != null && value.length > 0) {
                        return value;
                    }

                    return null;
                }

                function getMdsValueOrNull($element, selector) {
                    var value = $element.find(selector).val();
                    if (value != null && value.length > 0) {
                        return {value: value, verified: true};
                    }

                    return null;
                }
            }
        }
    }
};