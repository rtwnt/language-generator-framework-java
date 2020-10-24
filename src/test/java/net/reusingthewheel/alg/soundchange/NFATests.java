package net.reusingthewheel.alg.soundchange;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

class NFATests {

    static class SymbolSequenceConverter implements ArgumentConverter {

        @Override
        public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
            if (source instanceof String) {
                var symbols = ((String) source).split("");

                return Arrays.asList(symbols);
            } else if (source == null) {
                return new ArrayList();
            }

            throw new ArgumentConversionException(source + " is not a valid sequence of symbols");
        }
    }

    private void testAutomaton(NFA automaton, List<String> symbols, boolean expected) {
        var test = Assertions.assertThat(automaton.isMatch(symbols));
        if (expected) {
            test.isTrue();
        } else {
            test.isFalse();
        }
    }

    @ParameterizedTest
    @CsvSource({"true,", "false,b", "false,ax", "false,bx", "false,bx", "false,xa"})
    void testIsMatchForEmptySymbolNFA(boolean result, @ConvertWith(SymbolSequenceConverter.class) List<String> symbols) {
        var automaton = NFA.newEmptySymbolNFA();
        testAutomaton(automaton, symbols, result);
    }

    @ParameterizedTest
    @CsvSource({"true,a", "false,b", "false,ax", "false,bx", "false,bx", "false,xa"})
    void testIsMatchForSymbolNFA(boolean result, @ConvertWith(SymbolSequenceConverter.class) List<String> symbols) {
        var automaton = NFA.newSymbolNFA("a");
        testAutomaton(automaton, symbols, result);
    }

    @ParameterizedTest
    @CsvSource({"true,a", "true,b", "false,ax", "false,bx", "false,xb", "false,xa"})
    void testIsMatchForUnionNFA(boolean result, @ConvertWith(SymbolSequenceConverter.class) List<String> symbols) {
        var automaton = NFA.newUnionNFA(NFA.newSymbolNFA("a"), NFA.newSymbolNFA("b"));
        testAutomaton(automaton, symbols, result);
    }

    @ParameterizedTest
    @CsvSource({"true,ab", "false,a", "false,b", "false,ba", "false,abx", "false,xab"})
    void testIsMatchForConcatenateNFA(boolean result, @ConvertWith(SymbolSequenceConverter.class) List<String> symbols) {
        var automaton = NFA.newConcatenateNFA(NFA.newSymbolNFA("a"), NFA.newSymbolNFA("b"));
        testAutomaton(automaton, symbols, result);
    }

    @ParameterizedTest
    @CsvSource({"true,a", "true,", "true,aa", "false,b", "false,ax", "false,xa"})
    void testIsMatchForClosureNFA(boolean result, @ConvertWith(SymbolSequenceConverter.class) List<String> symbols) {
        var automaton = NFA.newKleeneClosureNFA(NFA.newSymbolNFA("a"));
        testAutomaton(automaton, symbols, result);
    }

    @ParameterizedTest
    @CsvSource({"true,a", "true,", "false,aa", "false,b", "false,ax", "false,xa"})
    void testIsMatchForZeroOrOneNFA(boolean result, @ConvertWith(SymbolSequenceConverter.class) List<String> symbols) {
        var automaton = NFA.newZeroOrOneNFA(NFA.newSymbolNFA("a"));
        testAutomaton(automaton, symbols, result);
    }

    @ParameterizedTest
    @CsvSource({"true,a", "false,", "true,aa", "false,b", "false,ax", "false,xa"})
    void testIsMatchForOneOrMoreNFA(boolean result, @ConvertWith(SymbolSequenceConverter.class) List<String> symbols) {
        var automaton = NFA.newOneOrMoreNFA(NFA.newSymbolNFA("a"));
        testAutomaton(automaton, symbols, result);
    }

    private static Stream<Arguments> getArgsForTestGetMatchingPrefixThrowsIllegalArgumentExceptionForEmptySymbolSequence() {
        return Stream.of(
                Arguments.of(NFA.newEmptySymbolNFA()),
                Arguments.of(NFA.newSymbolNFA("a")),
                Arguments.of(NFA.newConcatenateNFA(NFA.newSymbolNFA("a"), NFA.newSymbolNFA("b"))),
                Arguments.of(NFA.newUnionNFA(NFA.newSymbolNFA("a"), NFA.newSymbolNFA("b"))),
                Arguments.of(NFA.newKleeneClosureNFA(NFA.newSymbolNFA("a"))),
                Arguments.of(NFA.newZeroOrOneNFA(NFA.newSymbolNFA("a"))),
                Arguments.of(NFA.newOneOrMoreNFA(NFA.newSymbolNFA("a")))
        );
    }

    @ParameterizedTest
    @MethodSource("getArgsForTestGetMatchingPrefixThrowsIllegalArgumentExceptionForEmptySymbolSequence")
    void testGetMatchingPrefixThrowsIllegalArgumentExceptionForEmptySymbolSequence(NFA automaton) {
        Assertions.assertThatThrownBy(() -> {
            automaton.getMatchingPrefix(List.of());
        });
    }

    private static Stream<Arguments> getArgsForTestGetMatchingPrefixForEmptySymbolNFA() {
        final var matchResult = new MatchResult();
        matchResult.setMatchDetected(true);
        return Stream.of(
                Arguments.of(matchResult, List.of("b")),
                Arguments.of(matchResult, List.of("a", "x")),
                Arguments.of(matchResult, List.of("b", "x")),
                Arguments.of(matchResult, List.of("x", "a"))
        );
    }

    @ParameterizedTest
    @MethodSource("getArgsForTestGetMatchingPrefixForEmptySymbolNFA")
    void testGetMatchingPrefixForEmptySymbolNFA(MatchResult expected, List<String> symbols) {
        var automaton = NFA.newEmptySymbolNFA();
        var actual = automaton.getMatchingPrefix(symbols);
        Assertions.assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    private static Stream<Arguments> getArgsTestGetMatchingPrefixForSymbolNFA() {
        return Stream.of(
                Arguments.of(prepareMatchResult(true, "a"), List.of("a")),
                Arguments.of(prepareMatchResult(false), List.of("b")),
                Arguments.of(prepareMatchResult(true, "a"), List.of("a", "x")),
                Arguments.of(prepareMatchResult(false), List.of("b", "x")),
                Arguments.of(prepareMatchResult(false), List.of("x", "a"))
        );
    }

    @ParameterizedTest
    @MethodSource("getArgsTestGetMatchingPrefixForSymbolNFA")
    void testGetMatchingPrefixForSymbolNFA(MatchResult expected, List<String> symbols) {
        var automaton = NFA.newSymbolNFA("a");
        var actual = automaton.getMatchingPrefix(symbols);
        Assertions.assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    private static Stream<Arguments> getArgsForTestGetMatchesForUnionNFA() {
        return Stream.of(
                Arguments.of(prepareMatchResult(true, "a"), List.of("a")),
                Arguments.of(prepareMatchResult(true, "b"), List.of("b")),
                Arguments.of(prepareMatchResult(true, "a"), List.of("a", "x")),
                Arguments.of(prepareMatchResult(true, "b"), List.of("b", "x")),
                Arguments.of(prepareMatchResult(false), List.of("x", "a")),
                Arguments.of(prepareMatchResult(false), List.of("x", "b"))
        );
    }

    @ParameterizedTest
    @MethodSource("getArgsForTestGetMatchesForUnionNFA")
    void testGetMatchesForUnionNFA(MatchResult expected, List<String> symbols) {
        var automaton = NFA.newUnionNFA(NFA.newSymbolNFA("a"), NFA.newSymbolNFA("b"));
        var actual = automaton.getMatchingPrefix(symbols);
        Assertions.assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    private static Stream<Arguments> getArgsForTestGetMatchingPrefixForConcatenateNFA() {
        return Stream.of(
                Arguments.of(prepareMatchResult(true, "a", "b"), List.of("a", "b")),
                Arguments.of(prepareMatchResult(false), List.of("b")),
                Arguments.of(prepareMatchResult(false), List.of("a", "x")),
                Arguments.of(prepareMatchResult(false), List.of("b", "x")),
                Arguments.of(prepareMatchResult(false), List.of("x", "a")),
                Arguments.of(prepareMatchResult(false), List.of("x", "b"))
        );
    }

    @ParameterizedTest
    @MethodSource("getArgsForTestGetMatchingPrefixForConcatenateNFA")
    void testGetMatchingPrefixForConcatenateNFA(MatchResult expected, List<String> symbols) {
        var automaton = NFA.newConcatenateNFA(NFA.newSymbolNFA("a"), NFA.newSymbolNFA("b"));
        var actual = automaton.getMatchingPrefix(symbols);
        Assertions.assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    private static Stream<Arguments> getArgsForTestGetMatchignPrefixForClosureNFA() {
        return Stream.of(
                Arguments.of(prepareMatchResult(true, "a"), List.of("a", "b")),
                Arguments.of(prepareMatchResult(true), List.of("b")),
                Arguments.of(prepareMatchResult(true, "a", "a"), List.of("a", "a", "x")),
                Arguments.of(prepareMatchResult(true), List.of("b", "x")),
                Arguments.of(prepareMatchResult(true), List.of("x", "a")),
                Arguments.of(prepareMatchResult(true), List.of("x", "a", "a")),
                Arguments.of(prepareMatchResult(true), List.of("x", "b"))
        );
    }

    @ParameterizedTest
    @MethodSource("getArgsForTestGetMatchignPrefixForClosureNFA")
    void testGetMatchignPrefixForClosureNFA(MatchResult expected, List<String> symbols) {
        var automaton = NFA.newKleeneClosureNFA(NFA.newSymbolNFA("a"));
        var actual = automaton.getMatchingPrefix(symbols);
        Assertions.assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    private static Stream<Arguments> getArgsForTestGetMatchingPrefixForZeroOrOneNFA() {
        return Stream.of(
                Arguments.of(prepareMatchResult(true), List.of("b")),
                Arguments.of(prepareMatchResult(true, "a"), List.of("a", "x")),
                Arguments.of(prepareMatchResult(true, "a"), List.of("a", "a", "x")),
                Arguments.of(prepareMatchResult(true), List.of("b", "x")),
                Arguments.of(prepareMatchResult(true), List.of("x", "a")),
                Arguments.of(prepareMatchResult(true), List.of("x", "b"))
        );
    }

    @ParameterizedTest
    @MethodSource("getArgsForTestGetMatchingPrefixForZeroOrOneNFA")
    void testGetMatchingPrefixForZeroOrOneNFA(MatchResult expected, List<String> symbols) {
        var automaton = NFA.newZeroOrOneNFA(NFA.newSymbolNFA("a"));
        var actual = automaton.getMatchingPrefix(symbols);
        Assertions.assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    private static Stream<Arguments> getArgsForTestGetMatchingPrefixForOneOrMoreNFA() {
        return Stream.of(
                Arguments.of(prepareMatchResult(false), List.of("b")),
                Arguments.of(prepareMatchResult(true, "a"), List.of("a", "x")),
                Arguments.of(prepareMatchResult(true, "a", "a"), List.of("a", "a", "x")),
                Arguments.of(prepareMatchResult(false), List.of("b", "x")),
                Arguments.of(prepareMatchResult(false), List.of("x", "a")),
                Arguments.of(prepareMatchResult(false), List.of("x", "b"))
        );
    }

    @ParameterizedTest
    @MethodSource("getArgsForTestGetMatchingPrefixForOneOrMoreNFA")
    void testGetMatchingPrefixForOneOrMoreNFA(MatchResult expected, List<String> symbols) {
        var automaton = NFA.newOneOrMoreNFA(NFA.newSymbolNFA("a"));
        var actual = automaton.getMatchingPrefix(symbols);
        Assertions.assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    private static MatchResult prepareMatchResult(boolean matchDetected, String... matchedSymbols) {
        var result = new MatchResult();
        result.setMatchDetected(matchDetected);
        Arrays.asList(matchedSymbols).stream().sorted(Comparator.reverseOrder()).forEach(result::prependMatchingSymbol);

        return result;
    }
}
