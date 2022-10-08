package cz.larpovadatabaze.users;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PwdIT {
    @Test
    public void creatingAndVerifyingPassword() {
        String hash = Pwd.generateStrongPasswordHash("aaa", "aaa@bbb.cz");

        boolean valid = Pwd.validatePassword("aaa", hash);
        assertThat(valid, is(true));
    }
}
