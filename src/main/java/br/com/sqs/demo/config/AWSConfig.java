package br.com.sqs.demo.config;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.Session;


@EnableJms
@Configuration
@RequiredArgsConstructor
public class AWSConfig {

    private final SQSConnectionFactory connectionFactory;

    public AWSCredentials credentials() {
        AWSCredentials credentials = new BasicAWSCredentials(
                "accessKey",
                "secretKey"
        );
        return credentials;
    }

    @Bean
    public SQSConnectionFactory createConnectionFactory(){
        return SQSConnectionFactory.builder()
                .withRegion(Region.getRegion(Regions.SA_EAST_1))
                .withAWSCredentialsProvider(new StaticCredentialsProvider(credentials()))
                .build();
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(){
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(this.connectionFactory);
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);

        return factory;
    }

    @Bean
    public JmsTemplate defaultJmsTemplate(){
        return new JmsTemplate(this.connectionFactory);
    }

}
