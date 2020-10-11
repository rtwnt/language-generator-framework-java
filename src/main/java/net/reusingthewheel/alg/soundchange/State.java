package net.reusingthewheel.alg.soundchange;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A state in a finite state automaton.
 */
class State {

    private Boolean isFinal;
    private Map<String, State> symbolTransitions;
    private Set<State> emptySymbolTransitions;

    /**
     * Create a new instance with given value of isFinal flag.
     *
     * @param isFinal specifies whether the state is a final one of an automaton it occurs in.
     */
    State(Boolean isFinal) {
        this.isFinal = isFinal;
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
        return isFinal;
    }

    /**
     * Change the type of the state to final or non-final.
     *
     * @param isFinal a new value of the final state flag.
     */
    void setFinal(Boolean isFinal) {
        this.isFinal = isFinal;
    }

    Map<String, State> getSymbolTransitions() {
        return symbolTransitions;
    }

    Set<State> getEmptySymbolTransitions() {
        return emptySymbolTransitions;
    }
}
