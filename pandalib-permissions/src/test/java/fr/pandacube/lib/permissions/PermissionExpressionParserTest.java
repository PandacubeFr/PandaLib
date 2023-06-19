package fr.pandacube.lib.permissions;

import fr.pandacube.lib.permissions.PermissionExpressionParser.LiteralPermissionTester;
import org.junit.Test;

import static fr.pandacube.lib.permissions.PermissionExpressionParser.evaluate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PermissionExpressionParserTest {

    final java.util.List<String> pList = java.util.Arrays.asList("p1.cmd", "p1.toto", "p2.lol");
    final LiteralPermissionTester tester = pList::contains;

    @Test
    public void evaluateTrue() {
        assertTrue(evaluate("p1.cmd", tester));
    }

    @Test
    public void evaluateFalse() {
        assertFalse(evaluate("p1.notexist", tester));
    }

    @Test
    public void evaluateNegateFalse() {
        assertTrue(evaluate("!p1.notexist", tester));
    }

    @Test
    public void evaluateNegateTrue() {
        assertFalse(evaluate("!p1.cmd", tester));
    }

    @Test
    public void evaluateRevNegateTrue() {
        assertFalse(evaluate("p1.cmd!", tester));
    }

    @Test
    public void evaluateOrBothTrue() {
        assertTrue(evaluate("p1.cmd || p1.toto", tester));
    }

    @Test
    public void evaluateOrTrueFalse() {
        assertTrue(evaluate("p1.cmd || p1.notexist", tester));
    }

    @Test
    public void evaluateOrFalseTrue() {
        assertTrue(evaluate("p1.fefef || p2.lol", tester));
    }

    @Test
    public void evaluateOrBothFalse() {
        assertFalse(evaluate("p1.fefef || p2.lolilol", tester));
    }

    @Test
    public void evaluateAndBothTrue() {
        assertTrue(evaluate("p1.cmd && p1.toto", tester));
    }

    @Test
    public void evaluateAndTrueFalse() {
        assertFalse(evaluate("p1.cmd && p1.notexist", tester));
    }

    @Test
    public void evaluateAndFalseTrue() {
        assertFalse(evaluate("p1.fefef && p2.lol", tester));
    }

    @Test
    public void evaluateAndBothFalse() {
        assertFalse(evaluate("p1.fefef && p2.lolilol", tester));
    }

    @Test
    public void evaluateAndTrueNegateTrueWithSomeExtraSpaces() {
        assertFalse(evaluate("p1.cmd && !p1.toto  ", tester));
    }

    @Test
    public void evaluateAndNegateTrueTrueWithLotOfExtraSpaces() {
        assertFalse(evaluate("   !p1.cmd &&    p1.toto  ", tester));
    }

    @Test(expected = IllegalArgumentException.class)
    public void evaluateBadSyntax1() {
        evaluate("p1.cmd! p2.lol", tester);
    }

    @Test(expected = IllegalArgumentException.class)
    public void evaluateBadSyntax2() {
        evaluate("!p1.cmd & p1.toto", tester);
    }

    @Test(expected = IllegalArgumentException.class)
    public void evaluateBadSyntax3() {
        evaluate("!p1.cmd | p1.toto", tester);
    }

    @Test(expected = IllegalArgumentException.class)
    public void evaluateBadSyntax4() {
        evaluate("p1.not exist", tester);
    }
}