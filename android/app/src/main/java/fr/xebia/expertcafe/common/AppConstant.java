package fr.xebia.expertcafe.common;

public interface AppConstant {

    enum Time {
        TEN_0,
        TEN_1,
        TEN_2,
        TEN_3,

        ELEVEN_0,
        ELEVEN_1,
        ELEVEN_2,
        ELEVEN_3,

        TWELVE_0,
        TWELVE_1,
        TWELVE_2,
        TWELVE_3,

        ONE_0,
        ONE_1,

        TWO_0,
        TWO_1,
        TWO_2,
        TWO_3,

        THREE_0,
        THREE_1,
        THREE_2,
        THREE_3,

        FOUR_0,
        FOUR_1,
        FOUR_2,
        FOUR_3,

        FIVE_0,
        FIVE_1,
        FIVE_2,
        FIVE_3,

        SIX_0,
        SIX_1,

        UNKNOWN;

        public static Time from(String fromTime) {
            Time time;
            try {
                time = Time.valueOf(fromTime);
            } catch (IllegalArgumentException e) {
                time = UNKNOWN;
            }
            return time;
        }
    }

}
