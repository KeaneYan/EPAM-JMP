package com.epam.mentoring.restapi;

import com.epam.mentoring.restapi.controller.EmployeeController;
import com.epam.mentoring.restapi.modal.Employee;
import com.epam.mentoring.restapi.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = EmployeeControllerTests.class)   //MockServletContext
@SpringBootApplication
public class EmployeeControllerTests {

    private static final Employee Eddard_Stark = new Employee("Eddard Stark", "1", 1L, LocalDate.of(2016, 1, 1));
    private static final Employee Arya_Stark = new Employee("Arya Stark", "2", 1L, LocalDate.of(2016, 1, 2));
    private static final Employee John_Snow = new Employee("John Snow", "3", 1L, LocalDate.of(2016, 1, 3));
    private static final Employee Daenerys_Targaryen = new Employee("Daenerys Targaryen", "4", 2L, null);

    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        initDB(context);
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testGetAll() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/employee").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    public void testGetById() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/employee/2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(2)))
                .andExpect(jsonPath("$.name", is("Arya Stark")))
                ;
    }

    @Test
    public void testGetByName() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/employee/name/Arya%20Stark").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code", is("2")))
                .andExpect(jsonPath("$[0].name", is("Arya Stark")))
        ;
    }

    @Test
    public void testCreate() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/employee")
                .content(toJson(Daenerys_Targaryen))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(4)))
        ;
    }
    @Test
    public void testUpdateExistingEmployee() throws Exception {
        Eddard_Stark.setName("Eddard Stark(Died)");
        Eddard_Stark.setOnBoardDate(null);
        mvc.perform(MockMvcRequestBuilders.put("/employee/1")
                .content(toJson(Eddard_Stark))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", is("Eddard Stark(Died)")))
        ;
    }
    @Test
     public void testUpdateWhenNotExising() throws Exception {
        mvc.perform(MockMvcRequestBuilders.put("/employee/5")
                .content(toJson(Daenerys_Targaryen))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        ;
    }

    @Test
    public void testDelete() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/employee/id/3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(3)))
                .andExpect(jsonPath("$.name", is("John Snow")))
        ;
        mvc.perform(MockMvcRequestBuilders.delete("/employee/3")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        ;
        mvc.perform(MockMvcRequestBuilders.get("/employee/id/3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
        ;
    }

    private static void initDB(ApplicationContext ctx) {
        EmployeeRepository employeeRepository = ctx.getBean(EmployeeRepository.class);
        employeeRepository.save(Eddard_Stark);
        employeeRepository.save(Arya_Stark);
        employeeRepository.save(John_Snow);
    }

    private String toJson(Object object) throws Exception {
        ObjectMapper map = new ObjectMapper();
        return map.writeValueAsString(object);
    }
}
