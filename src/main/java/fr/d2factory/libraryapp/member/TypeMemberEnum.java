package fr.d2factory.libraryapp.member;

public enum TypeMemberEnum {
    STUDENT_NOT_FIRST_YEAR("STUDENT_NOT_FIRST_YEAR"),
    STUDENT_FIRST_YEAR("STUDENT_FIRST_YEAR"),
    RESIDENT("RESIDENT");

    private String value;

    private TypeMemberEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
