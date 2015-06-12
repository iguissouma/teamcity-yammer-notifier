package org.igu.yammer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.meterware.httpunit.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.igu.utils.Loggers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class YammerClient implements Closeable {

    /**
     * Restful Yammer URL for messages api.
	 */
	private static final String YAMMER_API_V1_MESSAGES = "https://www.yammer.com/api/v1/messages";
    private static final String YAMMER_API_V1_GROUPS = "https://www.yammer.com/api/v1/groups.json?mine=1";
    private static final String YAMMER_API_V1_CURRENT = "https://www.yammer.com/api/v1/users/current.json";
    private static final String OAUTH_GET_ACCESS_TOKEN_URL = "https://www.yammer.com/dialog/oauth?client_id=%s";
	/**
	 * Yammer URL for getting access token.
	 */
	private static final String OAUTH_ACCESS_TOKEN_URL = "https://www.yammer.com/oauth2/access_token.xml?client_id=%s&client_secret=%s&code=%s";

	private static final String MESSAGE_GROUP_ID_PARAM_NAME = "group_id";
	private static final String MESSAGE_CC_PARAM_NAME = "cc";
	private static final String MESSAGE_BODY_PARAM_NAME = "body";
    private static final String MESSAGE_TOPIC_PARAM_NAME = "topic";

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String MESSAGE_DIRECT_TO_ID_PARAM_NAME = "direct_to_id";
    private static final String YAMMER_API_V1_USER_BY_EMAIL = "https://www.yammer.com/api/v1/users/by_email.json?email=";


    private final String accessAuthToken;
    private final CloseableHttpClient httpclient;

    public YammerClient(String token) {
        httpclient = HttpClientBuilder.create().useSystemProperties().build();
        this.accessAuthToken = token;
    }


    public String getAccessAuthToken() {
        return accessAuthToken;
    }

    /**
     *
     * @param applicationKey The key of the application registered with Yammer. See http://www.yammer.com/client_applications/new
     * @param applicationSecret The secret of the application registered with Yammer. See http://www.yammer.com/client_applications/new
     * @throws java.io.IOException
     */
    public YammerClient(
            final String applicationKey,
            final String applicationSecret, final String username,
            final String password) throws IOException {
        Loggers.SERVER.info("Yammer Client Instance " + applicationKey + " >>" + "applicationSecret" + " >>" + username + " >>" + "password");
        httpclient = HttpClientBuilder.create().useSystemProperties().build();
        this.accessAuthToken = getAccessTokenParameters(applicationKey, username, password, applicationSecret);
    }

	public void sendMessage(final String group, final String message, final String... topics) throws IOException {

        HttpPost httpPost = new HttpPost(YAMMER_API_V1_MESSAGES);
        httpPost.addHeader("Authorization", "Bearer " + accessAuthToken);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(MESSAGE_BODY_PARAM_NAME, message));

        if (group != null && !group.equals("") && !group.equals("0")) {
            nvps.add(new BasicNameValuePair(MESSAGE_GROUP_ID_PARAM_NAME, group));
        }

        for(int i = 0; i < topics.length; ++i) {
            nvps.add(new BasicNameValuePair(MESSAGE_TOPIC_PARAM_NAME + (i+1), topics[i]));
        }

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, UTF8));
        HttpResponse response = httpclient.execute(httpPost);
        if(201 != response.getStatusLine().getStatusCode()) {
            throw new ClientProtocolException("failed to post message to yammer: " + response);
        }
        Loggers.SERVER.info(this.getClass().getSimpleName() + " :: YammerNotification delivered : "
                + " -> group "  + group
                + " -> message"+ message);
    }

    @Override
    public void close() throws IOException {
        httpclient.close();
    }

	private String getAccessTokenParameters(
            final String applicationKey,
            final String username,
            final String password,
            final String applicationSecret) throws IOException {

        String accessToken = getAccesToken(applicationKey, username, password);
        HttpGet httpGet = new HttpGet(String.format(OAUTH_ACCESS_TOKEN_URL, applicationKey, applicationSecret, accessToken));
        HttpResponse response = httpclient.execute(httpGet);
        if(200 == response.getStatusLine().getStatusCode()) {
            try {
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(response.getEntity().getContent());
                Element accessTokenElement = (Element) doc.getDocumentElement().getElementsByTagName("access-token").item(0);
                return accessTokenElement.getElementsByTagName("token").item(0).getTextContent();
            } catch (ParserConfigurationException ex) {
                throw new IOException("failed to parse xml response", ex);
            } catch (SAXException ex) {
                throw new IOException("failed to parse xml response", ex);
            }
        } else {
            throw new IOException("failed request: " + response);
        }
	}

    public UserInfo getUserByEmail(String user) throws IOException {
        UserInfo userInfo = null;
        HttpGet httpGet = new HttpGet(YAMMER_API_V1_USER_BY_EMAIL + user);
        httpGet.addHeader("Authorization", "Bearer " + accessAuthToken);
        HttpResponse response = httpclient.execute(httpGet);
        if (200 == response.getStatusLine().getStatusCode()) {
            final String s = EntityUtils.toString(response.getEntity());
            Loggers.SERVER.info("EntityUtils.toString(USER_BY_EMAIL)" + s);
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .setDateFormat("yyyy/MM/dd HH:mm:ss ZZZZ")
                    .create();
            @SuppressWarnings("serial")
            Type collectionType = new TypeToken<List<UserInfo>>() {
            }.getType();
            List<UserInfo> users = gson.fromJson(s, collectionType);
            if (users.size() > 0) {
                userInfo = users.get(0);
            }
        } else {
            throw new IOException("failed request: " + response);
        }
        return userInfo;
    }

    public List<Group> getGroups() throws IOException {
        List<Group> groups = new ArrayList<Group>();
        HttpGet httpGet = new HttpGet(YAMMER_API_V1_GROUPS);
        httpGet.addHeader("Authorization", "Bearer " + accessAuthToken);
        HttpResponse response = httpclient.execute(httpGet);
        if (200 == response.getStatusLine().getStatusCode()) {
            final String s = EntityUtils.toString(response.getEntity());
            Loggers.SERVER.info("EntityUtils.toString(GROUPS)" + s);
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .setDateFormat("yyyy/MM/dd HH:mm:ss ZZZZ")
                    .create();
            @SuppressWarnings("serial")
            Type collectionType = new TypeToken<List<Group>>() {
            }.getType();
            groups = gson.fromJson(s, collectionType);
        } else {
            throw new IOException("failed request: " + response);
        }
        return groups;
    }

    private String getAccesToken(
            final String applicationKey,
            final String username,
            final String password) throws IOException {

        try {
            HttpUnitOptions.setScriptingEnabled(false);
            WebConversation wc = new WebConversation();
            wc.set_connectTimeout(-1);
            WebResponse resp = wc.getResponse(String.format(OAUTH_GET_ACCESS_TOKEN_URL, applicationKey));
            WebForm form = findLoginForm(resp.getForms());
            form.setParameter("login", username);
            form.setParameter("password", password);
            resp = form.submit();
            if (!resp.getURL().toString().contains("code=")) {
                final WebForm webForm = resp.getForms()[0];
                if (webForm != null) {
                    final SubmitButton allow = webForm.getSubmitButton("allow");
                    if (allow != null) {
                        resp = webForm.submit(allow);
                    } else {
                        throw new IOException("No allow button found on " + OAUTH_GET_ACCESS_TOKEN_URL);
                    }
                } else {
                    throw new IOException("No allow form found on " + OAUTH_GET_ACCESS_TOKEN_URL);
                }
            }
            return resp.getURL().toString().split("code=")[1];
        } catch (SAXException ex) {
            throw new IOException(ex);
        }
    }

    private WebForm findLoginForm(WebForm[] forms) throws IOException {
        for(WebForm form : forms) {
            if("login-form".equalsIgnoreCase(form.getID())) {
                return form;
            }
        }
        throw new IOException("No login form found on " + OAUTH_GET_ACCESS_TOKEN_URL);
    }

    public void sendPrivateMessage(final String user, final String message, final String... topics) throws IOException {

        HttpPost httpPost = new HttpPost(YAMMER_API_V1_MESSAGES);
        httpPost.addHeader("Authorization", "Bearer " + accessAuthToken);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(MESSAGE_BODY_PARAM_NAME, message));

        if (user != null && !user.equals("")) {
            nvps.add(new BasicNameValuePair(MESSAGE_DIRECT_TO_ID_PARAM_NAME, "[[user:" + user + "]]"));
        }

        for(int i = 0; i < topics.length; ++i) {
            nvps.add(new BasicNameValuePair(MESSAGE_TOPIC_PARAM_NAME + (i+1), topics[i]));
        }

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, UTF8));
        HttpResponse response = httpclient.execute(httpPost);
        if(201 != response.getStatusLine().getStatusCode()) {
            throw new ClientProtocolException("failed to post a private message to yammer user: " + user);
        }
        Loggers.SERVER.info(this.getClass().getSimpleName() + " :: YammerNotification delivered : "
                + " -> user "  + user
                + " -> message"+ message);
    }
}