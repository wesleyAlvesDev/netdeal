package br.com.netdeal.domain.util;

public class PasswordValidators {
    public static int passwordStrengthPercentage(String password) {
        int length = password.length();
        int uppercaseCount = 0;
        int lowercaseCount = 0;
        int numberCount = 0;
        int symbolCount = 0;
        int consecutiveUppercase = 0;
        int consecutiveLowercase = 0;
        int consecutiveNumbers = 0;

        char previousChar = 0;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                uppercaseCount++;
                if (previousChar != 0 && Character.isUpperCase(previousChar)) {
                    consecutiveUppercase++;
                }
            } else if (Character.isLowerCase(c)) {
                lowercaseCount++;
                if (previousChar != 0 && Character.isLowerCase(previousChar)) {
                    consecutiveLowercase++;
                }
            } else if (Character.isDigit(c)) {
                numberCount++;
                if (previousChar != 0 && Character.isDigit(previousChar)) {
                    consecutiveNumbers++;
                }
            } else {
                symbolCount++;
            }

            previousChar = c;
        }

        int score = 0;

        score += length * 4;

        score += (length - uppercaseCount) * 2;
        score += (length - lowercaseCount) * 2;
        score += numberCount * 4;
        score += symbolCount * 6;

        score += (length - (uppercaseCount + lowercaseCount + numberCount + symbolCount)) * 2;

        score -= consecutiveUppercase * 2;
        score -= consecutiveLowercase * 2;
        score -= consecutiveNumbers * 2;

        score -= countSequentialChars(password) * 3;

        score = Math.max(0, score);
        score = Math.min(100, score);

        return score;
    }

    private static int countSequentialChars(String password) {
        int sequentialCount = 0;
        int length = password.length();

        for (int i = 0; i < length - 2; i++) {
            if (isSequential(password.charAt(i), password.charAt(i + 1), password.charAt(i + 2))) {
                sequentialCount++;
            }
        }

        return sequentialCount;
    }

    private static boolean isSequential(char c1, char c2, char c3) {
        return (c1 + 1 == c2 && c2 + 1 == c3) || (c1 - 1 == c2 && c2 - 1 == c3);
    }
}
