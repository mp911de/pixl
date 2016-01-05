package pixl.plugins.googlemail;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Component;
import pixl.api.application.Application;
import pixl.api.application.HasApplications;
import pixl.api.application.data.Value;
import pixl.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
@Component
public class GoogleMailClient implements Application, HasApplications, Plugin {

    private static NetHttpTransport HTTP_TRANSPORT;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    private static FileDataStoreFactory DATA_STORE_FACTORY;

    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/pixl");

    private Cache<String, ListMessagesResponse> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public Collection<Application> getApplications() {
        return ImmutableList.of((Application) this);
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize(URL secretJson) throws IOException {
        // Load client secrets.
        try (InputStream in = secretJson.openStream()) {

            GoogleClientSecrets clientSecrets =
                    GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow =
                    new GoogleAuthorizationCodeFlow.Builder(
                            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, GmailScopes.all())
                            .setDataStoreFactory(DATA_STORE_FACTORY)
                            .setAccessType("offline")
                            .build();
            Credential credential = new AuthorizationCodeInstalledApp(
                    flow, new LocalServerReceiver()).authorize("user");

            return credential;
        }
    }

    @Override
    public String getId() {
        return "google-mail";
    }

    @Override
    public List<Value<?>> getValues(Map<String, Object> configuration) {

        if (configuration.containsKey("secret.json")) {
            String secretJson = (String) configuration.get("secret.json");
            File file = new File(secretJson);

            ListMessagesResponse listMessagesResponse = cache.getIfPresent(secretJson);
            if (listMessagesResponse == null) {
                try {
                    Credential credential = authorize(file.toURI().toURL());
                    Gmail gmail = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                            .setApplicationName("pixl")
                            .build();

                    listMessagesResponse = gmail.users().messages().list("me")
                            .setLabelIds(Arrays.asList("UNREAD", "INBOX"))
                            .execute();

                    cache.put(secretJson, listMessagesResponse);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }

            if (listMessagesResponse.getMessages() != null) {
                return ImmutableList.of(new Value<>(listMessagesResponse.getMessages().size()));

            }
        }

        return ImmutableList.of(new Value<>(0));
    }
}
