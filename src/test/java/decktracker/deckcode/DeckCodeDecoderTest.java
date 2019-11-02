package decktracker.deckcode;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DeckCodeDecoderTest {

    @Test
    public void decode() {
        new DeckCodeDecoder().decode("AAEBAZICBOkB4KwC9fwC+KEDDV/+AcQGig60uwLLvALexAKHzgKe0gK7nwOhoQPbpQPZqQMA");
        ArrayList<String> decklist = new ArrayList<String>() {{
            add("Innervate");
            add("Innervate");
            add("Jade Idol");
            add("Jade Idol");
            add("Mistress of Mixtures");
            add("Mistress of Mixtures");
            add("Naturalize");
            add("Untapped Potential");
            add("Wrath");
            add("Wrath");
            add("Jade Blossom");
            add("Jade Blossom");
            add("Branching Paths");
            add("Branching Paths");
            add("Flobbidinous Floop");
            add("Poison Seeds");
            add("Poison Seeds");
            add("Crystal Stag");
            add("Crystal Stag");
            add("Oasis Surger");
            add("Oasis Surger");
            add("Khartut Defender");
            add("Khartut Defender");
            add("Nourish");
            add("Nourish");
            add("Overflow");
            add("Overflow");
            add("N'Zoth, the Corruptor");
            add("Ultimate Infestation");
            add("Ultimate Infestation");
        }};
    }
}