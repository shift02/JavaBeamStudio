package net.orekyuu.javatter.core.controller;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import net.orekyuu.javatter.api.controller.OwnerStage;
import net.orekyuu.javatter.api.service.AccountStorageService;
import net.orekyuu.javatter.core.account.TwitterAccountImpl;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignupController implements Initializable {
    public Pane veil;
    public ProgressIndicator indicator;
    public TextField pinField;
    public WebView webView;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Twitter twitter;
    private RequestToken token;

    @Inject
    private AccountStorageService accountStorageService;
    @OwnerStage
    private Stage owner;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executorService.submit(() -> {
            twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer("rMvLmU5qMgbZwg92Is5g", "RD28Uuu44KeMOs90UuqXAAoVTWXRTmD4H8xYKZSgBk");
            try {
                token = twitter.getOAuthRequestToken();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                webView.getEngine().load(token.getAuthorizationURL());
                veil.setVisible(false);
                indicator.setVisible(false);
            });
        });
    }

    public void signup() {
        try {
            AccessToken accessToken = twitter.getOAuthAccessToken(token, pinField.getText());
            TwitterAccountImpl account = new TwitterAccountImpl(accessToken);
            accountStorageService.save(account);
            owner.close();
            onClose();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    public void onClose() {
        executorService.shutdown();
    }
}
