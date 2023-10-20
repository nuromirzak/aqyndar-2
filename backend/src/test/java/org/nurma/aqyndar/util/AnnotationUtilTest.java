package org.nurma.aqyndar.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnnotationUtilTest {
    @Test
    public void testNormalInput() {
        List<Integer> result = AnnotationUtil.extractAnnotations("[text](1)[more text](2)");
        assertEquals(List.of(1, 2), result);
    }

    @Test
    public void testEmptyInput() {
        List<Integer> result = AnnotationUtil.extractAnnotations("");
        assertEquals(List.of(), result);
    }

    @Test
    public void testMixedInput() {
        List<Integer> result = AnnotationUtil.extractAnnotations("[note](1)[bad note](notAnInteger)");
        assertEquals(List.of(1), result);
    }

    @Test
    public void testTextOnly() {
        List<Integer> result = AnnotationUtil.extractAnnotations("Just some text");
        assertEquals(List.of(), result);
    }

    @Test
    public void testNegativeNumbers() {
        List<Integer> result = AnnotationUtil.extractAnnotations("[text](-1)");
        assertEquals(List.of(), result);
    }

    @Test
    public void testIntegerBound() {
        List<Integer> result = AnnotationUtil.extractAnnotations("[text](2147483647)[text](-2147483648)");
        assertEquals(List.of(2147483647), result);
    }

    @Test
    public void testUnclosedBrackets() {
        List<Integer> result = AnnotationUtil.extractAnnotations("[text(1)");
        assertEquals(List.of(), result);
    }

    @Test
    @Disabled
    public void testEmbeddedAnnotations() {
        List<Integer> result = AnnotationUtil.extractAnnotations("[text [inner](42)](1)");
        assertEquals(List.of(1), result);
    }

    @Test
    public void testMassiveText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append("[text](").append(i).append(")");
        }
        List<Integer> result = AnnotationUtil.extractAnnotations(sb.toString());

        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            expected.add(i);
        }
        assertEquals(expected, result);
    }
    @Test
    public void testNoAnnotations() {
        assertFalse(AnnotationUtil.hasOverlappingAnnotations("This has no annotations."));
    }

    @Test
    public void testSingleAnnotation() {
        assertFalse(AnnotationUtil.hasOverlappingAnnotations("[This has a single annotation](1)"));
    }

    @Test
    public void testMultipleNonOverlappingAnnotations() {
        assertFalse(AnnotationUtil.hasOverlappingAnnotations("[This has](1) [multiple](2) [annotations](3)"));
    }

    @Test
    public void testSingleLevelNestedAnnotation() {
        assertTrue(AnnotationUtil.hasOverlappingAnnotations("[This has [nested](4) annotation](1)"));
    }

    @Test
    public void testMultipleNestedAnnotations() {
        assertTrue(AnnotationUtil.hasOverlappingAnnotations("[This [has](2)](1) [multiple [nested](4)](3) [annotations](5)"));
    }

    @Test
    public void testMultipleLevelsOfNestedAnnotations() {
        assertTrue(AnnotationUtil.hasOverlappingAnnotations("[This [has [nested](6)](2)](1)"));
    }

    @Test
    public void testUnclosedBrackets1() {
        assertFalse(AnnotationUtil.hasOverlappingAnnotations("[This has unclosed bracket(1)"));
    }

    @Test
    @Disabled
    public void testNestedButIncompleteAnnotation() {
        assertFalse(AnnotationUtil.hasOverlappingAnnotations("[This has [incomplete nested](1)"));
    }

    @Test
    public void testNotNestedButOverlappingBrackets() {
        assertFalse(AnnotationUtil.hasOverlappingAnnotations("[This has] extra [brackets(1)]"));
    }

    @Test
    public void testEmptyString() {
        assertFalse(AnnotationUtil.hasOverlappingAnnotations(""));
    }
}