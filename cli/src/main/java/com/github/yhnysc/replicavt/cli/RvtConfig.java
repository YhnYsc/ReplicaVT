package com.github.yhnysc.replicavt.cli;

import com.github.yhnysc.replicavt.agent.RvtChgDataEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.StringUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"com.github.yhnysc.replicavt"})
@Command(name = "rvt", description = "Replicate database table Via Trigger")
public class RvtConfig implements ApplicationRunner, ExitCodeGenerator {

    @Option(names={"--config"}, description = {"Config file of replicavt"})
    @Value("${config:}")
    private String _configFile;

    @Option(names={"--etcd-endpoints"}, description = {"Endpoint(s) of the etcd (IP_1:Port_1,IP_2:Port_2,...)"}, defaultValue = "127.0.0.1:2379")
    @Value("${etcd-endpoints:127.0.0.1:2379}")
    private String _etcdEndpoints;
    @Option(names={"--etcd-key"}, description = {"Key of the etcd"})
    @Value("${etcd-key:}")
    private String _etcdKey;
    @Option(names={"--etcd-cert"}, description = {"Certificate of the etcd"})
    @Value("${etcd-cert:}")
    private String _etcdCert;
    @Option(names={"--etcd-cacert"}, description = {"CA Cert of the etcd"})
    @Value("${etcd-cacert:}")
    private String _etcdCaCert;

    private int _exitCode;
    private final RvtChgDataEventProducer _producer;

    @Autowired
    public RvtConfig(final RvtChgDataEventProducer producer) {
        _producer = producer;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("RvtConfig started");
        final CommandLine cmdLine = new CommandLine(this);
        cmdLine.parseArgs(args.getNonOptionArgs().toArray(new String[]{}));

        // Validate Config file
        if(StringUtils.hasText(_configFile)){
            if(!Files.exists(Paths.get(_configFile))){
                cmdLine.getErr().println("Error: Config file '%s' not existed".formatted(_configFile));
                _exitCode = 2;//TODO: CLI Exit Code
                return;
            }
            if(!Files.isReadable(Paths.get(_configFile))){
                cmdLine.getErr().println("Error: Config file '%s' unreadable".formatted(_configFile));
                _exitCode = 2;//TODO: CLI Exit Code
                return;
            }
        }
    }
    @Override
    public int getExitCode() {
        return _exitCode;
    }
}
