package codacyChallenge.utils;

import codacyChallenge.model.Commit;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class BranchesDeserializer extends StdDeserializer<String> {

    public BranchesDeserializer() {
        this(null);
    }

    public BranchesDeserializer(Class<?> vc) {
        super(vc);
    }


    @Override
    public String deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {

        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        return node.get("name").asText();
    }
}
