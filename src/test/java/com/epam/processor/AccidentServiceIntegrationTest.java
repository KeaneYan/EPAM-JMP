package com.epam.processor;

import com.epam.db.HsqlInit;
import com.epam.dbservice.AccidentService;
import com.epam.entities.Accident;
import org.hamcrest.core.IsEqual;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-config.xml")
public class AccidentServiceIntegrationTest {

    private static Connection connection;
    private static final HsqlInit hsqlInit = new HsqlInit();
    @Autowired
    public AccidentService accidentService;

    @BeforeClass
    public static void starDatabase(){
        connection = HsqlInit.initDatabase();
        hsqlInit.initTablesFromFiles(connection);
    }

    @AfterClass
    public static void shutdownDatabase() throws SQLException {
        connection.close();
        hsqlInit.stopDatabase();
    }

    @Test
    public void testFindByAccidentId() throws Exception {
        Accident accident = accidentService.findOne("200901BS70001");
        assertThat(accident.getLongitude(), IsEqual.equalTo("-0.201349"));
        assertThat(accident.getLatitude(), IsEqual.equalTo("51.512273"));
    }

    @Test
    public void testWhenRoadConditionAsDry() throws Exception{
        String roadConditionDry = "1";
        int countOfAccidentInDryRoadCondition = accidentService.getAllAccidentsByRoadCondition(roadConditionDry).size();
        assertThat(countOfAccidentInDryRoadCondition, IsEqual.equalTo(70315));
    }

    @Test
    public void testWhenRoadConditionAsWet() throws Exception{
        String roadConditionWet = "2";
        int countOfAccidentInWetRoadCondition = accidentService.getAllAccidentsByRoadCondition(roadConditionWet).size();
        assertThat(countOfAccidentInWetRoadCondition, IsEqual.equalTo(28044));
    }

    @Test
    public void testWhenWeathConditionAsRainingWithoutHighWindAndYearIn2009() throws Exception{
        String weathConditionRainingWithHighWind = "2";
        int countOfAccidentInWeathConditionAsRainingWithoutHighWindAndYearIn2009 = accidentService.getAllAccidentsByWeatherConditionAndYear(weathConditionRainingWithHighWind, "2009").size();
        assertThat(countOfAccidentInWeathConditionAsRainingWithoutHighWindAndYearIn2009, IsEqual.equalTo(12495));
    }

    @Test
    public void testWhenWeathConditionAsRainingWithHighWindAndYearIn2009() throws Exception{
        String weathConditionRainingWithHighWind = "5";
        int countOfAccidentInWeathConditionAsRainingWithHighWindAndYearIn2009 = accidentService.getAllAccidentsByWeatherConditionAndYear(weathConditionRainingWithHighWind, "2009").size();
        assertThat(countOfAccidentInWeathConditionAsRainingWithHighWindAndYearIn2009, IsEqual.equalTo(1387));
    }

    @Test
    public void testFindAccidentsByDate() throws Exception{
        Date date20090107 = new SimpleDateFormat("yyyy-MM-dd").parse("2009-01-07");
        List<Accident> accidentsOn20090107 = accidentService.getAllAccidentsByDate(date20090107);
        assertThat(accidentsOn20090107.size(), IsEqual.equalTo(201));

        Accident accidentOn20090107 = accidentService.findOne("200901BS70009");
        assertThat(accidentOn20090107.getTime(), IsEqual.equalTo("AFTERNOON"));
    }

}