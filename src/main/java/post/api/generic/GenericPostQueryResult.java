package post.api.generic;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "posts")
public class GenericPostQueryResult {

    private int count;

    private long offset;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "post", isAttribute = true)
    private final List<GenericPost> posts = new ArrayList<>();

}
