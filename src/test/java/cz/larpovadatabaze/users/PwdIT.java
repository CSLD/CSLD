package cz.larpovadatabaze.users;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PwdIT {
    @Test
    public void creatingAndVerifyingPassword() {
        String hash = "1000:616161406262622e637a:ec1473f8cc336460bf9d1dd30f77b84d20f4931fc1b1ca926a183996f78b8cf3e242ed6b655f69abe61a934cdea57d41378808d4cf04108025768ede4f82bf7a"; // Pwd.generateStrongPasswordHash("aaa", "aaa@bbb.cz");

        boolean valid = Pwd.validatePassword("aaa", hash);
        assertEquals(valid, true);
    }
}
