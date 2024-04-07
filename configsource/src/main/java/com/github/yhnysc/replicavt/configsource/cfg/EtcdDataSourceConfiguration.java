package com.github.yhnysc.replicavt.configsource.cfg;

import com.github.yhnysc.replicavt.configsource.entity.RvtNodeConfig;
import com.github.yhnysc.replicavt.configsource.entity.RvtNodeConfigKey;
import com.github.yhnysc.replicavt.configsource.repo.RvtNodeConfigRepository;
import com.github.yhnysc.replicavt.configsource.utils.SslUtil;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Lazy
@Configuration
public class EtcdDataSourceConfiguration {

    @Value("${config}")
    private String _configFilePath;

    //TODO: Set the value from env/properties/cli
    @Value("${etcd-namespace:rvt/}")
    private String _etcdNs;

    //TODO: Set the value from env/properties/cli
    @Value("${etcd-conn-timeout:10}")
    private String _etcdConnTimeout;

    @Value("${etcd-endpoints:}")
    private String _etcdEndpoints;

    @Value("${etcd-key:}")
    private String _etcdKey;

    @Value("${etcd-cert:}")
    private String _etcdCert;

    @Value("${etcd-cacert:}")
    private String _etcdCaCert;

    //TODO: Set the value from env/properties/cli
    @Value("${rvt.node-name:MASTER-NODE}")
    private String _nodeName;

    @Bean
    public Client etcdClient() {
        // If specified --config, use the content in config file to override option's value
        if(StringUtils.hasText(_configFilePath)){
            readConfigFile();
        }
        final List<URI> uriList = new ArrayList<>();
        Arrays.stream(_etcdEndpoints.split(",")).forEach(s -> {
            uriList.add(URI.create("https://"+s));//TODO: Support http://
        });
        final ClientBuilder builder = Client.builder()
                .endpoints(uriList)
                .connectTimeout(Duration.ofSeconds(Integer.parseInt(_etcdConnTimeout)))
                .namespace(ByteSequence.from(_etcdNs.getBytes()));
        try {
            // Inject CA cert, Client key and Client certificate
            final SslContextBuilder sslCtxBuilder = GrpcSslContexts.forClient();
            boolean isSslContextUsed = false;
            if(StringUtils.hasText(_etcdCaCert)){
                final X509Certificate etcdCaCert = SslUtil.loadCert(_etcdCaCert);
                sslCtxBuilder.trustManager(etcdCaCert);
                isSslContextUsed = true;
            }
            if (StringUtils.hasText(_etcdKey) && StringUtils.hasText(_etcdCert)) {
                final PrivateKey etcdKey = SslUtil.loadKey(_etcdKey);
                final X509Certificate etcdCert = SslUtil.loadCert(_etcdCert);
                sslCtxBuilder.keyManager(etcdKey, etcdCert);
                isSslContextUsed = true;
            }
            if(isSslContextUsed){
                builder.sslContext(sslCtxBuilder.build());
            }
            return builder.build();
        } catch (SSLException | GeneralSecurityException e) {
            throw new RuntimeException(e);
            //TODO: Output the error
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Primary
    @Bean
    @DependsOn({"etcdClient", "gson", "rvtNodeConfigRepository"})
    public RvtNodeConfig dataSourceFromConfig(@Qualifier("etcdClient") Client etcdCli, @Qualifier("rvtNodeConfigRepository") RvtNodeConfigRepository nodeConfigRepo){
        if (!StringUtils.hasText(_nodeName)){
            return null;
        }
        return nodeConfigRepo.findOne(new RvtNodeConfigKey(_nodeName)).orElse(null);
    }

    @SneakyThrows
    private void readConfigFile(){
        if(Files.isReadable(Paths.get(_configFilePath))){
            final Properties prop = new Properties();
            prop.load(new FileInputStream(_configFilePath));
            _etcdEndpoints  = prop.getProperty("etcd-endpoints");
            _etcdKey        = prop.getProperty("etcd-key");
            _etcdCert       = prop.getProperty("etcd-cert");
            _etcdCaCert     = prop.getProperty("etcd-cacert");
        }
    }

}
