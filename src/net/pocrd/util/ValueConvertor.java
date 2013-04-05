package net.pocrd.util;

import java.util.HashMap;

public class ValueConvertor {
    public static interface Evaluater<TLeft, TRight> {
        void Evaluate(TLeft left, TRight right);
    }

    private static HashMap<String, Evaluater<?, ?>> cache = new HashMap<String, Evaluater<?, ?>>();

    @SuppressWarnings("unchecked")
    public static <TLeft, TRight> Evaluater<TLeft, TRight> getConvertor(Class<TLeft> leftClass, Class<TRight> rightClass) {
        String key = leftClass.getName() + "_" + rightClass.getName();
        Evaluater<TLeft, TRight> evaluater = (Evaluater<TLeft, TRight>)cache.get(key);
        if (evaluater == null) {
            synchronized (cache) {
                evaluater = (Evaluater<TLeft, TRight>)cache.get(key);
                if (evaluater == null) {
                    evaluater = createEvaluater(leftClass, rightClass);
                    cache.put(key, evaluater);
                }
            }
        }
        return evaluater;
    }

    private static <TLeft, TRight> Evaluater<TLeft, TRight> createEvaluater(Class<TLeft> leftClass, Class<TRight> rightClass) {
        return null;
    }
}
