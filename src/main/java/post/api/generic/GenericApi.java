package post.api.generic;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import lombok.extern.slf4j.Slf4j;
import post.Post;
import post.PostQueryResult;
import post.api.PostApi;
import post.api.PostApiUtil;
import post.api.PostFetchException;
import post.autocomplete.AutocompleteException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class GenericApi implements PostApi {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final XmlMapper xmlMapper = new XmlMapper();

    @Override
    public boolean hasAutocomplete() {
        return true;
    }

    @Override
    public String getAutocompleteUrl(String tags) {
        return getBaseUrl() + "autocomplete.php?q=" + tags;
    }

    @Override
    public Optional<Integer> getMaxCount() {
        return Optional.empty();
    }

    @Override
    public int fetchCount(String tags) throws PostFetchException {
        String urlString = getBaseUrl() + "index.php?page=dapi&s=post&q=index&limit=0&tags=" + tags;
        PostQueryResult postQueryResult = getResult(urlString);

        return postQueryResult.getCount();
    }

    @Override
    public Optional<Post> fetchById(long id) throws PostFetchException {
        String urlString = getBaseUrl() + "index.php?page=dapi&s=post&q=index&limit=1&id=" + id;
        PostQueryResult queryResult = getResult(urlString);
        return getFirstPost(queryResult);
    }

    @Override
    public Optional<Post> fetchByTagsAndPage(String tags, int page) throws PostFetchException {
        String urlString = getBaseUrl() + "index.php?page=dapi&s=post&q=index&limit=1&tags=" + tags + "&pid=" + page;
        PostQueryResult queryResult = getResult(urlString);

        Optional<Post> post = getFirstPost(queryResult);
        post.ifPresent(p -> p.setSite(getSite()));

        return post;
    }

    @Override
    public List<ApplicationCommandOptionChoiceData> autocomplete(String input) throws AutocompleteException {
        String lastInput = PostApiUtil.getLastAutocompleteString(input);
        String urlString = getAutocompleteUrl(lastInput);
        return PostApiUtil.autocomplete(urlString, lastInput);
    }

    private Optional<Post> getFirstPost(PostQueryResult queryResult) {
        return queryResult.getPosts().stream().findFirst().map(x -> {
            x.setSite(getSite());
            return x;
        });
    }

    private PostQueryResult getResult(String urlString) throws PostFetchException {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new PostFetchException("Error occurred fetching post");
            }

            return xmlMapper.readValue(response.body(), PostQueryResult.class);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new PostFetchException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage(), e);
            throw new PostFetchException(e.getMessage(), e);
        }
    }
}