package org.igu.teamcity;

import jetbrains.buildServer.responsibility.ResponsibilityEntry;
import jetbrains.buildServer.responsibility.TestNameResponsibilityEntry;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import jetbrains.buildServer.tests.TestName;
import org.igu.config.YammerNotificationMainSettings;
import org.igu.utils.Loggers;
import org.igu.yammer.YammerClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.io.IOException;
import java.util.Collection;
import java.util.List;


/**
 * SlackNotificationListner
 * Listens for Server events and then triggers the execution of slacknotifications if configured.
 */
public class YammerNotificationListener extends BuildServerAdapter {

    private static final String YAMMERNOTIFICATIONS_SETTINGS_ATTRIBUTE_NAME = "yammerNotifications";
    private final SBuildServer myBuildServer;
    private final ProjectSettingsManager mySettings;
    private final YammerNotificationMainSettings myMainSettings;


    public YammerNotificationListener(SBuildServer sBuildServer, ProjectSettingsManager settings
            , YammerNotificationMainSettings configSettings
    ) {

        myBuildServer = sBuildServer;
        mySettings = settings;
        myMainSettings = configSettings;
        Loggers.SERVER.info("YammerNotificationListener :: Starting");
    }

    public void register() {
        myBuildServer.addListener(this);
        Loggers.SERVER.info("YammerNotificationListener :: Registering");
    }

    private void processBuildEvent(SRunningBuild sRunningBuild, BuildStateEnum state) {

        Loggers.SERVER.debug("About to process Yammer notifications for " + sRunningBuild.getProjectId() + " at buildState " + state.getShortName());
        final String token = myMainSettings.getToken();
        if (token != null) {
            final YammerClient yammerClient = new YammerClient(token);
            final String message = YammerMessage.doFormatMessage(sRunningBuild.getFullName(), sRunningBuild.getBuildNumber(), state.getDescriptionSuffix());
            final String groupId = myMainSettings.getGroupId();
            try {
                yammerClient.sendMessage(groupId, message);
            } catch (IOException e) {
                Loggers.ACTIVITIES.error("Could not send Notification to Yammer Group id=" + groupId);
            }
        }

    }



    @Override
    public void buildStarted(SRunningBuild sRunningBuild) {
        processBuildEvent(sRunningBuild, BuildStateEnum.BUILD_STARTED);
    }

    @Override
    public void buildFinished(SRunningBuild sRunningBuild) {
        processBuildEvent(sRunningBuild, BuildStateEnum.BUILD_FINISHED);
    }

    @Override
    public void buildInterrupted(SRunningBuild sRunningBuild) {
        processBuildEvent(sRunningBuild, BuildStateEnum.BUILD_INTERRUPTED);
    }

    @Override
    public void beforeBuildFinish(SRunningBuild sRunningBuild) {
        processBuildEvent(sRunningBuild, BuildStateEnum.BEFORE_BUILD_FINISHED);
    }


    @Override
    public void responsibleChanged(SProject project,
                                   Collection<TestName> testNames, ResponsibilityEntry entry,
                                   boolean isUserAction) {
        Loggers.SERVER.debug("About to process YammerNotifications for " + project.getProjectId() + " at buildState responsibilityChanged");

    }

    @Override
    public void responsibleChanged(SProject project, TestNameResponsibilityEntry oldTestNameResponsibilityEntry, TestNameResponsibilityEntry newTestNameResponsibilityEntry, boolean isUserAction) {
        Loggers.SERVER.debug("About to process YammerNotifications for " + project.getProjectId() + " at buildState responsibilityChanged");

    }

    /**
     * New version of responsibleChanged, which has some bugfixes, but
     * is only available in versions 7.0 and above.
     *
     * @param sBuildType
     * @param responsibilityEntryOld
     * @param responsibilityEntryNew
     * @since 7.0
     */
    @Override
    public void responsibleChanged(@NotNull SBuildType sBuildType,
                                   @NotNull ResponsibilityEntry responsibilityEntryOld,
                                   @NotNull ResponsibilityEntry responsibilityEntryNew) {

        Loggers.SERVER.debug("About to process YammerNotifications for " + sBuildType.getProjectId() + " at buildState responsibilityChanged");

    }

    public void responsibleRemoved(SProject project, TestNameResponsibilityEntry entry) {

    }

    @Nullable
    private SFinishedBuild getPreviousNonPersonalBuild(SRunningBuild paramSRunningBuild) {
        List<SFinishedBuild> localList = this.myBuildServer.getHistory().getEntriesBefore(paramSRunningBuild, false);

        for (SFinishedBuild localSFinishedBuild : localList)
            if (!(localSFinishedBuild.isPersonal())) return localSFinishedBuild;
        return null;
    }

    private boolean hasBuildChangedHistoricalState(SRunningBuild sRunningBuild) {
        SFinishedBuild previous = getPreviousNonPersonalBuild(sRunningBuild);
        if (previous != null) {
            if (sRunningBuild.getBuildStatus().isSuccessful()) {
                return previous.getBuildStatus().isFailed();
            } else if (sRunningBuild.getBuildStatus().isFailed()) {
                return previous.getBuildStatus().isSuccessful();
            }
        }
        return true;
    }


}
