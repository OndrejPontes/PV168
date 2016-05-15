package security.db;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;
import org.springframework.transaction.annotation.Transactional;
import security.logic.impl.*;
import security.logic.model.*;

@Configuration
@EnableTransactionManagement
@PropertySource(value = { "classpath:conf.properties" })
public class SpringConfig {
    @Autowired
    private Environment environment;
    
    @Bean
    public DataSource dataSource(){
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(environment.getRequiredProperty("jdbc.url"));
        ds.setUsername(environment.getRequiredProperty("jdbc.user"));
        ds.setPassword(environment.getRequiredProperty("jdbc.password"));
        return ds;
    }
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public AgentModel agentModel() {
        return new AgentModelImpl(dataSource());
    }

    @Bean
    public ManagerModel managerModel() {
        return new ManagerModelImpl(dataSource());
    }

    @Bean
    public MissionModel missionModel() {
        return new MissionModelImpl(dataSource());
    }
}
