package olnow.phmobile.restapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import olnow.phmobile.PhoneCash;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StringToPhoneCash implements Converter<String, PhoneCash> {
    @Override
    public PhoneCash convert(String source) {
        try {
            return new ObjectMapper().readValue(source, PhoneCash.class);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
