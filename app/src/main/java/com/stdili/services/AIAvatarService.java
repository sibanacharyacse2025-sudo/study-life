package com.stdili.services;

import android.util.Log;

public class AIAvatarService {
    private static final String TAG = "AIAvatarService";

    public enum Expression {
        THINKING, LISTENING, SPEAKING, CONCERNED, ENCOURAGING, HAPPY, NEUTRAL
    }

    private Expression currentExpression = Expression.NEUTRAL;

    public interface OnExpressionChangeListener {
        void onExpressionChanged(Expression expression);
    }

    private OnExpressionChangeListener listener;

    public void setExpression(Expression expression) {
        this.currentExpression = expression;
        Log.d(TAG, "Avatar expression changed to: " + expression);
        if (listener != null) {
            listener.onExpressionChanged(expression);
        }
    }

    public Expression getCurrentExpression() {
        return currentExpression;
    }

    public void setListener(OnExpressionChangeListener listener) {
        this.listener = listener;
    }

    public void updateExpressionFromMood(String mood) {
        switch (mood.toLowerCase()) {
            case "happy":
                setExpression(Expression.HAPPY);
                break;
            case "sad":
                setExpression(Expression.CONCERNED);
                break;
            case "stressed":
                setExpression(Expression.THINKING);
                break;
            case "confused":
                setExpression(Expression.LISTENING);
                break;
            default:
                setExpression(Expression.NEUTRAL);
        }
    }
}
