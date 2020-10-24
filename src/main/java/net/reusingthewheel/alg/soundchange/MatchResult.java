package net.reusingthewheel.alg.soundchange;

import java.util.ArrayList;
import java.util.List;

public class MatchResult {
    private boolean matchDetected;
    private List<String> matchedSymbols = new ArrayList<>();

    boolean isMatchDetected() {
        return matchDetected;
    }

    public void setMatchDetected(boolean matchDetected) {
        this.matchDetected = matchDetected;
    }

    List<String> getMatchedSymbols() {
        return matchedSymbols;
    }

    public void prependMatchingSymbol(String symbol) {
        this.matchedSymbols.add(0, symbol);
    }
}
