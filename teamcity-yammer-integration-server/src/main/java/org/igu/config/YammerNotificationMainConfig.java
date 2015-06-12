package org.igu.config;

import com.intellij.openapi.util.JDOMUtil;
import jetbrains.buildServer.configuration.ChangeListener;
import jetbrains.buildServer.configuration.FileWatcher;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.util.FileUtil;
import org.igu.utils.Loggers;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import java.io.File;
import java.io.IOException;

public class YammerNotificationMainConfig implements ChangeListener {

    private final FileWatcher myChangeObserver;
	private final File myConfigDir;
	private final File myConfigFile;
    private boolean enabled = true;

    private YammerNotificationContentConfig content;
    private boolean configFileExists;


	private String token;
	private String appKey;
	private String secretKey;
	private String userName;
	private String password;
	private String groupId;


	public YammerNotificationMainConfig(ServerPaths serverPaths) {
        this.content = new YammerNotificationContentConfig();
		this.myConfigDir = new File(serverPaths.getConfigDir(), "yammer");
		this.myConfigFile = new File(this.myConfigDir, "yammer-config.xml");
        configFileExists = this.myConfigFile.exists();
		reloadConfiguration();

		this.myChangeObserver = new FileWatcher(this.myConfigFile);
		this.myChangeObserver.setSleepingPeriod(10000L);
		this.myChangeObserver.registerListener(this);
		this.myChangeObserver.start();
	}

    public void refresh(){
        reloadConfiguration();
    }

	private void reloadConfiguration() {
		Loggers.ACTIVITIES.info("Loading configuration file: " + this.myConfigFile.getAbsolutePath());

		myConfigDir.mkdirs();
		FileUtil.copyResourceIfNotExists(getClass(), "/config_templates/yammer-config.xml", new File(this.myConfigDir, "yammer-config.xml"));

		Document document = parseFile(this.myConfigFile);
		if (document != null)
		{
			Element rootElement = document.getRootElement();
			readConfigurationFromXmlElement(rootElement);
		}
	}

	private Document parseFile(File configFile)
	{
		try
		{
			if (configFile.isFile()) {
				return JDOMUtil.loadDocument(configFile);
			}
		}
		catch (JDOMException e)
		{
			Loggers.ACTIVITIES.error("Failed to parse xml configuration file: " + configFile.getAbsolutePath(), e);
		}
		catch (IOException e)
		{
			Loggers.ACTIVITIES.error("I/O error occurred on attempt to parse xml configuration file: " + configFile.getAbsolutePath(), e);
		}
		return null;
	}



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}


    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


	public synchronized void save()
	{
		this.myChangeObserver.runActionWithDisabledObserver(new Runnable()
		{
			public void run()
			{
				FileUtil.processXmlFile(YammerNotificationMainConfig.this.myConfigFile, new FileUtil.Processor() {
					public void process(Element rootElement) {
						rootElement.setAttribute("enabled", Boolean.toString(YammerNotificationMainConfig.this.enabled));
						rootElement.setAttribute("token", emptyIfNull(YammerNotificationMainConfig.this.token));
						rootElement.setAttribute("appKey", emptyIfNull(YammerNotificationMainConfig.this.appKey));
						rootElement.setAttribute("secretKey", emptyIfNull(YammerNotificationMainConfig.this.secretKey));
						rootElement.setAttribute("userName", emptyIfNull(YammerNotificationMainConfig.this.userName));
						rootElement.setAttribute("password", emptyIfNull(YammerNotificationMainConfig.this.password));
						rootElement.setAttribute("groupId", emptyIfNull(YammerNotificationMainConfig.this.groupId));
					}
				});
			}
		});
	}

    private String emptyIfNull(String str){
        return str == null ? "" : str;
    }

	@Override
	public void changeOccured(String s) {
		reloadConfiguration();
	}

	public boolean getConfigFileExists() {
		return configFileExists;
	}

	void readConfigurationFromXmlElement(Element yammerNotificationsElement) {
        if(yammerNotificationsElement != null){
            content.setEnabled(true);
            if(yammerNotificationsElement.getAttribute("enabled") != null)
            {
                setEnabled(Boolean.parseBoolean(yammerNotificationsElement.getAttributeValue("enabled")));
            }
            if(yammerNotificationsElement.getAttribute("token") != null)
            {
                setToken(yammerNotificationsElement.getAttributeValue("token"));
            }
			if(yammerNotificationsElement.getAttribute("appKey") != null)
			{
				setAppKey(yammerNotificationsElement.getAttributeValue("appKey"));
			}
			if(yammerNotificationsElement.getAttribute("secretKey") != null)
			{
				setSecretKey(yammerNotificationsElement.getAttributeValue("secretKey"));
			}
			if(yammerNotificationsElement.getAttribute("userName") != null)
			{
				setUserName(yammerNotificationsElement.getAttributeValue("userName"));
			}
			if(yammerNotificationsElement.getAttribute("password") != null)
			{
				setPassword(yammerNotificationsElement.getAttributeValue("password"));
			}
			if(yammerNotificationsElement.getAttribute("groupId") != null)
			{
				setGroupId(yammerNotificationsElement.getAttributeValue("groupId"));
			}
        }
    }


    public YammerNotificationContentConfig getContent() {
        if(content == null){
            this.content = new YammerNotificationContentConfig();
        }
        return content;
    }



}