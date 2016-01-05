package pixl.plugins.twitter;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

@Configuration
@AutoConfigureBefore(SocialWebAutoConfiguration.class)
@EnableSocial
public class TwitterConfiguration implements EnvironmentAware {

    private RelaxedPropertyResolver propertyResolver;

    @Override
    public void setEnvironment(Environment environment) {
        propertyResolver = new RelaxedPropertyResolver(environment, getPropertyPrefix());
    }

    protected String getPropertyPrefix() {
        return "plugin.twitter.";
    }

    @Bean
    @Scope(value = "singleton")
    public Twitter twitter() {

        String consumerKey = propertyResolver.getRequiredProperty("consumerKey");
        String consumerSecret = propertyResolver.getRequiredProperty("consumerSecret");

        String accessToken = propertyResolver.getRequiredProperty("accessToken");
        String accessTokenSecret = propertyResolver.getRequiredProperty("accessTokenSecret");
        return new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
    }

}