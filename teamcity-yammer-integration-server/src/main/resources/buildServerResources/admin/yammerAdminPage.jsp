<%@ include file="/include.jsp" %>
<c:url value="/yammerNotifier/adminSettings.html" var="actionUrl"/>
<bs:linkCSS dynamic="${true}">
    ${jspHome}css/admin-styles.css
</bs:linkCSS>

<div id="settingsContainer">
    <form action="${actionUrl}" id="yammerNotifierAdminForm" method="post" onsubmit="return YammerNotifierAdmin.save()">
        <div class="editNotificatorSettingsPage">
            <div>
                <span class="yammerNotifierVersionInfo">Version: <c:out value='${pluginVersion}'/>&nbsp;<a
                        href="https://github.com/iguisssouma/teamcity-yammer-notifier" class="helpIcon"
                        style="vertical-align: middle;" target="_blank"><bs:helpIcon/></a></span>
            </div>
            <c:choose>
                <c:when test="${disabled}">
                    <div class="pauseNote" style="margin-bottom: 1em;">
                        The notifier is <strong>disabled</strong>. All yammer notifications are suspended&nbsp;&nbsp;<a
                            class="btn btn_mini" href="#" id="enable-btn">Enable</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div style="margin-left: 0.6em;">
                        The notifier is <strong>enabled</strong>&nbsp;&nbsp;<a class="btn btn_mini" href="#"
                                                                               id="disable-btn">Disable</a>
                    </div>
                </c:otherwise>
            </c:choose>

            <bs:messages key="message"/>
            <br/>

            <div class="yammer-config-errors" id="yammerNotificationErrors"></div>

            <table class="runnerFormTable">
                <tr class="groupingTitle">
                    <td colspan="2">General Configuration&nbsp;<a
                            href="https://github.com/iguissouma/teamcity-yammer-notifier"
                            class="helpIcon" style="vertical-align: middle;"
                            target="_blank"><bs:helpIcon/></a></td>
                </tr>
                <tr>
                    <th>
                        <label for="token">API token: <l:star/></label>
                    </th>
                    <td>
                        <forms:textField name="token" value="${token}" style="width: 300px;"/>
                        <span class="smallNote">A user OAuth token for your team. You can get this from the <a
                                href="https://developer.yammer.com/v1.0/docs/test-token" target="_blank">api page</a> when you are signed in to your team. You can use the button below, fill the form and get a new token</span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="token">Yammer Group: <l:star/></label>
                    </th>
                    <td>
                        <select id="groupId" name="groupId" style="width: 300px;">
                            <c:forEach var="group" items="${groups}">
                                <option value="${group.id}" ${groupId == group.id ? 'selected="selected"' : ''}>${group.fullName}</option>
                            </c:forEach>
                        </select>
                        <span class="smallNote">A yammer Group where notifications submited</span>
                    </td>
                </tr>

            </table>
            <div class="saveButtonsBlock">
                <forms:submit label="Save"/>
                <forms:submit id="testConnection" type="button" label="Send test notification"
                              onclick="return YammerNotifierAdmin.sendTestNotification()"/>

                <forms:submit id="genYamToken" type="button" label="Generate a yammer access token"
                              onclick="return BS.YammerNotificationConfigDialog.showDialog();"/>
                <forms:saving/>
            </div>
        </div>
    </form>

    <bs:linkScript>
        ${jspHome}js/yammerNotifierAdmin.js
    </bs:linkScript>
</div>
<div id="yammerConfigDialog" class="yammerConfigDialog modalDialog" style="width:500px;">
    <div class="dialogHeader">
        <div class="closeWindow">
            <a title="Close dialog window" href="javascript://" showdiscardchangesmessage="false"
               onclick="BS.YammerNotificationConfigDialog.cancelDialog()" title="Close dialog window" class="closeWindowLink">x</a>
        </div>
        <div class="dialogHandle">
            <h3 class="dialogTitle">Yammer Access Configuration </h3>
        </div>
    </div>
    <div class="modalDialogBody">
        <form  name='yammerTokenForm' id='yammerTokenForm' action="${actionUrl}"
              method="post" onsubmit="return YammerNotifierAdmin.getToken()">
            <div>
                <div class="yammer-config-error" id="yammerTokenErrors"></div>
                <table>
                    <tbody>
                    <tr>
                        <th><label for="appKey">App Key: <span title="Mandatory field" class="mandatoryAsterix">*</span></label></th>
                        <td><input type="text" default="" value="${appKey}" name="appKey" id="appKey"></td>
                    </tr>
                    <tr>
                        <th><label for="secretKey">App Secret Key: <span title="Mandatory field" class="mandatoryAsterix">*</span></label></th>
                        <td><input type="text" default="" value="${secretKey}" name="secretKey" id="secretKey"></td>
                    </tr>
                    <tr>
                        <th><label for="userName">UserName: <span title="Mandatory field" class="mandatoryAsterix">*</span></label></th>
                        <td><input type="text" default="" value="${userName}" name="userName" id="userName"></td>
                    </tr>
                    <tr>
                        <th><label for="password">Password: <span title="Mandatory field" class="mandatoryAsterix">*</span></label></th>
                        <td><input type="password" default="" value="${password}" name="password" id="password"></td>
                    </tr>
                    </tbody>
                </table>
                <div style="" id="yammerConfigError"></div>
            </div>

            <div class="popupSaveButtonsBlock">
                <input type="hidden" id="publicKey" name="publicKey" value="<c:out value='${hexEncodedPublicKey}'/>"/>
                <a href="javascript://" showdiscardchangesmessage="false"
                   onclick="BS.YammerNotificationConfigDialog.close()"
                   class="btn cancel">Cancel</a>
                <input type="submit" class="btn btn_primary submitButton " value="Save">
                <i title="Please wait..." class="icon-refresh icon-spin progressRing progressRingDefault"
                   style="display: none; " id="saving-dialog"></i>
            </div>

        </form>
    </div>
</div>

<script type="text/javascript">
    BS.YammerNotificationConfigDialog = OO.extend(BS.AbstractModalDialog, {
        getContainer: function () {
            return $('yammerConfigDialog');
        },

        showDialog: function () {
            this.showCentered();
        },

        cancelDialog: function () {
            this.close();
        }
    });

    (function ($) {
        var sendAction = function (enable) {
            $.post("${actionUrl}?action=" + (enable ? 'enable' : 'disable'),
                    function () {
                        BS.reload(true);
                    });
            return false;
        };
        $("#enable-btn").click(function () {
            return sendAction(true);
        });
        $("#disable-btn")
                .click(
                function () {
                    if (!confirm("Yammer notifications will not be sent until enabled. Disable the notifier?"))
                        return false;
                    return sendAction(false);
                });
    })(jQuery);
</script>