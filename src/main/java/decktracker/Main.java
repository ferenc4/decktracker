package decktracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * For fun Hearthstone tracker
 * Note: not anything close to production level code
 *
 * <a href="https://api.hearthstonejson.com/v1/35057/enUS/cards.collectible.json">Cards collectible json source<</a>
 */
public class Main {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String OUTPUT_PATH = "data.json";

    public static void main(String[] args) throws IOException {
        boolean requestedClose = false;
        //todo implement decoding
        String deckCode = "AAEBAZICBOkB4KwC9fwC+KEDDV/+AcQGig60uwLLvALexAKHzgKe0gK7nwOhoQPbpQPZqQMA";
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

        while (!requestedClose) {
            Game game = recordGame(deckCode, decklist);
            String json = OBJECT_MAPPER.writeValueAsString(game);
            saveJsonEntry(json);
            requestedClose = isCloseRequested();
        }
    }

    private static boolean isCloseRequested() {
        boolean requestedClose;
        System.out.println("Enter 'C' or 'c' to continue, anything else to exit.");
        char[] chars = new Scanner(System.in)
                .next()
                .toLowerCase()
                .toCharArray();
        requestedClose = !(chars.length == 1 && chars[0] == 'c');
        return requestedClose;
    }

    private static void saveJsonEntry(String json) throws IOException {
        File file = new File(OUTPUT_PATH);
        if (file.exists()) {
            RandomAccessFile randomAccessFile = new RandomAccessFile(OUTPUT_PATH, "rw");
            long pos = randomAccessFile.length();
            while (randomAccessFile.length() > 0) {
                pos--;
                randomAccessFile.seek(pos);
                if (randomAccessFile.readByte() == ']') {
                    randomAccessFile.seek(pos - 1);
                    break;
                }
            }
            randomAccessFile.writeBytes(",\n" + json + "\n]");
            randomAccessFile.close();
        } else {
            FileWriter fw = new FileWriter(file);
            fw.write("[\n" + json + "\n]");
            fw.close();
        }
    }

    public static void writeVarInt(DataOutputStream dos, int value) throws IOException {
        //taken from http://wiki.vg/Data_types, altered slightly
        do {
            byte temp = (byte) (value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the
            // number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            dos.writeByte(temp);
        } while (value != 0);
    }

    private static Game recordGame(String deckCode, final ArrayList<String> deckList) {
        System.out.println("Starting new game");
        List<String> deck = (List<String>) deckList.clone();
        List<String> hand = new ArrayList<>();
        boolean gameHasFinished = false;
        Scanner sc = new Scanner(System.in);
        Game game = new Game(deckCode);
        while (!gameHasFinished) {
            List<String> displayableDeck = toDisplayable(deck);
            List<String> displayableHand = toDisplayable(hand);
            display(displayableDeck, "Deck");
            display(displayableHand, "Hand");
            String action = sc.next();
            switch (action.toCharArray()[0]) {
                case 'w':
                    System.out.println("You won!");
                    gameHasFinished = true;
                    game.youWon = true;
                    break;
                case 'l':
                    System.out.println("You lost!");
                    gameHasFinished = true;
                    break;
                case 'p':
                    Scanner scp = new Scanner(action.substring(1));
                    int indexP = scp.nextInt();
                    String affectedP = displayableHand.get(indexP);
                    hand.remove(affectedP);
                    game.played.add(affectedP);
                    System.out.println("You played " + affectedP);
                    break;
                case 'd':
                    Scanner scd = new Scanner(action.substring(1));
                    int indexD = scd.nextInt();
                    String affectedD = displayableDeck.get(indexD);
                    hand.add(affectedD);
                    deck.remove(affectedD);
                    game.drawn.add(affectedD);
                    System.out.println("You drew " + affectedD);
                    if (game.drawn.size() < 4) {
                        game.mulligan.add(affectedD);
                    }
                    break;
                default:
                    System.out.println("Invalid input " + action);
            }
        }
        return game;
    }

    private static void display(List<String> displayable, String title) {
        String deckDisplay = IntStream.range(0, displayable.size())
                .mapToObj(i -> String.format("%d - %s", i, displayable.get(i)))
                .collect(Collectors.joining(",\n"));
        System.out.println(title);
        System.out.println(deckDisplay);
    }

    public static List<String> toDisplayable(List<String> deck) {
        return new ArrayList<>(new HashSet<>(deck));
    }

    @JsonSerializableSchema
    public static class Game {
        String deckCode;
        boolean youWon = false;
        List<String> played = new ArrayList<>();
        List<String> drawn = new ArrayList<>();
        List<String> mulligan = new ArrayList<>();

        public Game(String deckCode) {
            this.deckCode = deckCode;
        }

        public boolean isYouWon() {
            return youWon;
        }

        public List<String> getPlayed() {
            return played;
        }

        public List<String> getDrawn() {
            return drawn;
        }

        public List<String> getMulligan() {
            return mulligan;
        }
    }
}
