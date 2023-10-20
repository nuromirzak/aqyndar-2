package org.nurma.aqyndar.util;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class AnnotationUtil {
    public static List<Integer> extractAnnotations(final String content) {
        List<Integer> annotations = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(.*?)]\\((\\d+)\\)");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String id = matcher.group(2);
            try {
                annotations.add(Integer.parseInt(id));
            } catch (NumberFormatException e) {
                log.warn("Invalid annotation id: {}", id);
            }
        }

        return Collections.unmodifiableList(annotations);
    }

    public static boolean hasOverlappingAnnotations(final String content) {
        Pattern pattern = Pattern.compile("\\[(.*?)]\\((\\d+)\\)");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String text = matcher.group(1);
            if (text.contains("[") || text.contains("]")) {
                return true;
            }
        }
        return false;
    }
}
