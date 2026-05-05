package net.samitkumar.springboot_with_aws_lambda;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.HashMap;
import java.util.Map;

@DynamoDbBean
public class SessionEntity {

    private String sessionId;
    private Long creationTime;
    private Long lastAccessedTime;
    private Long maxInactiveInterval;
    private Long ttl;
    private Map<String, String> attributes = new HashMap<>();

    public SessionEntity() {}

    @DynamoDbPartitionKey
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Long getCreationTime() { return creationTime; }
    public void setCreationTime(Long creationTime) { this.creationTime = creationTime; }

    public Long getLastAccessedTime() { return lastAccessedTime; }
    public void setLastAccessedTime(Long lastAccessedTime) { this.lastAccessedTime = lastAccessedTime; }

    public Long getMaxInactiveInterval() { return maxInactiveInterval; }
    public void setMaxInactiveInterval(Long maxInactiveInterval) { this.maxInactiveInterval = maxInactiveInterval; }

    public Long getTtl() { return ttl; }
    public void setTtl(Long ttl) { this.ttl = ttl; }

    public Map<String, String> getAttributes() { return attributes; }
    public void setAttributes(Map<String, String> attributes) { this.attributes = attributes; }
}
