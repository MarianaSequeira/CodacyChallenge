package codacyChallenge.utils;

import codacyChallenge.model.Commit;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class CommitDeserializer extends StdDeserializer<Commit> {

    public CommitDeserializer() {
        this(null);
    }

    public CommitDeserializer(Class<?> vc) {
        super(vc);
    }


    @Override
    public Commit deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {
        Commit commit = new Commit();
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        commit.setSha(node.get("sha").asText());

        JsonNode commitInfo = node.get("commit");

        // try catch block
        JsonNode message = commitInfo.get("message");
        JsonNode authorInfo = commitInfo.get("author");
        JsonNode authorName = authorInfo.get("name");
        JsonNode authorEmail = authorInfo.get("email");
        JsonNode date = authorInfo.get("date");
        commit.setAuthorName(authorName.asText());
        commit.setAuthorEmail(authorEmail.asText());
        commit.setDate(date.asText());
        commit.setMessage(message.asText());

        return commit;
    }
}
