package net.reusingthewheel.alg.soundchange;

import java.util.ArrayList;
import java.util.List;

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
        var start = new State();
        var end = new State();
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
        var start = new State();
        var end = new State();
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
        var start = new State();
        start.addEmptySymbolTransition(first.start);
        start.addEmptySymbolTransition(second.start);

        var end = new State();
        first.end.addEmptySymbolTransition(end);
        second.end.addEmptySymbolTransition(end);

        return new NFA(start, end);
    }

    /**
     * Create an automaton that reaches its final state if the given automaton does it 0 or more times.
     *
     * @param automaton the given automaton
     * @return an instance of NFA
     */
    public static NFA newKleeneClosureNFA(NFA automaton) {
        var start = new State();
        var end = new State();

        start.addEmptySymbolTransition(automaton.start);
        automaton.end.addEmptySymbolTransition(end);
        automaton.end.addEmptySymbolTransition(automaton.start);
        start.addEmptySymbolTransition(end);

        return new NFA(start, end);
    }

    /**
     * Check if the automaton reaches its final state after consuming all of the given symbols.
     *
     * @param symbols a list of symbols.
     * @return true if the final state has been reached.
     */
    public boolean isMatch(List<String> symbols) {
        List<State> currentStates = new ArrayList<>();
        addNextState(this.start, currentStates, new ArrayList<>());


        for (String s : symbols) {
            List<State> nextStates = new ArrayList<>();

            currentStates.forEach( cs -> {
                var nextState = cs.getSymbolTransitions().get(s);
                if (nextState != null) {
                    addNextState(nextState, nextStates, new ArrayList<>());
                }
            });

            currentStates = nextStates;
        }

        return currentStates.stream().anyMatch(State::isFinal);
    }

    private void addNextState(State state, List<State> nextStates, List<State> visited) {
        if (state.getEmptySymbolTransitions().isEmpty()) {
            nextStates.add(state);
            return;
        }

        state.getEmptySymbolTransitions().forEach(s -> {
            if (!visited.contains(s)) {
                visited.add(s);
                addNextState(s, nextStates, visited);
            }
        });
    }

}
