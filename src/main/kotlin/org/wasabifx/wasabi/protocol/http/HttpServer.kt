package org.wasabifx.wasabi.protocol.http

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http2.Http2SecurityUtil
import io.netty.handler.ssl.*
import io.netty.handler.ssl.util.SelfSignedCertificate
import org.wasabifx.wasabi.app.AppServer
import org.wasabifx.wasabi.app.configuration
import org.wasabifx.wasabi.core.NettyPipelineInitializer
import java.io.FileInputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.TrustManagerFactory


class HttpServer(private val appServer: AppServer) {

    val bootstrap: ServerBootstrap
    val primaryGroup : NioEventLoopGroup
    val workerGroup :  NioEventLoopGroup
    val sslEnabled: Boolean = configuration!!.sslEnabled
    val sslCertificatePath = configuration!!.sslCertificatePath
    var sslContext: SslContext? = null

    init {

        // Setup SSL if we are using it.
        initialiseSsl()

        // Define worker groups
        primaryGroup = NioEventLoopGroup()
        workerGroup = NioEventLoopGroup()

        // Initialize bootstrap of server
        bootstrap = ServerBootstrap()

        bootstrap.group(primaryGroup, workerGroup)
        bootstrap.channel(NioServerSocketChannel::class.java)
        bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        bootstrap.childHandler(NettyPipelineInitializer(appServer, sslContext))

    }

    private fun initialiseSsl() {
        if (sslEnabled) {
            // Check for ALPN support in openssl, fallback to JDK implementation if not.
            val provider = if (OpenSsl.isAlpnSupported()) SslProvider.OPENSSL else SslProvider.JDK

            //var certificate : X509Certificate?

            // If the default empty string has been replaced in the wasabi configuration
            // attempt to read the file path into a stream.
            if (!sslCertificatePath.contentEquals("")) {

                val fileStream = FileInputStream(sslCertificatePath);

                val certificateFactory = CertificateFactory.getInstance("X.509");
                val caCertificate = certificateFactory.generateCertificate(fileStream);

                val trustManagerFactory = TrustManagerFactory
                        .getInstance(TrustManagerFactory.getDefaultAlgorithm());
                val keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

                // The keystore instance does't explicitly need to be derived from a
                // file.
                keyStore.load(null);
                keyStore.setCertificateEntry("caCert", caCertificate);

                trustManagerFactory.init(keyStore);

                // val sslContext = SSLContext.getInstance("TLS");
                // sslContext.init(null, trustManagerFactory.trustManagers, null);
            }
            // TODO Need to load real certificate....s


            val certificate = SelfSignedCertificate()
            this.sslContext = SslContextBuilder.forServer(certificate.certificate(), certificate.privateKey())
                    .sslProvider(provider).ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                    .applicationProtocolConfig(ApplicationProtocolConfig(ApplicationProtocolConfig.Protocol.ALPN, ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE, ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT
                            , ApplicationProtocolNames.HTTP_2, ApplicationProtocolNames.HTTP_1_1)).build()

        }
    }

    fun start(wait: Boolean = true) {
        val channel = bootstrap.bind(appServer.configuration.port)?.sync()?.channel()

        if (wait) {
            channel?.closeFuture()?.sync()
        }
    }

    fun stop() {

        // Shutdown all event loops
        primaryGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()

        // Wait till all threads are terminated
        primaryGroup.terminationFuture().sync()
        workerGroup.terminationFuture().sync()

    }


}