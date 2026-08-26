package tools.pantheum.gaia.gs1.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link CharUtils} — character classification helpers. */
@DisplayName("CharUtils")
class CharUtilsTest {

    @Test
    @DisplayName("isUpperAlpha accepts A-Z only")
    void isUpperAlpha() {
        assertTrue(CharUtils.isUpperAlpha('A'));
        assertTrue(CharUtils.isUpperAlpha('Z'));
        assertFalse(CharUtils.isUpperAlpha('a'));
        assertFalse(CharUtils.isUpperAlpha('0'));
    }

    @Test
    @DisplayName("isDigit accepts 0-9 only")
    void isDigit() {
        assertTrue(CharUtils.isDigit('0'));
        assertTrue(CharUtils.isDigit('9'));
        assertFalse(CharUtils.isDigit('A'));
    }

    @Test
    @DisplayName("isHexDigit accepts 0-9, a-f, A-F")
    void isHexDigit() {
        assertTrue(CharUtils.isHexDigit('0'));
        assertTrue(CharUtils.isHexDigit('a'));
        assertTrue(CharUtils.isHexDigit('F'));
        assertFalse(CharUtils.isHexDigit('g'));
    }

    @Test
    @DisplayName("isUnreservedPcenc accepts RFC 3986 unreserved characters")
    void isUnreservedPcenc() {
        assertTrue(CharUtils.isUnreservedPcenc('A'));
        assertTrue(CharUtils.isUnreservedPcenc('0'));
        assertTrue(CharUtils.isUnreservedPcenc('-'));
        assertFalse(CharUtils.isUnreservedPcenc('%'));
        assertFalse(CharUtils.isUnreservedPcenc(' '));
    }
}
