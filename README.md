# teamcity-yammer
A configurable TeamCity plugin that enables notifications to be sent to [Yammer](https://www.yammer.com) group and users.
Because it is a [TeamCity Custom Notifier](http://confluence.jetbrains.com/display/TCD9/Custom+Notifier) plugin, it extends the existing user interface and allows for easy configuration directly within your TeamCity server. Once installed, you can configure the plugin for multiple TeamCity projects and multiple build conditions (i.e. Build failures, successes, hangs, etc.)

This is an example of a successful build notification sent to a group:

![Successful Build Notification](/docs/yammer-group-post.png)

This is an example of a successful build notification sent as a private message:

![Successful Build Notification](/docs/yammer-message.png)

## Installation
Download the [plugin zip package](https://github.com/iguissouma/teamcity-yammer-notifier/raw/master/dist/v1.0.0/teamcity-yammer-integration.zip).

Follow the TeamCity [plugin installation directions](http://confluence.jetbrains.com/display/TCD9/Installing+Additional+Plugins).

You will need to restart the TeamCity service before you can configure the plugin.

## Configuration

Once you have installed the plugin and restarted head on over to the Admin page and configure your Yammer settings.

![Admin Page Configuration](/docs/yammer-config-example.png)

- The user token can be obtained via the button Generate a yammer access token and fill the form with Application Key(Client ID), Application Secret Key(Client Secret), Yammer username, Yammer password.

![Generate yammer access token](/docs/generate-yammer-access-token.png)

- *Yammer Group* is the group where notifications will be sent.

### Create yammer application
Sign up for Yammer [https://www.yammer.com](https://www.yammer.com)

Go to [https://www.yammer.com/client_applications](https://www.yammer.com/client_applications) and register a new app.

![Configuration Settings](/docs/yammer-create-app.png)

Fill out the form with your data
“Redirect URI” with: [https://www.yammer.com](https://www.yammer.com)

Navigate to the the Application Information page, note your Client ID and Client Secret.

![Configuration Settings](/docs/yammer-info-app.png)

### Choose Yammer Group
Once the token is successfully saved you can choose from the group where you want to post teamcity notifications

### Yammer UserName and notification rules
In order to receive direct messages from the notifier you must go to your profile page in TeamCity and tell it your yammer username.

Enter the Yammer username in the Notification settings as seen below.

Add notification rules as appropriate.

![Configuration Settings](/docs/user-config-example.png)

Once you have done this you can receive private messages on failed/successful builds.

## Warning

Tested exclusively with TeamCity version 9.0.3.

## Developers

[Issam GUISSOUMA](https://twitter.com/iguissouma)


