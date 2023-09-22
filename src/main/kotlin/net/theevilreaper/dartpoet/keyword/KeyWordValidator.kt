package net.theevilreaper.dartpoet.keyword

import net.theevilreaper.dartpoet.DartModifier
import net.theevilreaper.dartpoet.ModifierTarget
import net.theevilreaper.dartpoet.util.toImmutableSet

internal val ALLOWED_PARAMETER_KEY_WORDS: Set<DartModifier> = DartModifier.entries.filter { it.containsTarget(ModifierTarget.PARAMETER) }.toImmutableSet()

fun hasAllowedParameterKeys(modifiers: Set<DartModifier>): Boolean {
    return checkKeyWords(modifiers, ALLOWED_PARAMETER_KEY_WORDS)
}

private fun checkKeyWords(modifiers: Set<DartModifier>, allowed: Set<DartModifier>): Boolean {
    return modifiers.isNotEmpty() && !allowed.containsAll(modifiers)
}