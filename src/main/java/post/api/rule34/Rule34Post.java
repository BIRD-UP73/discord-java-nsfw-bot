package post.api.rule34;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import enums.PostSite;
import lombok.Getter;
import lombok.Setter;
import post.Post;
import util.CustomDateDeserializer;

import java.util.Date;

@Getter
@Setter
public class Rule34Post extends Post {

    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date createdAt;

    @Override
    public PostSite getSite() {
        return PostSite.RULE34;
    }
}
