package net.pocrd.define;

public interface Evaluater<TLeft, TRight> {
    void evaluate(TLeft left, TRight right);
}
