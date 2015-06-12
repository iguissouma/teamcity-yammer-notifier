package org.igu.ui;


import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.serverSide.crypt.RSACipher;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.igu.config.YammerNotificationMainConfig;
import org.igu.teamcity.YammerMessage;
import org.igu.utils.Loggers;
import org.igu.yammer.YammerClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;


public class YammerNotifierSettingsController extends BaseController {

    private static final String CONTROLLER_PATH = "/yammerNotifier/adminSettings.html";
    public static final String EDIT_PARAMETER = "edit";
    public static final String TEST_PARAMETER = "test";
    private static final Object ACTION_ENABLE = "enable";
    private static final String ACTION_PARAMETER = "action";
    private static final String GET_TOKEN_PARAMETER = "getToken";


    private SBuildServer server;
    private ServerPaths serverPaths;
    private WebControllerManager manager;
    private YammerNotificationMainConfig config;
    private PluginDescriptor descriptor;

    public YammerNotifierSettingsController(@NotNull SBuildServer server,
                                            @NotNull ServerPaths serverPaths,
                                            @NotNull WebControllerManager manager,
                                            @NotNull YammerNotificationMainConfig config,
                                            PluginDescriptor descriptor){

        this.server = server;
        this.serverPaths = serverPaths;
        this.manager = manager;
        this.config = config;
        this.descriptor = descriptor;

        manager.registerController(CONTROLLER_PATH, this);
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();

        if(request.getParameter(EDIT_PARAMETER) != null){
            Loggers.SERVER.debug("Updating configuration");
            params = this.handleConfigurationChange(request);
        } else if(request.getParameter(TEST_PARAMETER) != null){
            Loggers.SERVER.debug("Sending test notification");
            params = this.handleTestNotification(request);
        } else if (request.getParameter(ACTION_PARAMETER) != null) {
            Loggers.SERVER.debug("Changing plugin status");
            this.handlePluginStatusChange(request);
        }else if(request.getParameter(GET_TOKEN_PARAMETER) != null){
            Loggers.SERVER.debug("Sending token");
            params = this.handleGetToken(request);
        }
        return new ModelAndView(descriptor.getPluginResourcesPath() + "admin/ajaxEdit.jsp", params);
    }

    private void handlePluginStatusChange(HttpServletRequest request) {
        Loggers.SERVER.debug("Changing status");
        Boolean disabled = !request.getParameter(ACTION_PARAMETER).equals(ACTION_ENABLE);
        Loggers.SERVER.debug(String.format("Disabled status: %s", disabled));
        this.config.setEnabled(!disabled);
        this.config.save();
    }

    private HashMap<String, Object> handleGetToken(HttpServletRequest request) {
        Loggers.SERVER.debug("Sending token");
        HashMap<String, Object> params = new HashMap<String, Object>();
        String appKey = request.getParameter("appKey");
        String secretKey = request.getParameter("secretKey");
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");

        final YammerClient yammerClient;
        try {
            yammerClient = new YammerClient(appKey, secretKey, userName, password);
            String token = yammerClient.getAccessAuthToken();
            params.put("token", token);
            this.config.setAppKey(appKey);
            this.config.setSecretKey(secretKey);
            this.config.setUserName(userName);
            this.config.setPassword(password);
            this.config.setToken(token);
            this.config.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return params;
    }

    private HashMap<String, Object> handleTestNotification(HttpServletRequest request) throws IOException, YammerConfigValidationException {
        String token = request.getParameter("token");
        String groupId = request.getParameter("groupId");
        HashMap<String, Object> params = new HashMap<String, Object>();

        final YammerClient yammerClient = new YammerClient(token);
        yammerClient.sendMessage(groupId, YammerMessage.doFormatMessage("Project-Test-Notification :: Build","1","finished"));

        this.getOrCreateMessages(request).addMessage("notificationSent", "The notification has been sent");

        params.put("messages", "Sent");


        return params;
    }

    private void Validate(String token) throws YammerConfigValidationException {
        Loggers.ACTIVITIES.debug("Token" + token);
        if (token == null) {
            throw new YammerConfigValidationException("Could not validate parameters. Please recheck the request.");
        }
    }

    private boolean isNullOrEmpty(String str){
        return str == null || StringUtil.isEmpty(str);
    }

    public Integer tryParseInt(String str) {
        Integer retVal;
        try {
            retVal = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            retVal = null; // or null if that is your preference
        }
        return retVal;
    }

    private HashMap<String, Object> handleConfigurationChange(HttpServletRequest request) throws IOException, YammerConfigValidationException {
        String token = request.getParameter("token");
        String groupId = request.getParameter("groupId");

        this.config.setToken(token);
        this.config.setGroupId(groupId);
        this.config.save();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("message", "Saved");
        return params;
    }

    public class YammerConfigValidationException extends Exception {
        public YammerConfigValidationException(String message) {
            super(message);
        }
    }
}
