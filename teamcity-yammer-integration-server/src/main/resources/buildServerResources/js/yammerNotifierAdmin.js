var YammerNotifierAdmin = {

    validate : function(){
        try{
            var token = document.forms["yammerNotifierAdminForm"]["token"].value;

            var errors = [];

            if (!token) {
                errors.push("Api token is required.");
            }

            $('yammerNotificationErrors').innerHTML = '';


            if(errors.length > 0) {
                var errorList = jQuery('<ul></ul>');

                jQuery.each(errors, function(index, error) {
                   var li = jQuery('<li/>')
                        .addClass('yammer-config-error')
                        .text(error)
                        .appendTo(errorList);
                });
                errorList.appendTo($('yammerNotificationErrors'));
                return false;
            }
            else {
                return true;
            }
        }
        catch(err){
            $('yammerNotificationErrors').innerHTML = 'Oops! Something went wrong!';
            return false;
        }
    },

    sendTestNotification: function () {
        if (!YammerNotifierAdmin.validate()) {
            return false;
        }

        jQuery.ajax(
            {
                url: $("yammerNotifierAdminForm").action,
                data: {
                    test: 1,

                    token: $("token").value,
                    groupId: $("groupId").value

                },
                type: "GET"
            }).done(function () {
                alert("Notification sent\r\n\r\nNote: Any changes have not yet been saved.");
            }).fail(function () {
                alert("Failed to send notification!")
            });

        return false;
    },

    save: function () {
        if (!YammerNotifierAdmin.validate()) {
            return false;
        }
        $('saving').style.display = '';
        jQuery.ajax(
            {
                url: $("yammerNotifierAdminForm").action,
                data: {
                    edit: 1,
                    token: $("token").value,
                    groupId: $("groupId").value
                },
                type: "POST"
            }).done(function () {
                $('saving').style.display = 'hidden';
                BS.reload();
            }).fail(function () {
                alert("Failed to save configuration!")
            });

        return false;
    },

    validateGetToken: function () {
        try {
            var appKey = document.forms["yammerTokenForm"]["appKey"].value;
            var secretKey = document.forms["yammerTokenForm"]["secretKey"].value;
            var userName = document.forms["yammerTokenForm"]["userName"].value;
            var password = document.forms["yammerTokenForm"]["password"].value;

            var errors = [];

            if (!appKey) {
                errors.push("App Key is required.");
            }
            if (!secretKey) {
                errors.push("Secret Key is required.");
            }
            if (!userName) {
                errors.push("User Name is required.");
            }
            if (!password) {
                errors.push("Password is required.");
            }


            $('yammerTokenErrors').innerHTML = '';


            if (errors.length > 0) {
                var errorList = jQuery('<ul></ul>');

                jQuery.each(errors, function (index, error) {
                    var li = jQuery('<li/>')
                        .addClass('yammer-config-error')
                        .text(error)
                        .appendTo(errorList);
                });
                errorList.appendTo($('yammerTokenErrors'));
                return false;
            }
            else {
                return true;
            }
        }
        catch (err) {
            $('yammerTokenErrors').innerHTML = 'Oops! Something went wrong!';
            return false;
        }
    },

    getToken: function () {
        if (!YammerNotifierAdmin.validateGetToken()) {
            return false;
        }
        $('saving-dialog').style.display = '';
        jQuery.ajax(
            {
                url: $("yammerTokenForm").action,
                data: {
                    getToken: 1,
                    appKey: $("appKey").value,
                    secretKey: $("secretKey").value,
                    userName: $("userName").value,
                    password: $("password").value
                },
                type: "POST"
            }).done(function () {
                $('saving-dialog').style.display = 'hidden'
                BS.YammerNotificationConfigDialog.cancelDialog();
                BS.reload();
            }).fail(function () {
                alert("Failed to get token!")
            });

        return false;
    }


};