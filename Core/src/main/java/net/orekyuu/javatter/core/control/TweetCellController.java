package net.orekyuu.javatter.core.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import net.orekyuu.javatter.api.service.CurrentTweetAreaService;
import net.orekyuu.javatter.api.service.UserIconStorage;
import net.orekyuu.javatter.api.twitter.TwitterUser;
import net.orekyuu.javatter.api.twitter.model.Tweet;

import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class TweetCellController implements Initializable {

    public TweetText tweetContent;
    public ImageView currentIcon;
    public Hyperlink via;
    public Label userName;
    public Button reply;
    public Button reTweet;
    public Button favorite;
    public GridPane root;
    private ObjectProperty<Tweet> tweet = new SimpleObjectProperty<>();
    private ObjectProperty<TwitterUser> owner = new SimpleObjectProperty<>();
    @Inject
    private UserIconStorage iconStorage;
    @Inject
    private CurrentTweetAreaService tweetAreaService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tweet.addListener(this::onChange);
        tweetContent.setOnClickLink(url -> {
            try {
                Desktop.getDesktop().browse(new URL(url).toURI());
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    private void onChange(ObservableValue<? extends Tweet> observable, Tweet oldValue, Tweet newValue) {
        tweetContent.setTweet(newValue);
        currentIcon.setImage(iconStorage.find(newValue.getOwner()));
        via.setText(newValue.getViaName());
        userName.setText(String.format("@%s / %s", newValue.getOwner().getScreenName(), newValue.getOwner().getName()));
        TwitterUser twitterUser = owner.get();
        if (twitterUser != null) {
            //TODO ボタンの状態の更新
        }
    }


    public void setTweet(Tweet tweet) {
        this.tweet.set(tweet);
    }

    public Tweet getTweet() {
        return tweet.get();
    }

    public ObjectProperty<Tweet> tweetProperty() {
        return tweet;
    }

    public TwitterUser getOwner() {
        return owner.get();
    }

    public ObjectProperty<TwitterUser> ownerProperty() {
        return owner;
    }

    public void setOwner(TwitterUser owner) {
        this.owner.set(owner);
    }

    public void clickReply() {
        Tweet tweet = getTweet();
        if (tweet != null) {
            tweetAreaService.insertTop("@" + tweet.getOwner().getScreenName() + " ");
            tweetAreaService.setReply(tweet);
        }
    }

    public void clickRetweet() {
        TwitterUser twitterUser = owner.get();
        Tweet tweet = this.tweet.get();
        if (twitterUser != null && tweet != null) {
            twitterUser.reTweetAsync(tweet);
        }
    }

    public void clickFavorite() {
        TwitterUser twitterUser = owner.get();
        Tweet tweet = this.tweet.get();
        if (twitterUser != null && tweet != null) {
            twitterUser.favoriteAsync(tweet);
        }
    }
}
