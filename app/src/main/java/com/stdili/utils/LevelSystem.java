package com.stdili.utils;

import com.stdili.models.User;

public class LevelSystem {

    public static int calculateLevel(int xp) {
        return xp / 100 + 1;
    }

    public static int xpForNextLevel(int currentLevel) {
        return currentLevel * 100;
    }

    public static int xpToNextLevel(int currentXp) {
        int currentLevel = calculateLevel(currentXp);
        return xpForNextLevel(currentLevel) - currentXp;
    }

    public static void addXp(User user, int xp) {
        user.setXp(user.getXp() + xp);
        user.setLevel(calculateLevel(user.getXp()));
    }

    public static void addCoins(User user, int coins) {
        user.setCoins(user.getCoins() + coins);
    }

    public static String getLevelTitle(int level) {
        if (level <= 5) return "Beginner";
        if (level <= 10) return "Intermediate";
        if (level <= 15) return "Advanced";
        if (level <= 20) return "Expert";
        return "Master";
    }

    public static int getXpForAction(String action) {
        switch (action) {
            case "study_session": return 10;
            case "complete_quiz": return 25;
            case "help_peer": return 15;
            case "create_group": return 20;
            case "daily_login": return 5;
            case "streak_bonus": return 50;
            default: return 0;
        }
    }

    public static int getCoinsForAction(String action) {
        switch (action) {
            case "study_session": return 5;
            case "complete_quiz": return 10;
            case "help_peer": return 8;
            case "create_group": return 15;
            case "daily_login": return 2;
            case "streak_bonus": return 25;
            default: return 0;
        }
    }
}