package net.pocrd.util;

public interface Evaluater<TLeft, TRight> {
    void evaluate(TLeft left, TRight right);
}
