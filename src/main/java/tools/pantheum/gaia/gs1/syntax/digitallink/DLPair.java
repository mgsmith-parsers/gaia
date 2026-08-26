package tools.pantheum.gaia.gs1.syntax.digitallink;

import tools.pantheum.gaia.gs1.constants.GS1Constants;

/**
 * An AI/value pair extracted from a GS1 Digital Link URI, together with the
 * {@link GS1Constants.DigitalLinkAIType} role it plays (primary key, key
 * qualifier, or data attribute). Used by {@link DLSyntaxParser}.
 */
final class DLPair {

    final String ai;
    final String value;
    final GS1Constants.DigitalLinkAIType role;

    DLPair(String ai, String value, GS1Constants.DigitalLinkAIType role) {
        this.ai    = ai;
        this.value = value;
        this.role  = role;
    }
}
