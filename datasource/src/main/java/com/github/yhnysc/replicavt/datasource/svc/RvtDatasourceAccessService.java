package com.github.yhnysc.replicavt.datasource.svc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class RvtDatasourceAccessService {
    private final JdbcTemplate _jdbcTmpl;
    @Autowired
    public RvtDatasourceAccessService(JdbcTemplate jdbcTmpl){
        _jdbcTmpl = jdbcTmpl;
    }


}
