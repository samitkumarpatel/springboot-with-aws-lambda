package net.samitkumar.springboot_with_aws_lambda;

import lombok.Data;
import lombok.Getter;
import net.samitkumar.springboot_with_aws_lambda.utility.TableName;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.HashMap;
import java.util.Map;

@DynamoDbBean
@TableName(name = "spring-sessions")
@Data
public class SessionEntity {

    @Getter(onMethod_ = {@DynamoDbPartitionKey})
    private String sessionId;
    private Long creationTime;
    private Long lastAccessedTime;
    private Long maxInactiveInterval;
    private Long ttl;
    private Map<String, String> attributes = new HashMap<>();

}
