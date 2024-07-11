package com.klaxon.kserver.module.media.subscribe.provider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
class DouBanProviderTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private DouBanProvider douBanProvider;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
        douBanProvider = new DouBanProvider();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testParse() {
        // Mock the response from Jsoup
        String html = "<html><body>"
                + "<div class='upcoming-movies'>"
                + "<span class='title'>Movie Title</span>"
                + "<span class='date'>Jan 1, 2023</span>"
                + "<p class='description'>Movie Description</p>"
                + "</div>"
                + "</body></html>";
        Document doc = Jsoup.parse(html);

        // Use reflection to set the Document in the DouBanProvider
        // This assumes the DouBanProvider has a private field named 'doc' to set
        // If the actual implementation does not use a field named 'doc', you'll need to adjust this.
        try {
            java.lang.reflect.Field docField = DouBanProvider.class.getDeclaredField("doc");
            docField.setAccessible(true);
            docField.set(douBanProvider, doc);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Could not set Document field in DouBanProvider for testing.", e);
        }

        // Call the method to test
        douBanProvider.parse();

        // Verify the output
        String expectedOutput = "Title: Movie Title\n"
                + "Release Date: Jan 1, 2023\n"
                + "Description: Movie Description\n"
                + "--------------------\n";
        assertEquals(expectedOutput, outContent.toString());
    }
}