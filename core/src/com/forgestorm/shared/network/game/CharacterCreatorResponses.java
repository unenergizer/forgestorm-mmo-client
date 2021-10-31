package com.forgestorm.shared.network.game;

@SuppressWarnings("unused")
public enum CharacterCreatorResponses {

    SUCCESS,
    FAIL_BLACKLIST_NAME,
    FAIL_NAME_TAKEN,
    FAIL_TOO_MANY_CHARACTERS;

    public static CharacterCreatorResponses getCharacterErrorType(byte characterErrors) {
        for (CharacterCreatorResponses error : CharacterCreatorResponses.values()) {
            if ((byte) error.ordinal() == characterErrors) {
                return error;
            }
        }
        return null;
    }

    public byte getCharacterCreatorResponsesByte() {
        return (byte) this.ordinal();
    }
}
