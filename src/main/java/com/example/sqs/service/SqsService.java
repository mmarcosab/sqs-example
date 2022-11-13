package com.example.sqs.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SqsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqsService.class);
    private final AmazonSQS amazonSQS;

    public SqsService(AmazonSQS amazonSQS) {
        this.amazonSQS = amazonSQS;
    }

    public CreateQueueResult createQueue(final String queueName) {
        CreateQueueRequest create_request = new CreateQueueRequest(queueName)
                .addAttributesEntry("DelaySeconds", "60")
                .addAttributesEntry("MessageRetentionPeriod", "86400");
        return amazonSQS.createQueue(queueName);
    }

    public ListQueuesResult listQueues() {
        return amazonSQS.listQueues();
    }

    public DeleteQueueResult removeQueue(final String queueName) {
        return amazonSQS.deleteQueue(queueName);
    }


    public SendMessageResult publishMessage(final String queueUrl, final String message) {
        final ObjectMapper objectMapper = new ObjectMapper();
        SendMessageRequest sendMessageRequest = null;
        try {
            sendMessageRequest = new SendMessageRequest().withQueueUrl(queueUrl)
                    .withMessageBody(objectMapper.writeValueAsString(message))
                    .withMessageDeduplicationId(UUID.randomUUID().toString());
            return amazonSQS.sendMessage(sendMessageRequest);
        } catch (JsonProcessingException e) {
            LOGGER.error("JsonProcessingException e : {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Exception e : {}", e.getMessage());
        }
        return null;
    }

    public List<Message> receiveMessages(final String queueUrl) {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest();
        receiveMessageRequest.setQueueUrl(queueUrl);
        receiveMessageRequest.setWaitTimeSeconds(5);
        receiveMessageRequest.setMaxNumberOfMessages(5);
        return amazonSQS.receiveMessage(receiveMessageRequest).getMessages();
    }

}
