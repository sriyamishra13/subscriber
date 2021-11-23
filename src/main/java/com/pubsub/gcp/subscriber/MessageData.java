package com.pubsub.gcp.subscriber;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonDeserialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageData {
    @JsonProperty("messageId")
    public Long messageId;
    @JsonProperty("value")
    public String value;
}
