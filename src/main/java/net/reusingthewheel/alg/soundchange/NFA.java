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
}
