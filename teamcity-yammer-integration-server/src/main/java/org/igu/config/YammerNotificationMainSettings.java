package org.igu.config;

import jetbrains.buildServer.serverSide.MainConfigProcessor;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import org.igu.utils.Loggers;
import org.jdom.Element;


import java.io.IOException;
import java.util.Properties;

public class YammerNotificationMainSettings implements MainConfigProcessor {
	private static final String NAME = YammerNotificationMainSettings.class.getName();
	private YammerNotificationMainConfig yammerNotificationMainConfig;
	private SBuildServer server;
    private ServerPaths serverPaths;
    private String version;

    public YammerNotificationMainSettings(SBuildServer server, ServerPaths serverPaths){
        this.serverPaths = serverPaths;
        Loggers.SERVER.debug(NAME + " :: Constructor called");
		this.server = server;
		yammerNotificationMainConfig = new YammerNotificationMainConfig(serverPaths);

	}

    public void register(){
        Loggers.SERVER.debug(NAME + ":: Registering");
        server.registerExtension(MainConfigProcessor.class, "yammernotifications", this);
    }


    @SuppressWarnings("unchecked")
    public void readFrom(Element rootElement)
    /* Is passed an Element by TC, and is expected to persist it to the settings object.
     * Old settings should be overwritten.
     */
    {
        if(yammerNotificationMainConfig.getConfigFileExists()){
            // The MainConfigProcessor approach has been deprecated.
            // Instead we will use our own config file so we have better control over when it is persisted
            return;
        }
    	Loggers.SERVER.info("YammerNotificationMainSettings: re-reading main settings using old-style MainConfigProcessor. From now on we will use the yammer/yammer-config.xml file instead of main-config.xml");
    	Loggers.SERVER.debug(NAME + ":readFrom :: " + rootElement.toString());
    	YammerNotificationMainConfig tempConfig = new YammerNotificationMainConfig(serverPaths);
    	Element yammerNotificationsElement = rootElement.getChild("yammernotifications");
        tempConfig.readConfigurationFromXmlElement(yammerNotificationsElement);
        this.yammerNotificationMainConfig = tempConfig;
        tempConfig.save();
    }

    public void writeTo(Element parentElement)
    /* Is passed an (probably empty) Element by TC, which is expected to be populated from the settings
     * in memory. 
     */
    {

    }


    public String getToken() {
        return this.yammerNotificationMainConfig.getToken();
    }

    public String getAppKey() {
        return this.yammerNotificationMainConfig.getAppKey();
    }
    public String getSecretKey() {
        return this.yammerNotificationMainConfig.getSecretKey();
    }

    public String getUserName() {
        return this.yammerNotificationMainConfig.getUserName();
    }
    public String getPassword() {
        return this.yammerNotificationMainConfig.getPassword();
    }
    public String getGroupId() {
        return this.yammerNotificationMainConfig.getGroupId();
    }

    public boolean getEnabled(){
        return this.yammerNotificationMainConfig.getEnabled();
    }


    
	public void dispose() {
		Loggers.SERVER.debug(NAME + ":dispose() called");
	}


    public void refresh() {
        this.yammerNotificationMainConfig.refresh();
    }

    public String getPluginVersion() throws IOException {
        if(version != null){
            return version;
        }
        Properties props = new Properties();
        props.load(YammerNotificationMainSettings.class.getResourceAsStream("/version.txt"));
        version = props.getProperty("version");
        return version;
    }
}
