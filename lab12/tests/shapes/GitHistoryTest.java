package shapes;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Checks your git history against the partner rules for this lab. Run it after both solo
 * commits are pushed and pulled (H1 to H3 should pass), and again after the joint commit
 * (everything should pass). Staff check the same rules on GitHub after the deadline.
 *
 * Groups of three: if ShapeWorld is committed by someone who made neither solo commit, that
 * person is treated as Partner C, and the commit doesn't need a Co-authored-by line.
 *
 * It runs git in the folder your tests run from, so open lab12 (or your whole repo) as the
 * IntelliJ project. You do not need to modify this file.
 */
public class GitHistoryTest {
    private static final String PLUS = "src/shapes/Plus.java";
    private static final String SQUARE = "src/shapes/Square.java";
    private static final String WORLD = "src/shapes/ShapeWorld.java";

    /** Commits by these authors are ignored, e.g. staff fixes pulled in from the skeleton. */
    private static final Set<String> IGNORED_AUTHOR_EMAILS = Set.of();

    private static final Pattern CO_AUTHOR =
            Pattern.compile("(?im)^\\s*co-authored-by:.*<([^>]+)>\\s*$");

    /** One non-merge commit that changed at least one of this lab's files. */
    private static class Commit {
        private final String hash;
        private final String email;
        private final String name;
        private final String body;
        private final Map<String, Character> changes;

        Commit(String hash, String email, String name, String body, Map<String, Character> changes) {
            this.hash = hash;
            this.email = email;
            this.name = name;
            this.body = body;
            this.changes = changes;
        }

        boolean touches(String file) {
            return changes.containsKey(file);
        }

        boolean modifies(String file) {
            Character status = changes.get(file);
            return status != null && status == 'M';
        }

        boolean addsAnyLabFile() {
            return changes.containsValue('A');
        }

        @Override
        public String toString() {
            return hash.substring(0, 7) + " by " + name + " <" + email + ">";
        }
    }

    private static String gitError;
    private static List<Commit> commits;
    private static Commit plusCommit;
    private static Commit squareCommit;
    private static Commit worldCommit;
    /** True if a third person (Partner C) made the ShapeWorld commit. */
    private static boolean trio;

    @BeforeAll
    public static void loadHistory() {
        try {
            commits = studentCommits(readLog());
        } catch (IOException | IllegalStateException e) {
            gitError = "Couldn't read your git history: " + e.getMessage()
                    + "\nMake sure git is installed and lab12 is inside your git repo.";
            return;
        }
        plusCommit = soloCommit(PLUS, SQUARE, WORLD);
        squareCommit = soloCommit(SQUARE, PLUS, WORLD);
        for (Commit c : commits) {
            if (c.modifies(WORLD)) {
                worldCommit = c;
                break;
            }
        }
        trio = worldCommit != null && plusCommit != null && squareCommit != null
                && !sameAuthor(plusCommit, squareCommit)
                && !sameAuthor(worldCommit, plusCommit) && !sameAuthor(worldCommit, squareCommit);
    }

    @Test
    @DisplayName("H1: Partner A committed Plus.java on its own")
    public void h1SoloPlus() {
        checkSolo(plusCommit, PLUS, "Partner A");
    }

    @Test
    @DisplayName("H2: Partner B committed Square.java on its own")
    public void h2SoloSquare() {
        checkSolo(squareCommit, SQUARE, "Partner B");
    }

    @Test
    @DisplayName("H3: The two solo commits have different authors")
    public void h3TwoAuthors() {
        requireSoloCommits();
        assertTrue(!sameAuthor(plusCommit, squareCommit),
                "Both solo commits were made by " + plusCommit.email + ".\n"
                + "Each partner must commit their own shape from their own laptop, with their own "
                + "git config user.email.");
    }

    @Test
    @DisplayName("H4: Both solo commits were pulled in before the ShapeWorld commit")
    public void h4MergeFirst() {
        requireSoloCommits();
        requireWorldCommit();
        List<String> missing = new ArrayList<>();
        if (!isAncestor(plusCommit, worldCommit)) {
            missing.add("the Plus commit " + plusCommit);
        }
        if (!isAncestor(squareCommit, worldCommit)) {
            missing.add("the Square commit " + squareCommit);
        }
        assertTrue(missing.isEmpty(),
                "The ShapeWorld commit " + worldCommit + " was made before pulling "
                + String.join(" and ", missing) + ".\n"
                + "Pull both solo commits onto the laptop ShapeWorld is written on (the driver's, "
                + "or Partner C's in a group of three) before starting ShapeWorld.");
    }

    @Test
    @DisplayName("H5: The ShapeWorld commit credits the other partner (or is by Partner C)")
    public void h5CoAuthored() {
        requireWorldCommit();
        if (trio) {
            return;
        }
        Matcher m = CO_AUTHOR.matcher(worldCommit.body);
        boolean credited = false;
        while (m.find()) {
            if (!m.group(1).trim().equalsIgnoreCase(worldCommit.email)) {
                credited = true;
            }
        }
        if (credited) {
            return;
        }
        if (plusCommit == null || squareCommit == null) {
            fail("The ShapeWorld commit " + worldCommit + " has no Co-authored-by line.\n"
                    + "If you're a group of three, this passes once H1 and H2 pass and show that "
                    + "the ShapeWorld author is Partner C.");
        }
        fail("The ShapeWorld commit " + worldCommit + " has no Co-authored-by line naming "
                + "the navigator.\nAdd one as the last paragraph of the commit message, like:\n"
                + "    Co-authored-by: Navigator Name <navigator@berkeley.edu>\n"
                + "In a group of three, Partner C makes this commit from their own laptop, with "
                + "their own git config user.email.");
    }

