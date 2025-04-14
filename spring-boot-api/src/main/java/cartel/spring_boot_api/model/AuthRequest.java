package cartel.spring_boot_api.model;

public class AuthRequest {
    private String password;

    // Default constructor needed for JSON parsing
    public AuthRequest() {
    }

    public AuthRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
