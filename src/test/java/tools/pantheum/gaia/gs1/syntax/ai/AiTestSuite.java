package tools.pantheum.gaia.gs1.syntax.ai;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Aggregates all per-AI test classes into a single runnable suite.
 *
 * <p>Every test class in {@code tools.pantheum.gaia.gs1.syntax.ai} is picked up
 * automatically via {@link SelectPackages}, so new AI test classes are included
 * without any change to this file.
 *
 * <p>Run this class directly from your IDE to execute the complete AI test
 * battery in one pass, or use {@code mvn test} to run it as part of the
 * standard build.
 */
@Suite
@SuiteDisplayName("GS1 Application Identifier Tests")
@SelectPackages("tools.pantheum.gaia.gs1.syntax.ai")
public class AiTestSuite {
}
