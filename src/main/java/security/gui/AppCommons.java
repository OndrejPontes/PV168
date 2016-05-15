/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package security.gui;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Locale;
import javax.sql.DataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import security.db.SpringConfig;
import security.logic.model.*;

public abstract class AppCommons {
    protected static ApplicationContext appContext = new AnnotationConfigApplicationContext(SpringConfig.class);
    protected static MissionModel missionModel = appContext.getBean("missionModel", MissionModel.class);
    protected static AgentModel agentModel = appContext.getBean("agentModel", AgentModel.class);
    protected static ManagerModel managerModel = appContext.getBean("managerModel", ManagerModel.class);
    protected static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    protected static DataSource ds = appContext.getBean("dataSource",DataSource.class);
    
    public static DataSource getDataSource() {
        return ds;
    }
    
    public static DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public static ApplicationContext getAppContext() {
        return appContext;
    }

    public static MissionModel getMissionModel() {
        return missionModel;
    }

    public static AgentModel getAgentModel() {
        return agentModel;
    }

    public static ManagerModel getManagerModel() {
        return managerModel;
    }
}
