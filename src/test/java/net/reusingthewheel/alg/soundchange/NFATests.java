package net.reusingthewheel.alg.soundchange;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    @CsvSource({"true,a", "true,", "false,b", "false,ax", "false,xa"})
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
}
