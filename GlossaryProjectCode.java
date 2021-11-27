import java.util.Comparator;

import components.map.Map;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * This program creates a html glossary for a given set of words/definitions
 *
 * @author Matthew Lake
 *
 */
public final class GlossaryProjectCode {

    private static class StringLT implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    /**
     * Start the html index page
     *
     * @param out
     *            SimpleWriter
     */
    public static void htmlIndexStart(SimpleWriter out) {
        out.println("<html>");
        out.println("<head>");
        out.println("<title> Glossary </title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2> Glossary </h2>");
        out.println("<hr>");
        out.println("<h3> Index </h3>");
    }

    /**
     * This method makes a map of words and definitions from the SimpleReader
     *
     * @param in
     *            SimpleReader
     * @return A map with all the terms and definitions
     */
    public static Map<String, String> gettingTermsAndDefinitions(
            SimpleReader in) {
        Map<String, String> m = new Map1L<>(); //Initalizing a new map
        while (!in.atEOS()) { //While the end of the input file hasnt been reched
            String word = in.nextLine();
            String def = "";
            String next = in.nextLine();
            while (!in.atEOS() && next.length() != 0) {
                def = def.concat(next); //Combine the strings
                next = in.nextLine();
            }
            m.add(word, def); //Add the word and def to the map
        }
        return m;
    }

    /**
     * This method takes a map of words and definitions and creating a sorted
     * Alphabetically Queue of words in return.
     *
     * @param m
     *            A map of words and definitions
     * @return A alphabetically sorted Queue
     */
    public static Queue<String> sortingTerms(Map<String, String> m) {
        Map<String, String> temp = m.newInstance(); //New map
        Queue<String> terms = new Queue1L<>(); //New Queue
        while (m.size() != 0) {
            Map.Pair<String, String> p = m.removeAny();
            String word = p.key();
            terms.enqueue(word); //Add word to the Queue
            temp.add(p.key(), p.value()); //Restoring the map
        }
        m.transferFrom(temp);
        terms.sort(new StringLT());
        return terms;
    }

    /**
     * This method generates the middle of the index page regarding the listing
     * of the words
     *
     * @param list
     *            A Queue of words
     * @param out
     *            SimpleWriter
     * @requires List.length() > 0
     */
    public static void htmlIndexMiddle(Queue<String> list, SimpleWriter out) {
        out.println("<ul>");
        int size = list.length();
        for (int i = 0; i < size; i++) {
            String current = list.dequeue();
            out.println("<li>");
            out.println(
                    "<a href =\"" + current + ".html\">" + current + "</a>");
            out.println("</li>");
            list.enqueue(current);
        }
        out.println("</ul>");
    }

    /**
     * This method finishes off the commonly last two lines in any html pages
     *
     * @param out
     *            SimpleWriter
     */
    public static void htmlEnd(SimpleWriter out) {
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * This method creates a html file for a word that is currently being used.
     * Also, linking the the definition to that word and having a return to
     * index page path. Lastly if a word in the definition matches any of the
     * other words in the map the html must have a path to that other word.
     *
     * @param out
     *            The SimpleWriter to write to a file
     *
     * @param word
     *            The current word being used to print
     *
     * @param definition
     *            The definition to the word passed into the method
     * @param m
     *            The map with all of the other terms and their definitions
     * @requires word.length() > 0 , definition.length() > 0 , m.size() > 0
     */
    public static void page(SimpleWriter out, String word, String definition,
            Map<String, String> m) {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>" + word + "</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>");
        out.println("<b>");
        out.println("<i>");
        out.println("<font color=\"red\">" + word + "</font>");
        out.println("</i>");
        out.println("</b>");
        out.println("</h2>");
        out.print("<blockquote>");
        Set<Character> seperators = new Set1L<>(); //New Set
        seperators.add(',');
        seperators.add(' ');
        seperators.add('.');
        seperators.add('!');
        seperators.add('?');
        seperators.add('-');
        int i = 0;
        while (i < definition.length()) {
            String current = nextWordOrSeparator(definition, i, seperators);
            i = i + current.length();
            if (m.hasKey(current)) {
                out.print("<a href=\"" + current + ".html" + "\">" + current
                        + "</a>");
            } else {
                out.print(current);
            }
        }
        out.print("</blockquote>");
        out.println("<hr>");
        out.println("<p>");
        out.println("Return to ");
        out.println("<a href=\"index.html\">index</a>");
        out.println(".");
        out.println("</p>");
        htmlEnd(out);
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";
        char k = text.charAt(position);
        int posCopy = position;
        if (separators.contains(k)) {
            while (posCopy < text.length()
                    && separators.contains(text.charAt(posCopy))) {
                posCopy++;
            }
        } else {
            while (posCopy < text.length()
                    && !separators.contains(text.charAt(posCopy))) {
                posCopy++;
            }
        }
        return text.substring(position, posCopy);
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        out.print("Enter a input file: ");
        String file1 = in.nextLine();
        SimpleReader fileIn = new SimpleReader1L(file1);
        out.print(
                "Enter the folder where you would like to store thr output files: ");
        String folder = in.nextLine();
        String main = folder + "/index.html";
        SimpleWriter fileOut = new SimpleWriter1L(main);
        htmlIndexStart(fileOut);
        Map<String, String> m = gettingTermsAndDefinitions(fileIn);
        Queue<String> terms = sortingTerms(m);
        htmlIndexMiddle(terms, fileOut);
        htmlEnd(fileOut);
        for (int i = 0; i < m.size(); i++) {
            String currentWord = terms.dequeue();
            String definition = m.value(currentWord);
            SimpleWriter currentPage = new SimpleWriter1L(
                    folder + "/" + currentWord + ".html");
            page(currentPage, currentWord, definition, m);
            currentPage.close();
        }
        fileIn.close();
        fileOut.close();
        in.close();
        out.close();
    }

}
