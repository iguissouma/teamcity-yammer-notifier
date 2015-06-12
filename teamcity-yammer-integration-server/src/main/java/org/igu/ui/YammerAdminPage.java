package org.igu.ui;

import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.crypt.RSACipher;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import org.igu.config.YammerNotificationMainSettings;
import org.igu.utils.Loggers;
import org.igu.yammer.Group;
import org.igu.yammer.YammerClient;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jasonconnery on 02/03/2014.
 */
public class YammerAdminPage extends AdminPage {

    public static final String PLUGIN_NAME = "yammerNotifier";
    public static final String TAB_TITLE = "Yammer Notifier";
    public static final String ADMIN_PAGE = "/admin/yammerAdminPage.jsp";
    private static final String AFTER_PAGE_ID = "jabber";
    private static final String BEFORE_PAGE_ID = "clouds";
    private final String jspHome;

    private SBuildServer sBuildServer;
    private YammerNotificationMainSettings yammerMainSettings;

    public YammerAdminPage(@NotNull PagePlaces pagePlaces,
                           @NotNull PluginDescriptor descriptor,
                           @NotNull SBuildServer sBuildServer,
                           @NotNull YammerNotificationMainSettings yammerMainSettings
    ) {
        super(pagePlaces);
        this.sBuildServer = sBuildServer;
        this.yammerMainSettings = yammerMainSettings;
        setPluginName(PLUGIN_NAME);
        setIncludeUrl(descriptor.getPluginResourcesPath(ADMIN_PAGE));
        jspHome = descriptor.getPluginResourcesPath();
        setTabTitle(TAB_TITLE);
        ArrayList<String> after = new ArrayList<String>();
        after.add(AFTER_PAGE_ID);
        ArrayList<String> before = new ArrayList<String>();
        before.add(BEFORE_PAGE_ID);
        setPosition(PositionConstraint.between(after, before));
        register();

    }


    @Override
    public boolean isAvailable(HttpServletRequest request) {
        return super.isAvailable(request) && checkHasGlobalPermission(request, Permission.CHANGE_SERVER_SETTINGS);
    }

    public String getGroup() {
        return SERVER_RELATED_GROUP;
    }

    @Override
    public void fillModel(Map<String, Object> model, HttpServletRequest request) {
        super.fillModel(model, request);
        yammerMainSettings.refresh();
        model.put("token", this.yammerMainSettings.getToken());
        model.put("appKey", this.yammerMainSettings.getAppKey());
        model.put("secretKey", this.yammerMainSettings.getSecretKey());
        model.put("userName", this.yammerMainSettings.getUserName());
        model.put("password", this.yammerMainSettings.getPassword());
        model.put("groupId", this.yammerMainSettings.getGroupId());
        model.put("hexEncodedPublicKey", RSACipher.getHexEncodedPublicKey());
        try {
            model.put("pluginVersion", this.yammerMainSettings.getPluginVersion());
        } catch (IOException e) {
            Loggers.ACTIVITIES.error("Could not retrieve yammer plugin version", e);
        }

        final YammerClient yammerClient = new YammerClient(this.yammerMainSettings.getToken());
        try {
            final List<Group> groups = new ArrayList<Group>();
            final Group e = new Group(0,"All Company");
            groups.add(e);
            groups.addAll(yammerClient.getGroups());
            Loggers.ACTIVITIES.info("Groups loaded= " + groups.size());
            model.put("groups", groups);
        } catch (IOException e) {
            Loggers.ACTIVITIES.error("Could not retrieve yammer groups", e);
        }
        model.put("disabled", !this.yammerMainSettings.getEnabled());
        model.put("jspHome", this.jspHome);

    }
}