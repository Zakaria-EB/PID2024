package be.iccbxl.pid.reservationsspringboot.model;

public enum UserRole {
    ADMIN("admin"),
    MEMBER("member"),
    AFFILIATE("affiliate"),
    PRESS("press"),
    PRODUCER("producer");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getValue() {
        return role;
    }
}
