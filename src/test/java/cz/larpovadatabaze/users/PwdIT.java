package cz.larpovadatabaze.users;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PwdIT {
    @Test
    public void creatingAndVerifyingPassword() {
        String hash = Pwd.generateStrongPasswordHash("aaa", "aaa@bbb.cz");

        boolean valid = Pwd.validatePassword("aaa", hash);
        assertEquals(valid, true);
    }
}
