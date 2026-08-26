package tools.pantheum.gaia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for {@link GaiaVersion} — build metadata loaded from gaia-version.properties. */
@DisplayName("GaiaVersion")
class GaiaVersionTest {

    @Test
    @DisplayName("VERSION is populated from the build")
    void versionPopulated() {
        assertNotNull(GaiaVersion.VERSION);
        assertFalse(GaiaVersion.VERSION.isBlank(), "VERSION must not be blank");
    }

    @Test
    @DisplayName("TIMESTAMP is populated from the build")
    void timestampPopulated() {
        assertNotNull(GaiaVersion.TIMESTAMP);
        assertFalse(GaiaVersion.TIMESTAMP.isBlank(), "TIMESTAMP must not be blank");
    }
}
