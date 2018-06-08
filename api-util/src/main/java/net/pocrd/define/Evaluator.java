package net.pocrd.define;

public interface Evaluator<TLeft, TRight> {
    void evaluate(TLeft left, TRight right);
}
