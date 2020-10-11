package net.reusingthewheel.alg.soundchange;

/**
 * A nondeterministic finite state automaton
 */
public class NFA {
    private State start;
    private State end;

    private NFA(State start, State end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Create an automaton that allows for a transition without consuming any symbol.
     *
     * @return an instance of NFA
     */
    public static NFA newEmptySymbolNFA() {
        var start = new State(false);
        var end = new State(true);
        start.addEmptySymbolTransition(end);

        return new NFA(start, end);
    }

    /**
     * Create an automaton that allows for a transition only after consuming the given symbol.
     *
     * @param symbol a symbol necessary to be consumed for the automaton to reach its end state.
     * @return an instance of NFA
     */
    public static NFA newSymbolNFA(String symbol) {
        var start = new State(false);
        var end = new State(true);
        start.addSymbolTransitions(symbol, end);

        return new NFA(start, end);
    }

    /**
     * Create an automaton that reaches its final state if the first and then the second does.
     *
     * @param first the first automaton
     * @param second the second automaton
     * @return an instance of NFA
     */
    public static NFA newConcatenateNFA(NFA first, NFA second) {
        first.end.addEmptySymbolTransition(second.start);
        first.end.setFinal(false);

        return new NFA(first.start, second.end);
    }

    /**
     * Create an automaton that reaches its final state if the first or the second does.
     *
     * @param first the first automaton
     * @param second the second automaton
     * @return an instance of NFA
     */
    public static NFA newUnionNFA(NFA first, NFA second) {
        var start = new State(false);
        start.addEmptySymbolTransition(first.start);
        start.addEmptySymbolTransition(second.start);

        var end = new State(true);
        first.end.addEmptySymbolTransition(end);
        first.end.setFinal(false);
        second.end.addEmptySymbolTransition(end);
        second.end.setFinal(false);

        return new NFA(start, end);
    }

    /**
     * Create an automaton that reaches its final state if the given automaton does it 0 or more times.
     *
     * @param automaton the given automaton
     * @return an instance of NFA
     */
    public static NFA newKleeneClosureNFA(NFA automaton) {
        var start = new State(false);
        var end = new State(true);

        start.addEmptySymbolTransition(automaton.start);
        automaton.end.addEmptySymbolTransition(end);
        automaton.end.addEmptySymbolTransition(automaton.start);
        start.addEmptySymbolTransition(end);

        return new NFA(start, end);
    }
}
