package net.reusingthewheel.alg.soundchange;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A state in a finite state automaton.
 */
class State {

    private Map<String, State> symbolTransitions;
    private Set<State> emptySymbolTransitions;

    State() {
        this.symbolTransitions = new HashMap<>();
        this.emptySymbolTransitions = new HashSet<>();
    }

    /**
     * Add a symbol-based transition to another state.
     *
     * @param symbol a symbol that needs to be consumed to move from this state to the next state.
     * @param to the next state for given symbol.
     */
    void addSymbolTransitions(String symbol, State to) {
        this.symbolTransitions.put(symbol, to);
    }

    /**
     * Add an empty symbol-based transition to another state.
     *
     * @param to the next state.
     */
    void addEmptySymbolTransition(State to) {
        this.emptySymbolTransitions.add(to);
    }

    /**
     * Check if the state is a final one of an automaton it occurs in.
     *
     * @return true if it is a final state.
     */
    Boolean isFinal() {
        return symbolTransitions.isEmpty() && emptySymbolTransitions.isEmpty();
    }

    Map<String, State> getSymbolTransitions() {
        return symbolTransitions;
    }

    Set<State> getEmptySymbolTransitions() {
        return emptySymbolTransitions;
    }
}
