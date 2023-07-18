package org.blossom.localmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(indexName = "user_index")
public class LocalUser {
    private int id;
    private String fullName;
    private String userName;
    private String imageUrl;
}
