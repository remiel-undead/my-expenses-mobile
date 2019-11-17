package ru.neofusion.undead.myexpenses.repository.network

import android.content.Context
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import ru.neofusion.undead.myexpenses.R
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.*
import javax.net.ssl.*

object OkHttpClientProvider {
    fun getClient(context: Context) = createClient(context)

    private fun createClient(context: Context): OkHttpClient {
        val spec = ConnectionSpec.MODERN_TLS
        val trustManager = trustManagerForCertificates(context)
        val keyManager = keyManagerForCertificates(context)
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(arrayOf<KeyManager>(keyManager), arrayOf<TrustManager>(trustManager), null)
        return OkHttpClient.Builder()
            .connectionSpecs(Collections.singletonList(spec))
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .build()
    }

    private fun trustManagerForCertificates(context: Context): X509TrustManager {
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificate =
            certificateFactory.generateCertificate(context.resources.openRawResource(R.raw.ca))
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        val password = "password".toCharArray()
        keyStore.load(null, password)
        keyStore.setCertificateEntry("CA", certificate)
        val keyManagerFactory =
            KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, password)
        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)
        val trustManagers = trustManagerFactory.trustManagers
        check(trustManagers.size == 1 && trustManagers[0] is X509TrustManager) {
            "Unexpected default trust managers: ${Arrays.toString(trustManagers)}"
        }
        return trustManagers[0] as X509TrustManager
    }

    private fun keyManagerForCertificates(context: Context): X509KeyManager {
        val keyStore = KeyStore.getInstance("PKCS12")
        val password = "password".toCharArray()
        keyStore.load(context.resources.openRawResource(R.raw.client), "".toCharArray())
        val keyManagerFactory = KeyManagerFactory.getInstance("X509")
        keyManagerFactory.init(keyStore, password)
        val keyManagers = keyManagerFactory.keyManagers
        check(keyManagers.size == 1 && keyManagers[0] is X509KeyManager) {
            "Unexpected default key managers: ${Arrays.toString(keyManagers)}"
        }
        return keyManagers[0] as X509KeyManager
    }
}