    @Test
    @DisplayName("H6: Nobody changed their partner's shape before the joint part")
    public void h6StayInYourLane() {
        requireSoloCommits();
        if (sameAuthor(plusCommit, squareCommit)) {
            fail("Skipped until H3 passes.");
        }
        List<String> problems = new ArrayList<>();
        for (Commit c : commits) {
            if (worldCommit != null && !isAncestor(c, worldCommit)) {
                continue;
            }
            // In a pair, the ShapeWorld commit itself may fix either shape.
            if (c != worldCommit) {
                if (sameAuthor(c, plusCommit) && c.modifies(SQUARE)) {
                    problems.add(c + " (Partner A) changed Square.java");
                }
                if (sameAuthor(c, squareCommit) && c.modifies(PLUS)) {
                    problems.add(c + " (Partner B) changed Plus.java");
                }
            }
            if (trio && sameAuthor(c, worldCommit)) {
                for (String file : List.of(PLUS, SQUARE)) {
                    if (c.modifies(file)) {
                        problems.add(c + " (Partner C) changed " + new File(file).getName());
                    }
                }
            }
        }
        assertTrue(problems.isEmpty(), String.join("\n", problems)
                + "\nEach partner changes only their own shape until the joint part. "
                + "In a pair, fix your partner's shape together in the ShapeWorld commit. "
                + "In a group of three, A and B fix their own shapes, push, and C pulls.");
    }

    /* ----- Helpers ----- */

    private static void checkSolo(Commit solo, String file, String who) {
        requireGit();
        if (solo != null) {
            return;
        }
        String name = new File(file).getName();
        for (Commit c : commits) {
            if (c.modifies(file)) {
                fail("Commit " + c + " changed " + name + " along with another lab file.\n"
                        + "Each solo commit should change only your own shape: "
                        + "git add only " + name + " before committing.");
            }
        }
        fail("No commit changes " + name + " yet.\n"
                + who + ": finish " + name + ", git add only that file, commit, and push.\n"
                + "Your partner then runs git pull.");
    }

    private static void requireGit() {
        if (gitError != null) {
            fail(gitError);
        }
    }

    private static void requireSoloCommits() {
        requireGit();
        if (plusCommit == null || squareCommit == null) {
            fail("Needs H1 and H2 to pass first.");
        }
    }

    private static void requireWorldCommit() {
        requireGit();
        if (worldCommit == null) {
            fail("No ShapeWorld commit yet. That's expected until you finish the joint part.");
        }
    }

    private static boolean sameAuthor(Commit a, Commit b) {
        return a.email.equalsIgnoreCase(b.email);
    }

    /** Returns the first commit that changes MINE and neither of the OTHERS. */
    private static Commit soloCommit(String mine, String... others) {
        for (Commit c : commits) {
            if (c.modifies(mine) && !c.touches(others[0]) && !c.touches(others[1])) {
                return c;
            }
        }
        return null;
    }

    /** Drops commits that add lab files (the skeleton) and commits by ignored authors. */
    private static List<Commit> studentCommits(List<Commit> all) {
        List<Commit> result = new ArrayList<>();
        for (Commit c : all) {
            if (!c.addsAnyLabFile() && !IGNORED_AUTHOR_EMAILS.contains(c.email)) {
                result.add(c);
            }
        }
        return result;
    }

    /** Returns every non-merge commit that changed a lab file, oldest first. */
    private static List<Commit> readLog() throws IOException {
        String out = git("log", "--no-merges", "--reverse", "--topo-order", "--name-status",
                "--format=%x1e%H%x1f%ae%x1f%an%x1f%B%x1f", "--", ".");
        List<Commit> result = new ArrayList<>();
        for (String record : out.split("\u001e")) {
            if (record.isBlank()) {
                continue;
            }
            String[] fields = record.split("\u001f", -1);
            Map<String, Character> changes = new HashMap<>();
            for (String line : fields[4].split("\\R")) {
                String[] parts = line.trim().split("\t");
                if (parts.length < 2) {
                    continue;
                }
                String path = parts[parts.length - 1];
                for (String file : List.of(PLUS, SQUARE, WORLD)) {
                    if (path.endsWith(file)) {
                        changes.put(file, parts[0].charAt(0));
                    }
                }
            }
            if (!changes.isEmpty()) {
                result.add(new Commit(fields[0].trim(), fields[1].trim().toLowerCase(),
                        fields[2].trim(), fields[3], changes));
            }
        }
        return result;
    }

    private static boolean isAncestor(Commit ancestor, Commit descendant) {
        if (ancestor.hash.equals(descendant.hash)) {
            return true;
        }
        try {
            return run("merge-base", "--is-ancestor", ancestor.hash, descendant.hash) == 0;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String git(String... args) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int status = run(out, args);
        if (status != 0) {
            throw new IllegalStateException("git " + args[0] + " exited with status " + status);
        }
        return out.toString(StandardCharsets.UTF_8);
    }

    private static int run(String... args) throws IOException {
        return run(new ByteArrayOutputStream(), args);
    }

    private static int run(ByteArrayOutputStream out, String... args) throws IOException {
        List<String> command = new ArrayList<>(List.of("git", "-c", "core.quotepath=off"));
        command.addAll(List.of(args));
        Process p = new ProcessBuilder(command).redirectError(ProcessBuilder.Redirect.DISCARD).start();
        try (InputStream in = p.getInputStream()) {
            in.transferTo(out);
        }
        try {
            return p.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("interrupted while running git", e);
        }
    }
}
