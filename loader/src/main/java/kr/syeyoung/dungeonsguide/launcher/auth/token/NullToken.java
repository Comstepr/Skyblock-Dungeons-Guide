package kr.syeyoung.dungeonsguide.launcher.auth.token;

import java.security.KeyPair;
import java.time.Instant;

public class NullToken implements AuthToken {
    @Override
    public boolean isUserVerified() {
        return false;
    }

    @Override
    public boolean hasFullCapability() {
        return false;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public Instant getExpiryInstant() {
        return Instant.MIN;
    }

    @Override
    public String getToken() {
        return null;
    }
}