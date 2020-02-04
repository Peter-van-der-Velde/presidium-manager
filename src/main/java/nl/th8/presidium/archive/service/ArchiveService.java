package nl.th8.presidium.archive.service;

import nl.th8.presidium.archive.TypeNotFoundException;
import nl.th8.presidium.home.controller.dto.Kamerstuk;
import nl.th8.presidium.home.controller.dto.KamerstukType;
import nl.th8.presidium.home.data.KamerstukRepository;
import nl.th8.presidium.scheduler.KamerstukNotFoundException;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils.*;

@Service
public class ArchiveService {

    @Autowired
    KamerstukRepository repository;

    public String getKamerstukHtml(String type, String id) throws KamerstukNotFoundException {
        if(id.length() < 4) {
            id = padLeftZeros(id, 4);
        }
        type = type.toUpperCase();

        Optional<Kamerstuk> possibleKamerstuk = repository.findByCallsignEqualsAndPostedIsTrue(type+id);

        if(possibleKamerstuk.isPresent()) {
            MutableDataSet options = new MutableDataSet();
            Kamerstuk kamerstuk = possibleKamerstuk.get();
            options.set(Parser.HEADING_NO_ATX_SPACE, true)
                    .set(Parser.LISTS_CODE_INDENT, 1)
                    .set(Parser.LISTS_ITEM_INDENT, 1);
            String kamerstukText = String.format("##%s \n \n%s", kamerstuk.getTitle(), kamerstuk.getContent());
            Parser parser = Parser.builder(options).build();
            Node kamerstukMarkdown = parser.parse(kamerstukText);
            HtmlRenderer renderer = HtmlRenderer.builder(options).build();

            return renderer.render(kamerstukMarkdown);
        }
        throw new KamerstukNotFoundException();
    }

    public String getNiceCallsign(String type, String id) {
        if(id.length() < 4) {
            id = padLeftZeros(id, 4);
        }
        type = type.toUpperCase();
        return type+id;
    }

    public List<Kamerstuk> getKamerstukkenForType(String type) throws TypeNotFoundException {
        for(KamerstukType k : KamerstukType.getPublics()) {
            if(k.getCall().equals(type)) {
                List<Kamerstuk> kamerstukList = repository.findAllByPostedIsTrueAndCallsignContains(type);
                return kamerstukList.stream()
                        .sorted(Comparator.comparing(Kamerstuk::getCallsign))
                        .collect(Collectors.toList());
            }
        }
        throw new TypeNotFoundException();
    }

    public List<Kamerstuk> getKamerstukkenForTypeFiltered(String type, String filterString) throws TypeNotFoundException {
        List<Kamerstuk> kamerstukList = new ArrayList<>();
        for(KamerstukType k : KamerstukType.getPublics()) {
            if(k.getCall().equals(type)) {
                kamerstukList = repository.findAllByPostedIsTrueAndCallsignContains(type);
            }
        }
        if(kamerstukList.isEmpty()) {
            throw new TypeNotFoundException();
        }

        Predicate<Kamerstuk> callsignContains = kamerstuk -> StringUtils.containsIgnoreCase(kamerstuk.getCallsign(), filterString);
        Predicate<Kamerstuk> titleContains = kamerstuk -> StringUtils.containsIgnoreCase(kamerstuk.getTitle(), filterString);
        Predicate<Kamerstuk> contentContains = kamerstuk -> StringUtils.containsIgnoreCase(kamerstuk.getContent(), filterString);
        return kamerstukList.stream()
                .filter(callsignContains.or(titleContains).or(contentContains))
                .sorted(Comparator.comparing(Kamerstuk::getCallnumber))
                .collect(Collectors.toList());
    }

    private String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }
}