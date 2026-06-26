package com.aks.offvault.data.backup

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object BackupCrypto {

    private const val SALT_LEN = 16
    private const val IV_LEN = 12
    private const val KEY_LEN_BITS = 256
    private const val PBKDF2_ITERATIONS = 310_000
    private const val GCM_TAG_BITS = 128

    fun encrypt(plaintext: ByteArray, passphrase: String): ByteArray {
        val salt = ByteArray(SALT_LEN).also { SecureRandom().nextBytes(it) }
        val iv = ByteArray(IV_LEN).also { SecureRandom().nextBytes(it) }
        val key = deriveKey(passphrase, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))
        val ciphertext = cipher.doFinal(plaintext)

        // Layout: [salt 16B][iv 12B][ciphertext + 16B GCM tag]
        return salt + iv + ciphertext
    }

    fun decrypt(data: ByteArray, passphrase: String): ByteArray {
        require(data.size > SALT_LEN + IV_LEN) { "Data too short to be a valid backup" }

        val salt = data.copyOfRange(0, SALT_LEN)
        val iv = data.copyOfRange(SALT_LEN, SALT_LEN + IV_LEN)
        val ciphertext = data.copyOfRange(SALT_LEN + IV_LEN, data.size)
        val key = deriveKey(passphrase, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))
        return cipher.doFinal(ciphertext)
    }

    private fun deriveKey(passphrase: String, salt: ByteArray): SecretKeySpec {
        val spec = PBEKeySpec(passphrase.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LEN_BITS)
        val raw = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        spec.clearPassword()
        return SecretKeySpec(raw, "AES")
    }
}
