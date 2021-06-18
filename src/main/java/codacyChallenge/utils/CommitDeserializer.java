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

    /*
    "sha": "581afb153d8749867b0628c6f41bb88bd580d9a9",
    "node_id": "MDY6Q29tbWl0Mjk2MDYwNDM2OjU4MWFmYjE1M2Q4NzQ5ODY3YjA2MjhjNmY0MWJiODhiZDU4MGQ5YTk=",
    "commit": {
        "author": {
            "name": "sernadela",
            "email": "pedro.sernadela@gmail.com",
            "date": "2015-03-18T17:51:28Z"
        },
        "committer": {
            "name": "sernadela",
            "email": "pedro.sernadela@gmail.com",
            "date": "2015-03-18T17:51:28Z"
        },
        "message": "Merge pull request #27 from bioinformatics-ua/enhancement/readme\n\nEnhancement/readme",
     */


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
