package com.example.notas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NotaValidatorTest {
    @Test
    fun titleShorterThanThreeCharactersIsRejected() {
        assertEquals(
            "El título debe tener entre 3 y 80 caracteres.",
            NotaValidator.validate("ab", "")
        )
    }

    @Test
    fun titleIsTrimmedBeforeValidation() {
        assertEquals(
            "El título debe tener entre 3 y 80 caracteres.",
            NotaValidator.validate("  a  ", "")
        )
    }

    @Test
    fun descriptionLongerThanThreeHundredCharactersIsRejected() {
        assertEquals(
            "La descripción no puede superar 300 caracteres.",
            NotaValidator.validate("Título válido", "a".repeat(301))
        )
    }

    @Test
    fun validTitleAndDescriptionAreAcceptedAtTheirLimits() {
        assertNull(NotaValidator.validate("a".repeat(80), "b".repeat(300)))
    }
}
